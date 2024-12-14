package com.github.alexgaard.tracy.stack_trace;

import com.github.alexgaard.tracy.source_map.MappingToken;
import com.github.alexgaard.tracy.source_map.ParsedSourceMap;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BaseStackFrameResolver implements StackFrameResolver {

    @Override
    public Optional<StackFrame> resolve(StackFrame minifiedStackFrame, ParsedSourceMap parsedSourceMap) {
        Optional<MappingToken> mappingToken = findMappingTokenRange(minifiedStackFrame, parsedSourceMap.mappingTokens);

        return mappingToken.map(token -> createSourceStackFrame(minifiedStackFrame, token, parsedSourceMap));
    }

    public static StackFrame createSourceStackFrame(StackFrame minifiedFrame, MappingToken mappingToken, ParsedSourceMap sourceMap) {
        List<Integer> ignoreList = sourceMap.ignoreList != null
                ? sourceMap.ignoreList
                : Collections.emptyList();

        if (ignoreList.contains(mappingToken.sourceIndex)) {
            return minifiedFrame;
        }

        return new StackFrame(sourceMap.sources.get(mappingToken.sourceIndex), mappingToken.sourceLine + 1, mappingToken.sourceColumn);
    }

    public static Optional<MappingToken> findMappingTokenRange(StackFrame minifiedStackFrame, List<MappingToken> sortedMappingTokens) {
        if (sortedMappingTokens.isEmpty()) {
            return Optional.empty();
        }

        int line = minifiedStackFrame.line - 1; // minus 1 for zero-based index
        int col = minifiedStackFrame.col - 1; // minus 1 for zero-based index

        if (line < 1 || col < 1) {
            return Optional.empty();
        }

        MappingToken searchForToken = new MappingToken(line, col, -1, -1, -1, -1);

        int tokenIdx = Collections.binarySearch(sortedMappingTokens, searchForToken, MappingToken::compare);

        return Optional.of(sortedMappingTokens.get(Math.abs(tokenIdx)));
    }

}
