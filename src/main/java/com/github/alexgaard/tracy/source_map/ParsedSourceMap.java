package com.github.alexgaard.tracy.source_map;

import java.util.List;

public class ParsedSourceMap {

    public final int version;

    public final String file;

    public final List<String> sources;

    public final List<String> names;

    public final List<MappingToken> mappingTokens;

    public final List<Integer> ignoreList;

    public ParsedSourceMap(int version, String file, List<String> sources, List<String> names, List<MappingToken> mappingTokens, List<Integer> ignoreList) {
        this.version = version;
        this.file = file;
        this.sources = sources;
        this.names = names;
        this.mappingTokens = mappingTokens;
        this.ignoreList = ignoreList;
    }

}
