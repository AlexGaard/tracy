package com.github.com.alexgaard.tracy;

import com.github.alexgaard.tracy.stack_trace.BaseStackTraceParser;
import com.github.alexgaard.tracy.stack_trace.StackFrame;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseStackTraceParserTest {

    private final BaseStackTraceParser stackTraceParser = new BaseStackTraceParser();

    @Test
    public void shouldParseTrace1() {
        Map<String, StackFrame> parseResult = stackTraceParser.parse("at t (index-BEz93Ooz.js:40:57476)");

        assertEquals(new StackFrame("t", "index-BEz93Ooz.js", 40, 57476), parseResult.get("t (index-BEz93Ooz.js:40:57476)"));
    }

    @Test
    public void shouldParseTrace2() {
        Map<String, StackFrame> parseResult = stackTraceParser.parse("at t index-BEz93Ooz.js:40:57476");

        assertEquals(new StackFrame("t", "index-BEz93Ooz.js", 40, 57476), parseResult.get("t index-BEz93Ooz.js:40:57476"));
    }

    @Test
    public void shouldParseTrace3() {
        Map<String, StackFrame> parseResult = stackTraceParser.parse("at index-BEz93Ooz.js:40:57476");

        assertEquals(new StackFrame(null, "index-BEz93Ooz.js", 40, 57476), parseResult.get("index-BEz93Ooz.js:40:57476"));
    }

    @Test
    public void shouldParseTrace4() {
        Map<String, StackFrame> parseResult = stackTraceParser.parse("at (index-BEz93Ooz.js:40:57476)");

        assertEquals(new StackFrame(null, "index-BEz93Ooz.js", 40, 57476), parseResult.get("(index-BEz93Ooz.js:40:57476)"));
    }


    @Test
    public void shouldParseTrace5() {
        Map<String, StackFrame> parseResult = stackTraceParser.parse("@https://example.com/assets/index-Ip5hKn11.js:791:106667");

        assertEquals(new StackFrame(null, "https://example.com/assets/index-Ip5hKn11.js", 791, 106667), parseResult.get("@https://example.com/assets/index-Ip5hKn11.js:791:106667"));
    }

    @Test
    public void shouldParseTrace6() {
        Map<String, StackFrame> parseResult = stackTraceParser.parse("BgE@https://example.com/assets/index-Ip5hKn11.js:791:106667");

        assertEquals(new StackFrame("BgE", "https://example.com/assets/index-Ip5hKn11.js", 791, 106667), parseResult.get("BgE@https://example.com/assets/index-Ip5hKn11.js:791:106667"));
    }

    @Test
    public void shouldParseTrace7() {
        Map<String, StackFrame> parseResult = stackTraceParser.parse("index-Ip5hKn11.js:791:106667");

        assertEquals(new StackFrame(null, "index-Ip5hKn11.js", 791, 106667), parseResult.get("index-Ip5hKn11.js:791:106667"));
    }

    @Test
    public void shouldParseManyTraces() {
        Map<String, StackFrame> parseResult = stackTraceParser.parse(
                "test "
                + "at t (index-BEz93Ooz.js:40:57476)\n"
                + "BgE@https://example.com/assets/index-Ip5hKn11.js:791:106667\n"
                + "at t index-BEz93Ooz.js:40:57476\n"
        );

        assertEquals(new StackFrame("t", "index-BEz93Ooz.js", 40, 57476), parseResult.get("t (index-BEz93Ooz.js:40:57476)"));
        assertEquals(new StackFrame("BgE", "https://example.com/assets/index-Ip5hKn11.js", 791, 106667), parseResult.get("BgE@https://example.com/assets/index-Ip5hKn11.js:791:106667"));
        assertEquals(new StackFrame("t", "index-BEz93Ooz.js", 40, 57476), parseResult.get("t index-BEz93Ooz.js:40:57476"));
    }

}
