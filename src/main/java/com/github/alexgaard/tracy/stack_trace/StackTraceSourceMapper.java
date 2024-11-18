package com.github.alexgaard.tracy.stack_trace;

import com.github.alexgaard.tracy.source_map.MappingToken;
import com.github.alexgaard.tracy.source_map.ParsedSourceMap;
import com.github.alexgaard.tracy.source_map.RawSourceMap;
import com.github.alexgaard.tracy.source_map.SourceMapRetriever;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.*;

import static com.github.alexgaard.tracy.source_map.MappingsTokenizer.extractTokens;

public class StackTraceSourceMapper {

    private final SourceMapRetriever sourceMapRetriever;

    private final Cache<String, Optional<ParsedSourceMap>> cachedSourceMaps = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(6))
            .build();

    private final Cache<StackFrame, StackFrame> resolvedStackFrames = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(6))
            .build();

    public StackTraceSourceMapper(SourceMapRetriever sourceMapRetriever) {
        this.sourceMapRetriever = sourceMapRetriever;
    }

    public String applySourceToMinifiedStackTrace(String minifiedStackTrace) {
        final Map<String, StackFrame> stackFrames = StackTrackParser.findStackFrames(minifiedStackTrace);

        String stackTrace = minifiedStackTrace;

        for (Map.Entry<String, StackFrame> entry : stackFrames.entrySet()) {
            String stackFrameStr = entry.getKey();
            StackFrame minifiedStackFrame = entry.getValue();

            Optional<ParsedSourceMap> maybeSourceMap = cachedSourceMaps.get(minifiedStackFrame.file, (file) -> {
                return sourceMapRetriever.getSourceMap(file).map(StackTraceSourceMapper::parseRawSourceMap);
            });

            if (maybeSourceMap.isEmpty()) {
                continue;
            }

            Optional<StackFrame> maybeStackFrame = applySourceMap(minifiedStackFrame, maybeSourceMap.get());

            if (maybeStackFrame.isEmpty()) {
                continue;
            }

            stackTrace = stackTrace.replaceAll(stackFrameStr, maybeStackFrame.get().toFrameString());
        }

        return stackTrace;
    }

    public static Optional<StackFrame> applySourceMap(StackFrame minifiedStackFrame, ParsedSourceMap parsedSourceMap) {
        // TODO: Cache using resolvedStackFrames
        Optional<MappingTokenRange> tokenRange = findMappingTokenRange(minifiedStackFrame, parsedSourceMap.mappingTokens);

        return tokenRange.map(range -> createSourceStackFrame(minifiedStackFrame, range, parsedSourceMap));
    }

    public static StackFrame createSourceStackFrame(StackFrame minifiedFrame, MappingTokenRange matchingTokenRange, ParsedSourceMap sourceMap) {
        MappingToken token = matchingTokenRange.from;

        List<Integer> ignoreList = sourceMap.ignoreList != null
                ? sourceMap.ignoreList
                : Collections.emptyList();

        if (ignoreList.contains(token.sourceIndex)){
            return minifiedFrame;
        }

        return new StackFrame(sourceMap.sources.get(token.sourceIndex + ignoreList.size()), token.sourceLine + 1, token.sourceIndex + 1);
    }

    public static Optional<MappingTokenRange> findMappingTokenRange(StackFrame minifiedStackFrame, List<MappingToken> sortedMappingTokens) {
        final int line = minifiedStackFrame.line - 1; // minus 1 for 0 idx
        final int col = minifiedStackFrame.col - 1; // minus 1 for 0 idx

        // TODO: Binary search
        for (int i = 1; i < sortedMappingTokens.size(); i++) {
            MappingToken previousToken = sortedMappingTokens.get(i - 1);
            MappingToken currentToken = sortedMappingTokens.get(i);

            if (currentToken.minifiedLine == line && currentToken.minifiedColumn == col) {
                return Optional.of(new MappingTokenRange(currentToken, currentToken));
            }

            if (
                    previousToken.minifiedLine < line
                            && previousToken.minifiedColumn < col
                            && currentToken.minifiedLine > line
                            && currentToken.minifiedColumn > col
            ) {
                return Optional.of(new MappingTokenRange(previousToken, currentToken));
            }
        }

        return Optional.empty();
    }

    public static ParsedSourceMap parseRawSourceMap(RawSourceMap rawSourceMap) {
        return new ParsedSourceMap(
                rawSourceMap.getVersion(),
                rawSourceMap.getFile(),
                rawSourceMap.getSources(),
                rawSourceMap.getNames(),
                extractTokens(rawSourceMap.getMappings()),
                rawSourceMap.getX_google_ignoreList()
        );
    }

    public static class MappingTokenRange {
        public final MappingToken from;
        public final MappingToken to;

        public MappingTokenRange(MappingToken from, MappingToken to) {
            this.from = from;
            this.to = to;
        }
    }

}
