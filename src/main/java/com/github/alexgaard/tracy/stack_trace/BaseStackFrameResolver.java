package com.github.alexgaard.tracy.stack_trace;

import com.github.alexgaard.tracy.source_map.MappingToken;
import com.github.alexgaard.tracy.source_map.ParsedSourceMap;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class BaseStackFrameResolver implements StackFrameResolver {

    private final boolean useIgnoreList;

    public BaseStackFrameResolver(boolean useIgnoreList) {
        this.useIgnoreList = useIgnoreList;
    }

    @Override
    public Optional<StackFrame> resolve(StackFrame minifiedStackFrame, ParsedSourceMap parsedSourceMap) {
        Optional<MappingToken> mappingToken = findMappingToken(minifiedStackFrame, parsedSourceMap.mappingTokens);

        return mappingToken.map(token -> {
            if (useIgnoreList) {
                List<Integer> ignoreList = parsedSourceMap.ignoreList != null
                        ? parsedSourceMap.ignoreList
                        : Collections.emptyList();

                if (ignoreList.contains(token.sourceIndex)) {
                    return minifiedStackFrame;
                }
            }

            return createSourceStackFrame(token, parsedSourceMap);
        });
    }

    public static StackFrame createSourceStackFrame(MappingToken mappingToken, ParsedSourceMap sourceMap) {
        String functionName = safeGet(sourceMap.names, mappingToken.nameIndex)
                .orElse(StackFrame.ANONYMOUS_FUNCTION);

        String file = safeGet(sourceMap.sources, mappingToken.sourceIndex)
                .orElse(StackFrame.UNKNOWN_FILE);

        return new StackFrame(functionName, file, mappingToken.sourceLine + 1, mappingToken.sourceColumn);
    }

    public static Optional<MappingToken> findMappingToken(StackFrame minifiedStackFrame, List<MappingToken> sortedMappingTokens) {
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

    public static <T> Optional<T> safeGet(List<T> list, int idx) {
        if (idx < 0 || idx >= list.size()) {
            return Optional.empty();
        }

        return Optional.ofNullable(list.get(idx));
    }

}
