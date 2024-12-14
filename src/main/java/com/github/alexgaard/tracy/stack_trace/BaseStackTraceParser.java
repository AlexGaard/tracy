package com.github.alexgaard.tracy.stack_trace;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseStackTraceParser implements StackTraceParser {

    private static final Pattern STACK_FRAME_EXTENDED_PATTERN = Pattern.compile("at \\w+ \\([\\w-]+\\.js:\\d+:\\d+\\)");

    private static final Pattern STACK_FRAME_PATTERN = Pattern.compile("[\\w-]+\\.js:\\d+:\\d+");

    @Override
    public Map<String, StackFrame> parse(String minifiedStackTrace) {
        Matcher extendedMatcher = STACK_FRAME_EXTENDED_PATTERN.matcher(minifiedStackTrace);

        Map<String, StackFrame> stackFrames = new HashMap<>();
        StringBuilder totalStackFramesStrs = new StringBuilder();

        while (extendedMatcher.find()) {
            String stackFrameStr = extendedMatcher.group();
            totalStackFramesStrs.append(stackFrameStr);
            stackFrames.put(stackFrameStr, toStackFrame(stackFrameStr));
        }

        Matcher matcher = STACK_FRAME_PATTERN.matcher(minifiedStackTrace);

        while (matcher.find()) {
            String stackFrameStr = matcher.group();

            // Only add frames that have not already been added
            if (totalStackFramesStrs.indexOf(stackFrameStr) == -1) {
                stackFrames.put(stackFrameStr, toStackFrame(stackFrameStr));
            }
        }

        return stackFrames;
    }

    /**
     * Parses strings such as "at t (index-BEz93Ooz.js:40:57476)" or "index-BEz93Ooz.js:40:57476" into a StackFrame
     * @param stackFrameStr stack frame string
     * @return parsed stack frame
     */
    public static StackFrame toStackFrame(String stackFrameStr) {
        if (stackFrameStr.startsWith("at ")) {
            stackFrameStr = stackFrameStr.substring(3);
        }

        String[] functionAndFile = stackFrameStr.split(" ");

        String frameInfo = functionAndFile.length > 1
                ? functionAndFile[1]
                : functionAndFile[0];

        if (frameInfo.startsWith("(") && frameInfo.endsWith(")")) {
            frameInfo = frameInfo.substring(1, frameInfo.length() - 1);
        }

        String[] frameInfoParts = frameInfo.split(":");

        return new StackFrame(
                functionAndFile.length > 1 ? functionAndFile[0] : null,
                frameInfoParts[0], Integer.parseInt(frameInfoParts[1]),
                Integer.parseInt(frameInfoParts[2])
        );
    }

}
