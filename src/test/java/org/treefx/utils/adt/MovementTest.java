package org.treefx.utils.adt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MovementTest {
    @Test
    void show() {
        // Test Movement.show() for Up
        assertEquals("Up", Movement.show(Movement.UP));

        // Test Movement.show() for Down with a sample value
        assertEquals("Down(5)", Movement.show(Movement.DOWN(5)));
    }

   @Test
    void read() {
        // Test Movement.read() for a valid "Up" string
        assertInstanceOf(Movement.Up.class, Movement.read("Up"));

        // Test Movement.read() for a valid "Down(<int>)" string
        Movement.Down downMovement = (Movement.Down) Movement.read("Down(10)");
        assertEquals(10, downMovement.child());

        // Test Movement.read() for an invalid string (should throw RuntimeException)
        assertThrows(RuntimeException.class, () -> Movement.read("InvalidInput"));

        // Test Movement.read() for a malformed "Down" string (should throw RuntimeException)
        assertThrows(RuntimeException.class, () -> Movement.read("Down(abc)"));
    }

    @Test
    void showReadIsomorphism() {
        // Test show(read(s)) == s for "Up"
        String upString = "Up";
        assertEquals(upString, Movement.show(Movement.read(upString)));

        // Test show(read(s)) == s for "Down(<int>)"
        String downString = "Down(15)";
        assertEquals(downString, Movement.show(Movement.read(downString)));
    }

    @Test
    void readShowIsomorphism() {
        // Test read(show(m)) == m for Movement.UP
        Movement upMovement = Movement.UP;
        assertEquals(upMovement, Movement.read(Movement.show(upMovement)));

        // Test read(show(m)) == m for Movement.Down(<int>)
        Movement downMovement = Movement.DOWN(20);
        assertEquals(downMovement, Movement.read(Movement.show(downMovement)));
    }
}