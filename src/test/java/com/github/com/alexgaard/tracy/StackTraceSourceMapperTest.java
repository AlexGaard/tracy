package com.github.com.alexgaard.tracy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alexgaard.tracy.source_map.RawSourceMap;
import com.github.alexgaard.tracy.source_map.RawSourceMapRetriever;
import com.github.alexgaard.tracy.stack_trace.StackTraceSourceMapper;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackTraceSourceMapperTest {

    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void shouldResolveMinifiedStackTrace() throws IOException {
        String debugMap = getResourceFileAsString("index-BEz93Ooz.js.map");

        RawSourceMap rawSourceMap = mapper.readValue(debugMap, RawSourceMap.class);
        RawSourceMapRetriever sourceMapRetriever = minifiedFilePath -> Optional.of(rawSourceMap);
        StackTraceSourceMapper stackTraceSourceMapper = new StackTraceSourceMapper(sourceMapRetriever);

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

        String expectedTrace = "Error: Kablamo\n" +
                "    at t (../../src/App.tsx:11:18)\n" +
                "    at r (../../src/App.tsx:18:12)\n" +
                "    at Rd (../../src/App.tsx:22:12)\n" +
                "    at hu (index-BEz93Ooz.js:38:16959)\n" +
                "    at Xa (index-BEz93Ooz.js:40:43694)\n" +
                "    at Qa (index-BEz93Ooz.js:40:39499)\n" +
                "    at md (index-BEz93Ooz.js:40:39430)\n" +
                "    at Jr (index-BEz93Ooz.js:40:39289)\n" +
                "    at Ba (index-BEz93Ooz.js:40:34440)\n" +
                "    at E (index-BEz93Ooz.js:25:1562)";

        assertEquals(expectedTrace, stackTraceSourceMapper.applySourceMap(stackTrace));
    }

    private static String getResourceFileAsString(String filePath) throws IOException {
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
