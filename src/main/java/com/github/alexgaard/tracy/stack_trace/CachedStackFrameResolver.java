package com.github.alexgaard.tracy.stack_trace;

import com.github.alexgaard.tracy.source_map.ParsedSourceMap;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.Optional;

public class CachedStackFrameResolver implements StackFrameResolver {

    private final StackFrameResolver stackFrameResolver;

    private final Cache<StackFrame, Optional<StackFrame>> resolvedStackFrames;

    public CachedStackFrameResolver(StackFrameResolver stackFrameResolver, Cache<StackFrame, Optional<StackFrame>> resolvedStackFrames) {
        this.stackFrameResolver = stackFrameResolver;
        this.resolvedStackFrames = resolvedStackFrames;
    }

    @Override
    public Optional<StackFrame> resolve(StackFrame minifiedStackFrame, ParsedSourceMap parsedSourceMap) {
        return resolvedStackFrames.get(minifiedStackFrame, (ignored) -> stackFrameResolver.resolve(minifiedStackFrame, parsedSourceMap));
    }

}
