package org.treefx.utils.adt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TTest {

    @Test
    void fst() {
        T.MkT<Integer, String> pair = new T.MkT<>(1, "test");
        assertEquals(1, pair.fst());
    }

    @Test
    void snd() {
        T.MkT<Integer, String> pair = new T.MkT<>(1, "test");
        assertEquals("test", pair.snd());
    }
}