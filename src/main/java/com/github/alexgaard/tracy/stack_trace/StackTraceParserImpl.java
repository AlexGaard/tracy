package com.github.alexgaard.tracy.stack_trace;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackTraceParserImpl implements StackTraceParser {

    private static final Pattern STACK_FRAME_PATTERN = Pattern.compile("[\\w-]+\\.js:\\d+:\\d+");

    @Override
    public Map<String, StackFrame> parse(String minifiedStackTrace) {
        Matcher matcher = STACK_FRAME_PATTERN.matcher(minifiedStackTrace);

        Map<String, StackFrame> stackFrames = new HashMap<>();

        while (matcher.find()) {
            String stackFrameStr = matcher.group();
            stackFrames.put(stackFrameStr, StackFrame.fromString(stackFrameStr));
        }

        return stackFrames;
    }

}
