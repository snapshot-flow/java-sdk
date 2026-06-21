package com.snapshotflow.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueryParamsTest {

    @Test
    void skipsNullValues() {
        QueryParams q = new QueryParams()
                .add("a", "1")
                .add("b", (String) null)
                .add("c", (Integer) null)
                .add("d", (Boolean) null);
        assertEquals("a=1", q.encode());
    }

    @Test
    void percentEncodesSpecialCharacters() {
        QueryParams q = new QueryParams().add("url", "https://example.com/a b?x=1&y=2");
        assertEquals("url=https%3A%2F%2Fexample.com%2Fa+b%3Fx%3D1%26y%3D2", q.encode());
    }

    @Test
    void formatsBooleansAndDoubles() {
        QueryParams q = new QueryParams()
                .add("flag", Boolean.TRUE)
                .add("whole", 1.0)
                .add("frac", 1.5);
        assertEquals("flag=true&whole=1&frac=1.5", q.encode());
    }

    @Test
    void emptyWhenNoParams() {
        QueryParams q = new QueryParams();
        assertTrue(q.isEmpty());
        assertEquals("", q.encode());
    }
}
