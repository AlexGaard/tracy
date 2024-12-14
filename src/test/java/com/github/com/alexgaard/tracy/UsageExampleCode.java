package com.github.com.alexgaard.tracy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alexgaard.tracy.source_map.RawSourceMap;
import com.github.alexgaard.tracy.source_map.RawSourceMapRetriever;
import com.github.alexgaard.tracy.stack_trace.StackTraceSourceMapper;
import com.github.alexgaard.tracy.utils.SourceMapUtils;

public class UsageExampleCode {

    public static void main(String[] args) {
        // Use ObjectMapper from Jackson or another JSON library
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        RawSourceMapRetriever rawSourceMapRetriever = minifiedFilePath -> {
            return SourceMapUtils.retrieveSourceMapFromFile("./source_maps", minifiedFilePath)
                    .map(fileContent -> {
                        try {
                            return mapper.readValue(fileContent, RawSourceMap.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
        };

        StackTraceSourceMapper stackTraceSourceMapper = new StackTraceSourceMapper(rawSourceMapRetriever);

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

        String unminifiedStackTrace = stackTraceSourceMapper.applySourceMap(stackTrace);

        System.out.println(unminifiedStackTrace);

        /*
            Prints out:
            Error: Kablamo
                at ../../src/App.tsx:11:18
                at test2 (../../src/App.tsx:18:12)
                at test (../../src/App.tsx:22:12)
                at c (../../node_modules/react-dom/cjs/react-dom.production.min.js:160:136)
                at Nh (../../node_modules/react-dom/cjs/react-dom.production.min.js:289:336)
                at Vk (../../node_modules/react-dom/cjs/react-dom.production.min.js:279:388)
                at Uk (../../node_modules/react-dom/cjs/react-dom.production.min.js:279:319)
                at Tk (../../node_modules/react-dom/cjs/react-dom.production.min.js:279:179)
                at Ik (../../node_modules/react-dom/cjs/react-dom.production.min.js:267:208)
                at d (../../node_modules/scheduler/cjs/scheduler.production.min.js:13:202)
        */
    }

}
