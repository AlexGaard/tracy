package com.github.alexgaard.tracy.stack_trace;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackTrackParser {

    private static Pattern stackFramePattern = Pattern.compile("[\\w-]+\\.js:\\d+:\\d+");

    public static Map<String, StackFrame> findStackFrames(String minifiedStackTrace) {
        Matcher matcher = stackFramePattern.matcher(minifiedStackTrace);

        Map<String, StackFrame> stackFrames = new HashMap<>();

        while (matcher.find()) {
            String stackFrameStr = matcher.group();
            stackFrames.put(stackFrameStr, StackFrame.fromString(stackFrameStr));
        }

        return stackFrames;
    }

}
