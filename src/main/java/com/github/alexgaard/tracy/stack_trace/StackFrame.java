package com.github.alexgaard.tracy.stack_trace;

import java.util.Objects;

public class StackFrame {
    public final String file;
    public final int line;

    public final int col;

    public StackFrame(String file, int line, int col) {
        this.file = file;
        this.line = line;
        this.col = col;
    }

    public static StackFrame fromString(String str) {
        String[] split = str.split(":");

        return new StackFrame(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public String toFrameString() {
        return file + ":" + line + ":" + col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StackFrame that = (StackFrame) o;
        return line == that.line && col == that.col && Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, line, col);
    }

    @Override
    public String toString() {
        return "StackFrame{" +
                "file='" + file + '\'' +
                ", line=" + line +
                ", col=" + col +
                '}';
    }
}