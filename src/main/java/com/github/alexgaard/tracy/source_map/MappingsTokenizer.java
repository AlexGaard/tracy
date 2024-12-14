package com.github.alexgaard.tracy.source_map;

import com.github.alexgaard.tracy.base64vlq.Base64Vlq;
import com.github.alexgaard.tracy.base64vlq.CharIterator;

import java.util.ArrayList;
import java.util.List;

public class MappingsTokenizer {

    public static List<MappingToken> extractTokens(String mappingsStr) {
        final CharIterator iter = CharIterator.fromString(mappingsStr);
        final List<MappingToken> tokens = new ArrayList<>();

        int minifiedLineNumber = 0;
        int minifiedColumnNumber = 0;
        int sourceIndex = 0;
        int sourceLine = 0;
        int sourceColumn = 0;
        int nameIndex = 0;
        int valueCounter = 0;

        while (iter.hasNext()) {
            char c = iter.currentChar();

            if (c == ',' || c == ';') {
                tokens.add(new MappingToken(
                        minifiedLineNumber,
                        minifiedColumnNumber,
                        valueCounter >= 2 ? sourceIndex : Integer.MIN_VALUE,
                        valueCounter >= 3 ? sourceLine : Integer.MIN_VALUE,
                        valueCounter >= 4 ? sourceColumn : Integer.MIN_VALUE,
                        valueCounter >= 5 ? nameIndex : Integer.MIN_VALUE
                ));

                if (c == ';') {
                    minifiedLineNumber += 1;
                    minifiedColumnNumber = 0;
                }

                valueCounter = 0;
                iter.next();
                continue;
            }

            int decodedValue = Base64Vlq.decode(iter);

            if (valueCounter == 0) {
                minifiedColumnNumber += decodedValue;
                minifiedColumnNumber = Math.max(minifiedColumnNumber, 0);
            } else if (valueCounter == 1) {
                sourceIndex += decodedValue;
                sourceIndex = Math.max(sourceIndex, 0);

            } else if (valueCounter == 2) {
                sourceLine += decodedValue;
                sourceLine = Math.max(sourceLine, 0);

            } else if (valueCounter == 3) {
                sourceColumn += decodedValue;
                sourceColumn = Math.max(sourceColumn, 0);

            } else if (valueCounter == 4) {
                nameIndex += decodedValue;
                nameIndex = Math.max(nameIndex, 0);

            }

            valueCounter++;
        }

        tokens.add(new MappingToken(
                minifiedLineNumber,
                minifiedColumnNumber,
                valueCounter >= 2 ? sourceIndex : Integer.MIN_VALUE,
                valueCounter >= 3 ? sourceLine : Integer.MIN_VALUE,
                valueCounter >= 4 ? sourceColumn : Integer.MIN_VALUE,
                valueCounter >= 5 ? nameIndex : Integer.MIN_VALUE
        ));

        return tokens;
    }

}
