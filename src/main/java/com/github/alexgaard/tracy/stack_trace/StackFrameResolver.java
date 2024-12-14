package com.github.alexgaard.tracy.stack_trace;

import com.github.alexgaard.tracy.source_map.ParsedSourceMap;

import java.util.Optional;

public interface StackFrameResolver {
    Optional<StackFrame> resolve(StackFrame minifiedStackFrame, ParsedSourceMap parsedSourceMap);

}
