package com.github.alexgaard.tracy.stack_trace;

import com.github.alexgaard.tracy.source_map.CachedSourceMapRetriever;
import com.github.alexgaard.tracy.source_map.ParsedSourceMap;
import com.github.alexgaard.tracy.source_map.RawSourceMapRetriever;
import com.github.alexgaard.tracy.source_map.SourceMapRetriever;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class StackTraceSourceMapper {

    private static final Logger log = LoggerFactory.getLogger(StackTraceSourceMapper.class);

    private final SourceMapRetriever sourceMapRetriever;

    private final StackFrameResolver stackFrameResolver;

    private final StackTraceParser stackTraceParser;


    public StackTraceSourceMapper(RawSourceMapRetriever rawSourceMapRetriever) {
        stackTraceParser = new BaseStackTraceParser();

        sourceMapRetriever = new CachedSourceMapRetriever(rawSourceMapRetriever, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(6))
                .build());

        stackFrameResolver = new CachedStackFrameResolver(new BaseStackFrameResolver(false), Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(6))
                .build());
    }

    public String applySourceMap(String minifiedStackTrace) {
        Map<String, StackFrame> stackFrames = stackTraceParser.parse(minifiedStackTrace);

        String stackTrace = minifiedStackTrace;

        for (Map.Entry<String, StackFrame> entry : stackFrames.entrySet()) {
            String stackFrameStr = entry.getKey();
            StackFrame minifiedStackFrame = entry.getValue();

            Optional<ParsedSourceMap> maybeSourceMap = sourceMapRetriever.getSourceMap(minifiedStackFrame.file);

            if (maybeSourceMap.isEmpty()) {
                continue;
            }

            ParsedSourceMap sourceMap = maybeSourceMap.get();

            if (sourceMap.version != 3) {
                log.warn("Unable to use source maps with other version than 3. Skipping applying of source map to stack frame");
                continue;
            }

            Optional<StackFrame> maybeStackFrame = stackFrameResolver.resolve(minifiedStackFrame, maybeSourceMap.get());

            if (maybeStackFrame.isEmpty()) {
                continue;
            }

            StackFrame resolvedStackFrame = maybeStackFrame.get();

            boolean hadFunctionNameOriginally = minifiedStackFrame.functionName != null && resolvedStackFrame.functionName == null;

            stackTrace = stackTrace.replace(stackFrameStr, toSourceFrameString(resolvedStackFrame, hadFunctionNameOriginally));
        }

        return stackTrace;
    }

    public static String toSourceFrameString(StackFrame sourceFrame, boolean hadFunctionNameOriginally) {
        String fileLineCol = sourceFrame.file + ":" + sourceFrame.line + ":" + sourceFrame.col;

        if (sourceFrame.functionName != null) {
            return "at " + sourceFrame.functionName + " (" + fileLineCol + ")";
        }

        if (hadFunctionNameOriginally) {
            return "at " + fileLineCol;
        }

        return fileLineCol;
    }

}
