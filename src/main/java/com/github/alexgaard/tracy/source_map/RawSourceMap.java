package com.github.alexgaard.tracy.source_map;

import java.util.List;
import java.util.Objects;

public class RawSourceMap {

    private int version;

    private String file;

    private List<String> sources;

    private List<String> sourcesContent;

    private List<String> names;

    private String mappings;

    private List<Integer> x_google_ignoreList;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public List<String> getSourcesContent() {
        return sourcesContent;
    }

    public void setSourcesContent(List<String> sourcesContent) {
        this.sourcesContent = sourcesContent;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getMappings() {
        return mappings;
    }

    public void setMappings(String mappings) {
        this.mappings = mappings;
    }

    public List<Integer> getX_google_ignoreList() {
        return x_google_ignoreList;
    }

    public void setX_google_ignoreList(List<Integer> xGoogleIgnoreList) {
        this.x_google_ignoreList = xGoogleIgnoreList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawSourceMap that = (RawSourceMap) o;
        return version == that.version && Objects.equals(file, that.file) && Objects.equals(sources, that.sources) && Objects.equals(sourcesContent, that.sourcesContent) && Objects.equals(names, that.names) && Objects.equals(mappings, that.mappings) && Objects.equals(x_google_ignoreList, that.x_google_ignoreList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, file, sources, sourcesContent, names, mappings, x_google_ignoreList);
    }
}
