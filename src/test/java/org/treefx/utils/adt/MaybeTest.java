package org.treefx.utils.adt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MaybeTest {

    @Test
    void testJustReturnsValue() {
        Maybe<String> maybe = new Maybe.Just<>("valor");

        assertTrue(maybe.isJust());
        assertFalse(maybe.isNothing());
        assertEquals("valor", maybe.fromJust());
    }

    @Test
    void testNothingReturnsNull() {
        Maybe<String> maybe = new Maybe.Nothing<>();

        assertTrue(maybe.isNothing());
        assertFalse(maybe.isJust());
        assertNull(maybe.fromJust());
    }

    @Test
    void testJustCanContainNull() {
        Maybe<String> maybe = new Maybe.Just<>(null);

        assertTrue(maybe.isJust());
        assertFalse(maybe.isNothing());
        assertNull(maybe.fromJust());
    }

    @Test
    void testJustEquality() {
        Maybe<Integer> m1 = new Maybe.Just<>(100);
        Maybe<Integer> m2 = new Maybe.Just<>(100);

        assertEquals(m1, m2);
    }

    @Test
    void testNothingEquality() {
        Maybe<Integer> m1 = new Maybe.Nothing<>();
        Maybe<Integer> m2 = new Maybe.Nothing<>();

        assertEquals(m1, m2);
    }

    @Test
    void testJustInequality() {
        Maybe<Integer> m1 = new Maybe.Just<>(1);
        Maybe<Integer> m2 = new Maybe.Just<>(2);

        assertNotEquals(m1, m2);
    }
}
