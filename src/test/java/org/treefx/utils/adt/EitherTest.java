package org.treefx.utils.adt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EitherTest {

    @Test
    void testLeftValue() {
        Either<String, Integer> either = new Either.Left<>("error");

        assertTrue(either instanceof Either.Left);
        assertFalse(either instanceof Either.Right);

        Either.Left<String, Integer> left = (Either.Left<String, Integer>) either;
        assertEquals("error", left.l());
    }

    @Test
    void testRightValue() {
        Either<String, Integer> either = new Either.Right<>(42);

        assertTrue(either instanceof Either.Right);
        assertFalse(either instanceof Either.Left);

        Either.Right<String, Integer> right = (Either.Right<String, Integer>) either;
        assertEquals(42, right.r());
    }
}
