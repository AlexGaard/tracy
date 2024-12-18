package com.github.alexgaard.tracy.stack_trace;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.alexgaard.tracy.stack_trace.StackFrame.ANONYMOUS_FUNCTION;

public class BaseStackTraceParser implements StackTraceParser {

    private static final Pattern STACK_FRAME_PATTERN = Pattern.compile("at (\\w+ )?\\(?([\\w:\\-/]+\\.js):(\\d+):(\\d+)\\)?");

    @Override
    public Map<String, StackFrame> parse(String minifiedStackTrace) {
        Matcher matcher = STACK_FRAME_PATTERN.matcher(minifiedStackTrace);

        Map<String, StackFrame> stackFrames = new HashMap<>();

        while (matcher.find()) {
            String stackFrameStr = matcher.group(0);

            String functionName = Optional.ofNullable(matcher.group(1)).orElse(ANONYMOUS_FUNCTION);
            String file = matcher.group(2);
            Optional<Integer> line = tryParseInt(matcher.group(3));
            Optional<Integer> col = tryParseInt(matcher.group(4));

            if (line.isEmpty() || col.isEmpty()) {
                continue;
            }

            stackFrames.put(stackFrameStr, new StackFrame(functionName, file, line.get(), col.get()));
        }

        return stackFrames;
    }

    private Optional<Integer> tryParseInt(String str) {
        try {
            return Optional.of(Integer.parseInt(str));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
