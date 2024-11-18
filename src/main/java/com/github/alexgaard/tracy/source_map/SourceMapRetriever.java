package com.github.alexgaard.tracy.source_map;

import java.util.Optional;

public interface SourceMapRetriever {

    Optional<RawSourceMap> getSourceMap(String minifiedFilePath);

}
