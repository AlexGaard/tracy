package com.github.alexgaard.tracy;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alexgaard.tracy.source_map.RawSourceMap;
import com.github.alexgaard.tracy.source_map.SourceMapRetriever;
import com.github.alexgaard.tracy.stack_trace.StackTraceSourceMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        String stackTrace = "Error: Kablamo\n" +
                "    at t (index-BEz93Ooz.js:40:57476)\n" +
                "    at r (index-BEz93Ooz.js:40:57535)\n" +
                "    at Rd (index-BEz93Ooz.js:40:57546)\n" +
                "    at hu (index-BEz93Ooz.js:38:16959)\n" +
                "    at Xa (index-BEz93Ooz.js:40:43694)\n" +
                "    at Qa (index-BEz93Ooz.js:40:39499)\n" +
                "    at md (index-BEz93Ooz.js:40:39430)\n" +
                "    at Jr (index-BEz93Ooz.js:40:39289)\n" +
                "    at Ba (index-BEz93Ooz.js:40:34440)\n" +
                "    at E (index-BEz93Ooz.js:25:1562)";

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String debugMap = getFileAsString(".data/index-BEz93Ooz.js.map");

        RawSourceMap rawSourceMap = mapper.readValue(debugMap, RawSourceMap.class);

        SourceMapRetriever sourceMapRetriever = new SourceMapRetriever() {
            @Override
            public Optional<RawSourceMap> getSourceMap(String minifiedFilePath) {
                return Optional.of(rawSourceMap);
            }
        };

        StackTraceSourceMapper stackTraceSourceMapper = new StackTraceSourceMapper(sourceMapRetriever);

        String stackTraceWithSource = stackTraceSourceMapper.applySourceToMinifiedStackTrace(stackTrace);

        System.out.println(stackTraceWithSource);
    }

    public static String getFileAsString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static String getResourceFileAsString(String filePath) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(filePath)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

}