package com.github.alexgaard.tracy.source_map;

import java.util.Objects;

public class MappingToken {
    public final int minifiedLine;
    public final int minifiedColumn;
    public final int sourceIndex;
    public final int sourceLine;
    public final int sourceColumn;
    public final int nameIndex;

    public MappingToken(int minifiedLine, int minifiedColumn, int sourceIndex, int sourceLine, int sourceColumn, int nameIndex) {
        this.minifiedLine = minifiedLine;
        this.minifiedColumn = minifiedColumn;
        this.sourceIndex = sourceIndex;
        this.sourceLine = sourceLine;
        this.sourceColumn = sourceColumn;
        this.nameIndex = nameIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingToken token = (MappingToken) o;
        return minifiedLine == token.minifiedLine && minifiedColumn == token.minifiedColumn && sourceIndex == token.sourceIndex && sourceLine == token.sourceLine && sourceColumn == token.sourceColumn && nameIndex == token.nameIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minifiedLine, minifiedColumn, sourceIndex, sourceLine, sourceColumn, nameIndex);
    }

    @Override
    public String toString() {
        return "DebugToken{" +
                "minifiedLine=" + minifiedLine +
                ", minifiedColumn=" + minifiedColumn +
                ", sourceIndex=" + sourceIndex +
                ", sourceLine=" + sourceLine +
                ", sourceColumn=" + sourceColumn +
                ", nameIndex=" + nameIndex +
                '}';
    }
}
