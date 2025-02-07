package com.github.alexgaard.tracy.stack_trace;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseStackTraceParser implements StackTraceParser {

    private static final Pattern STACK_FRAME_PATTERN = Pattern.compile("\\(?([\\w:\\-/.]+\\.js):(\\d+):(\\d+)\\)?");

    @Override
    public Map<String, StackFrame> parse(String minifiedStackTrace) {
        Map<String, StackFrame> stackFrames = new HashMap<>();

        Matcher matcher = STACK_FRAME_PATTERN.matcher(minifiedStackTrace);

        while (matcher.find()) {
            String file = matcher.group(1);
            Optional<Integer> line = tryParseInt(matcher.group(2));
            Optional<Integer> col = tryParseInt(matcher.group(3));

            if (line.isEmpty() || col.isEmpty()) {
                continue;
            }

            StackFrameFunction function = findFrameFunction(minifiedStackTrace, matcher.start());
            String matchedFrame = minifiedStackTrace.substring(function.startOfFrameIdx, matcher.end());

            stackFrames.put(matchedFrame, new StackFrame(function.functionName, file, line.get(), col.get()));
        }

        return stackFrames;
    }

    private static StackFrameFunction findFrameFunction(String minifiedTrace, int startOfMatchedFrameIdx) {
        if (startOfMatchedFrameIdx == 0) {
            return new StackFrameFunction(null, 0);
        }

        int leftCharIdx = startOfMatchedFrameIdx - 1;
        char leftChar = minifiedTrace.charAt(leftCharIdx);

        if (leftChar == '@' || leftChar == ' '){
            // we want '@' to be part of the final stack frame but not ' '
            int startOfFrameIdx = leftChar == '@'
                    ? leftCharIdx
                    : startOfMatchedFrameIdx;

            int i = startOfMatchedFrameIdx - 2;

            // Function name is left until non-alphabetic char
            for (; i > 0; i--) {
                if (!Character.isAlphabetic(minifiedTrace.charAt(i))) {
                   break;
                }
            }

            if (i == 0) {
                if (minifiedTrace.startsWith("at")) {
                    return new StackFrameFunction(null, startOfFrameIdx);
                }

                return new StackFrameFunction(null, 0);
            }

            if (i == startOfMatchedFrameIdx - 2) {
                return new StackFrameFunction(null, startOfFrameIdx);
            }

            String functionName = minifiedTrace.substring(i + 1, leftCharIdx);

            if ("at".equals(functionName)) {
                return new StackFrameFunction(null, startOfFrameIdx);
            }

            return new StackFrameFunction(functionName, i + 1);
        }

        return new StackFrameFunction(null, startOfMatchedFrameIdx);
    }

    private Optional<Integer> tryParseInt(String str) {
        try {
            return Optional.of(Integer.parseInt(str));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static class StackFrameFunction {
        public final String functionName;

        public final int startOfFrameIdx;

        private StackFrameFunction(String functionName, int startOfFrameIdx) {
            this.functionName = functionName;
            this.startOfFrameIdx = startOfFrameIdx;
        }
    }

}
