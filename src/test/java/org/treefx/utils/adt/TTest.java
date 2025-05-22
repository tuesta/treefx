package org.treefx.utils.adt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TTest {

    @Test
    void testTupleValues() {
        T<String, Integer> tuple = new T.MkT<>("clave", 100);

        assertEquals("clave", tuple.fst());
        assertEquals(100, tuple.snd());
    }

    @Test
    void testTupleWithNulls() {
        T<String, String> tuple = new T.MkT<>(null, null);

        assertNull(tuple.fst());
        assertNull(tuple.snd());
    }

    @Test
    void testTupleEquality() {
        T<String, Integer> t1 = new T.MkT<>("x", 1);
        T<String, Integer> t2 = new T.MkT<>("x", 1);

        assertEquals(t1, t2);
    }

    @Test
    void testTupleInequality() {
        T<String, Integer> t1 = new T.MkT<>("x", 1);
        T<String, Integer> t2 = new T.MkT<>("x", 2);

        assertNotEquals(t1, t2);
    }

    @Test
    void testTypesIndependent() {
        T<Integer, String> t = new T.MkT<>(42, "respuesta");

        assertEquals(42, t.fst());
        assertEquals("respuesta", t.snd());
    }
}
