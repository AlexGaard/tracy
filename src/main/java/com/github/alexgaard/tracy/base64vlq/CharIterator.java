package com.github.alexgaard.tracy.base64vlq;

public interface CharIterator {
    boolean hasNext();

    char next();

    char currentChar();

    static CharIterator fromString(String str) {
        return new CharIterator() {
            int counter = 0;
            @Override
            public boolean hasNext() {
                return counter < str.length();
            }

            @Override
            public char next() {
                return str.charAt(counter++);
            }

            @Override
            public char currentChar() {
                return str.charAt(counter);
            }
        };
    }

}
