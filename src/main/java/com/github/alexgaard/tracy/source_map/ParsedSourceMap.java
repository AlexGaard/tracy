package com.github.alexgaard.tracy.source_map;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.github.alexgaard.tracy.source_map.MappingsTokenizer.extractTokens;

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

    public static ParsedSourceMap parseRawSourceMap(RawSourceMap rawSourceMap) {
        List<MappingToken> mappingTokens = extractTokens(rawSourceMap.getMappings());
        mappingTokens.sort(MappingToken::compare);

        return new ParsedSourceMap(
                rawSourceMap.getVersion(),
                rawSourceMap.getFile(),
                Collections.emptyList(),
//                rawSourceMap.getSources(), Skip sources as they are not used and take a lot of memory
                rawSourceMap.getNames(),
                mappingTokens,
                rawSourceMap.getX_google_ignoreList()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedSourceMap that = (ParsedSourceMap) o;
        return version == that.version && Objects.equals(file, that.file) && Objects.equals(sources, that.sources) && Objects.equals(names, that.names) && Objects.equals(mappingTokens, that.mappingTokens) && Objects.equals(ignoreList, that.ignoreList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, file, sources, names, mappingTokens, ignoreList);
    }

    @Override
    public String toString() {
        return "ParsedSourceMap{" +
                "version=" + version +
                ", file='" + file + '\'' +
                ", sourcesSize=" + sources.size() +
                ", namesSize=" + names.size() +
                ", mappingTokensSize=" + mappingTokens.size() +
                ", ignoreList=" + ignoreList +
                '}';
    }
}
