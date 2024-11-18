package com.github.alexgaard.tracy.source_map;

import java.util.List;

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
}
