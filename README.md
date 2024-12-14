# Tracy

[![](https://jitpack.io/v/AlexGaard/tracy.svg)](https://jitpack.io/#AlexGaard/tracy)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=AlexGaard_tracy&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=AlexGaard_tracy) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=AlexGaard_tracy&metric=bugs)](https://sonarcloud.io/summary/new_code?id=AlexGaard_tracy) [![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=AlexGaard_tracy&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=AlexGaard_tracy) [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=AlexGaard_tracy&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=AlexGaard_tracy)

Apply source maps to JavaScript stack traces with Java.

In many cases it is easier to apply source maps to JavaScript stack traces while in the browser before logging it to a backend.

However, in cases where the source map should be kept secret, then it is better to send minified stack traces to a backend and have 
it apply the source maps instead before logging it.

## Usage

```java
// Use ObjectMapper from Jackson or another mapper from a different JSON library
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
```

## Installation

### Add Jitpack repository

Gradle:
```kotlin
repositories {
	maven { setUrl("https://jitpack.io") }
}
```

Maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Add dependency
Gradle:
```kotlin
dependencies {
	implementation("com.github.alexgaard:tracy:LATEST_RELEASE")
}
```

Maven:
```xml
<dependency>
    <groupId>com.github.alexgaard</groupId>
    <artifactId>tracy</artifactId>
    <version>LATEST_RELEASE</version>
</dependency>
```

The latest release can be found at https://github.com/AlexGaard/tracy/releases.


## Requirements

* Java 11 or later
* A JSON library such as Jackson or Gson