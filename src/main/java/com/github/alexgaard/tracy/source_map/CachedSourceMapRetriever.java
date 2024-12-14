package com.github.alexgaard.tracy.source_map;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CachedSourceMapRetriever implements SourceMapRetriever {

    private static final Logger log = LoggerFactory.getLogger(CachedSourceMapRetriever.class);

    private final RawSourceMapRetriever rawSourceMapRetriever;

    private final Cache<String, Optional<ParsedSourceMap>> cachedSourceMaps;

    public CachedSourceMapRetriever(RawSourceMapRetriever rawSourceMapRetriever, Cache<String, Optional<ParsedSourceMap>> cachedSourceMaps) {
        this.rawSourceMapRetriever = rawSourceMapRetriever;
        this.cachedSourceMaps = cachedSourceMaps;
    }

    @Override
    public Optional<ParsedSourceMap> getSourceMap(String minifiedFilePath) {
        return cachedSourceMaps.get(minifiedFilePath, (file) -> {
            log.debug("Retrieving source map for file {}", file);
            return rawSourceMapRetriever.getSourceMap(file).map(ParsedSourceMap::parseRawSourceMap);
        });
    }

}
