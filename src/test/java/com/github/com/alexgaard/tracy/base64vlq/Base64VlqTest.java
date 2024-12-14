package com.github.com.alexgaard.tracy.base64vlq;

import com.github.alexgaard.tracy.base64vlq.Base64Vlq;
import com.github.alexgaard.tracy.base64vlq.CharIterator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base64VlqTest {

    @Test
    public void shouldParseVlq() {
        assertEquals(List.of(0,0,0,0), decodeAll("AAAA"));
        assertEquals(List.of(0, 0, 16, 1), decodeAll("AAgBC"));
        assertEquals(List.of(-1), decodeAll("D"));
        assertEquals(List.of(-2147483648), decodeAll("B"));
        assertEquals(List.of(2147483647), decodeAll("+/////D"));
    }

    private static List<Integer> decodeAll(String str) {
        CharIterator iterator = CharIterator.fromString(str);
        List<Integer> values = new ArrayList<>();

        while (iterator.hasNext()) {
            values.add(Base64Vlq.decode(iterator));
        }

        return values;
    }

}
