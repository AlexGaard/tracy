package com.github.alexgaard.tracy.stack_trace;

import com.github.alexgaard.tracy.source_map.CachedSourceMapRetriever;
import com.github.alexgaard.tracy.source_map.ParsedSourceMap;
import com.github.alexgaard.tracy.source_map.RawSourceMapRetriever;
import com.github.alexgaard.tracy.source_map.SourceMapRetriever;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class StackTraceSourceMapper {

    private final SourceMapRetriever sourceMapRetriever;

    private final StackFrameResolver stackFrameResolver;

    private final StackTraceParser stackTraceParser;


    public StackTraceSourceMapper(RawSourceMapRetriever rawSourceMapRetriever) {
        stackTraceParser = new StackTraceParserImpl();

        sourceMapRetriever = new CachedSourceMapRetriever(rawSourceMapRetriever, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(6))
                .build());

        stackFrameResolver = new CachedStackFrameResolver(new BaseStackFrameResolver(), Caffeine.newBuilder()
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

            Optional<StackFrame> maybeStackFrame = stackFrameResolver.resolve(minifiedStackFrame, maybeSourceMap.get());

            if (maybeStackFrame.isEmpty()) {
                continue;
            }

            stackTrace = stackTrace.replaceAll(stackFrameStr, maybeStackFrame.get().toFrameString());
        }

        return stackTrace;
    }

}
