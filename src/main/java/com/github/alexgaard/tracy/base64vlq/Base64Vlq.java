package com.github.alexgaard.tracy.base64vlq;

public class Base64Vlq {

    private static final String BASE64_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789+/";

    private Base64Vlq() {}

    public static int decode(CharIterator iterator) {
        int shift = 0;
        int value = 0;

        do {
            char c = iterator.next();
            int charValue = BASE64_CHARS.indexOf(c);

            if (charValue == -1) {
                throw new IllegalArgumentException("Invalid Base64 VLQ character: " + c);
            }

            boolean hasContinuationBit = (charValue & 32) != 0;

            charValue &= 31;
            value += charValue << shift;

            if (hasContinuationBit) {
                shift += 5;
            } else {
                boolean shouldNegate = (value & 1) != 0;
                value >>>= 1;

                if (shouldNegate) {
                    return value == 0 ? -0x80000000 : -value;
                } else {
                    return value;
                }
            }
        } while (true);
    }

}
