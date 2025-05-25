package org.treefx.model;

import org.junit.jupiter.api.Test;
import org.treefx.utils.ReadCredentials;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionDBTest {
    @Test
    void success() {
        try {
            var userPass = ReadCredentials.read();
            ConnectionDB connection = new ConnectionDB("localhost", "3306", userPass.fst(), userPass.snd(), "treefx");
            assertTrue(connection.success(), "The database connection was not successful. Verify your credentials and database configurations.");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("DATABASE_CREDENTIALS file format is invalid"), "Error: Invalid format in the DATABASE_CREDENTIALS file.");
        }
    }
}