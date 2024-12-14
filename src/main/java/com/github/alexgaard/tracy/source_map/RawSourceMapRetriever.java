package com.github.alexgaard.tracy.source_map;

import java.util.Optional;

public interface RawSourceMapRetriever {

    Optional<RawSourceMap> getSourceMap(String minifiedFilePath);

}
