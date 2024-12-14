package com.github.com.alexgaard.tracy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alexgaard.tracy.source_map.RawSourceMap;
import com.github.alexgaard.tracy.source_map.RawSourceMapRetriever;
import com.github.alexgaard.tracy.stack_trace.StackTraceSourceMapper;
import com.github.alexgaard.tracy.utils.SourceMapUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StackTraceSourceMapperTest {

    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void shouldResolveMinifiedStackTrace() throws IOException {
        String debugMap = SourceMapUtils.retrieveSourceMapFromFile("./source_maps", "index-BEz93Ooz.js")
                .orElseThrow();

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
                "    at ../../src/App.tsx:11:18\n" +
                "    at test2 (../../src/App.tsx:18:12)\n" +
                "    at test (../../src/App.tsx:22:12)\n" +
                "    at c (../../node_modules/react-dom/cjs/react-dom.production.min.js:160:136)\n" +
                "    at Nh (../../node_modules/react-dom/cjs/react-dom.production.min.js:289:336)\n" +
                "    at Vk (../../node_modules/react-dom/cjs/react-dom.production.min.js:279:388)\n" +
                "    at Uk (../../node_modules/react-dom/cjs/react-dom.production.min.js:279:319)\n" +
                "    at Tk (../../node_modules/react-dom/cjs/react-dom.production.min.js:279:179)\n" +
                "    at Ik (../../node_modules/react-dom/cjs/react-dom.production.min.js:267:208)\n" +
                "    at d (../../node_modules/scheduler/cjs/scheduler.production.min.js:13:202)";

        assertEquals(expectedTrace, stackTraceSourceMapper.applySourceMap(stackTrace));
    }

}
