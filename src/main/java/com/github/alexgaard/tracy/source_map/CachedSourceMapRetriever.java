package com.github.alexgaard.tracy.source_map;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.Optional;
import java.util.function.Function;

public class CachedSourceMapRetriever implements SourceMapRetriever {

    private final RawSourceMapRetriever rawSourceMapRetriever;
    private final Cache<String, Optional<ParsedSourceMap>> cachedSourceMaps;

    public CachedSourceMapRetriever(RawSourceMapRetriever rawSourceMapRetriever, Cache<String, Optional<ParsedSourceMap>> cachedSourceMaps) {
        this.rawSourceMapRetriever = rawSourceMapRetriever;
        this.cachedSourceMaps = cachedSourceMaps;
    }

    @Override
    public Optional<ParsedSourceMap> getSourceMap(String minifiedFilePath) {
        return cachedSourceMaps.get(minifiedFilePath, (file) -> {
            return rawSourceMapRetriever.getSourceMap(file).map(ParsedSourceMap::parseRawSourceMap);
        });
    }

}
