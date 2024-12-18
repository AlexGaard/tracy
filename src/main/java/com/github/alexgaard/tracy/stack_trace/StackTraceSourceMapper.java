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
import java.util.Objects;
import java.util.Optional;

public class StackTraceSourceMapper {

    private static final Logger log = LoggerFactory.getLogger(StackTraceSourceMapper.class);

    private final SourceMapRetriever sourceMapRetriever;

    private final StackFrameResolver stackFrameResolver;

    private final StackTraceParser stackTraceParser;

    public StackTraceSourceMapper(
            SourceMapRetriever sourceMapRetriever,
            StackFrameResolver stackFrameResolver,
            StackTraceParser stackTraceParser
    ) {
        this.sourceMapRetriever = sourceMapRetriever;
        this.stackFrameResolver = stackFrameResolver;
        this.stackTraceParser = stackTraceParser;
    }

    public StackTraceSourceMapper(RawSourceMapRetriever rawSourceMapRetriever) {
        stackTraceParser = new BaseStackTraceParser();

        sourceMapRetriever = new CachedSourceMapRetriever(rawSourceMapRetriever, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(6))
                .maximumSize(1000)
                .build());

        stackFrameResolver = new CachedStackFrameResolver(new BaseStackFrameResolver(false), Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(6))
                .maximumSize(100_000)
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

            stackTrace = stackTrace.replace(stackFrameStr, createSourceFrameString(resolvedStackFrame));
        }

        return stackTrace;
    }

    private static String createSourceFrameString(StackFrame sourceFrame) {
        return "at " + sourceFrame.functionName + " (" + sourceFrame.file + ":" + sourceFrame.line + ":" + sourceFrame.col + ")";
    }

}
