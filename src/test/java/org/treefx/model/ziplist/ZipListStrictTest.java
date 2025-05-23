package org.treefx.model.ziplist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZipListStrictTest {
    @Test
    void next() {
        ZipListStrict<Integer> zipList = new ZipListStrict<>();
        zipList.insert(5);
        zipList.insert(10);

        assertTrue(zipList.next());
        assertEquals(10, zipList.extract().fromJust());
        assertFalse(zipList.next());
    }

    @Test
    void hasNext() {
        ZipListStrict<Integer> zipList = new ZipListStrict<>();
        zipList.insert(5);
        zipList.insert(10);

        assertTrue(zipList.hasNext());
        zipList.next();
        assertFalse(zipList.hasNext());
    }

    @Test
    void prev() {
        ZipListStrict<Integer> zipList = new ZipListStrict<>();
        zipList.insert(5);
        zipList.insert(10);
        zipList.next();

        assertTrue(zipList.prev());
        assertEquals(5, zipList.extract().fromJust());

        assertFalse(zipList.prev());
    }

    @Test
    void hasPrev() {
        ZipListStrict<Integer> zipList = new ZipListStrict<>();
        zipList.insertR(5);
        zipList.insertR(10);
        zipList.next();

        assertTrue(zipList.hasPrev());
        zipList.prev();
        assertFalse(zipList.hasPrev());
    }

    @Test
    void insertR() {
        ZipListStrict<Integer> zipList = new ZipListStrict<>();
        zipList.insertR(5);
        zipList.insertR(10);

        assertEquals(2, zipList.size());
        assertEquals(10, zipList.extract().fromJust());
        zipList.prev();
        assertEquals(5, zipList.extract().fromJust());
    }

    @Test
    void deleteCurrent() {
        ZipListStrict<Integer> zipList = new ZipListStrict<>();
        zipList.insertR(5);
        zipList.insertR(10);

        assertEquals(2, zipList.size());
        assertEquals(10, zipList.deleteCurrent().fromJust());
        assertEquals(1, zipList.size());
        assertEquals(5, zipList.extract().fromJust());
    }

    @Test
    void delete() {
    }

    @Test
    void to() {
        ZipListStrict<Integer> zipList = new ZipListStrict<>();
        zipList.insertR(5);
        zipList.insertR(10);
        zipList.insertR(15);

        assertEquals(2, zipList.to(2));
        assertEquals(10, zipList.extract().fromJust());

        assertEquals(1, zipList.to(1));
        assertEquals(5, zipList.extract().fromJust());

        assertEquals(3, zipList.to(10)); // Target is clamped to size 3
        assertEquals(15, zipList.extract().fromJust());
    }

    @Test
    void mapM() {
        ZipListStrict<Integer> zipList = new ZipListStrict<>();
        zipList.insertR(5);
        zipList.insertR(10);
        zipList.insertR(15);

        StringBuilder result = new StringBuilder();
        zipList.mapM(val -> result.append(val).append(","));

        assertEquals("5,10,15,", result.toString());
    }
}