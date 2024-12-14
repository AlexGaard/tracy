package com.github.alexgaard.tracy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class SourceMapUtils {

    private final static Logger log = LoggerFactory.getLogger(SourceMapUtils.class);

    public static Optional<String> retrieveSourceMapFromFile(String sourceMapDirectory, String minifiedFilePath) {
        Path sourceMapPath = Paths.get(sourceMapDirectory, minifiedFilePath + ".map");

        try {
            String sourceMapContent = new String(Files.readAllBytes(sourceMapPath));
            return Optional.of(sourceMapContent);
        } catch (Exception e) {
            log.warn("Unable to find source map file at {}", sourceMapPath.toAbsolutePath());
            return Optional.empty();
        }
    }

    public static Optional<String> retrieveSourceMapFromUrl(String baseUrl, String minifiedFilePath) {
        try {
            String preparedBaseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
            URL url = URI.create(preparedBaseUrl).resolve(minifiedFilePath + ".map").toURL();

            try (InputStream in = url.openStream()) {
                String sourceMapContent = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                return Optional.of(sourceMapContent);
            }
        } catch (Exception e) {
            log.warn("Unable to find source map file at baseUrl {} with path {}", baseUrl, minifiedFilePath+ ".map");
            return Optional.empty();
        }
    }

}
