package org.treefx.utils.adt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MaybeTest {

    @Test
    void fromJust() {
        Maybe<Integer> just = new Maybe.Just<>(42);
        assertEquals(42, just.fromJust());

        Maybe<Integer> nothing = new Maybe.Nothing<>();
        assertNull(nothing.fromJust());
    }

    @Test
    void isNothing() {
        Maybe<Integer> just = new Maybe.Just<>(42);
        assertFalse(just.isNothing());

        Maybe<Integer> nothing = new Maybe.Nothing<>();
        assertTrue(nothing.isNothing());
    }

    @Test
    void isJust() {
        Maybe<Integer> just = new Maybe.Just<>(42);
        assertTrue(just.isJust());

        Maybe<Integer> nothing = new Maybe.Nothing<>();
        assertFalse(nothing.isJust());
    }
}