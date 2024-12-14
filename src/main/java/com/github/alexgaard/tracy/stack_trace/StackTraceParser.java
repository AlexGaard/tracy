package com.github.alexgaard.tracy.stack_trace;

import java.util.Map;

public interface StackTraceParser {

    /**
     * Parses a minified stack trace like the string below and returns a map with all the found stack frames in the trace
     * Error: Kablamo 
     *    at t (index-BEz93Ooz.js:40:57476)
     *    at r (index-BEz93Ooz.js:40:57535)
     *    at Rd (index-BEz93Ooz.js:40:57546)
     *    at hu (index-BEz93Ooz.js:38:16959)
     *    at Xa (index-BEz93Ooz.js:40:43694)
     *    at Qa (index-BEz93Ooz.js:40:39499)
     *    at md (index-BEz93Ooz.js:40:39430)
     *    at Jr (index-BEz93Ooz.js:40:39289)
     *    at Ba (index-BEz93Ooz.js:40:34440)
     *    at E (index-BEz93Ooz.js:25:1562);
     * @param minifiedStackTrace JavaScript stack trace
     * @return map where the keys are strings such as "index-BEz93Ooz.js:40:57476" and the corresponding parsed stack frame
     */
    Map<String, StackFrame> parse(String minifiedStackTrace);

}
