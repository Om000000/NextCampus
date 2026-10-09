package com.college.sms.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite connection helper — no MySQL / Workbench required.
 * Database file: database/college_sms.db
 */
public class DBConnection {

    private static boolean schemaReady = false;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "SQLite JDBC driver not found. Place sqlite-jdbc jar in java/lib/", e);
        }

        Path dbFile = resolveDbFile();
        try {
            Files.createDirectories(dbFile.getParent());
        } catch (Exception e) {
            throw new SQLException("Could not create database folder: " + dbFile.getParent(), e);
        }

        String url = "jdbc:sqlite:" + dbFile.toAbsolutePath();
        Connection con = DriverManager.getConnection(url);
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA journal_mode = WAL");
            st.execute("PRAGMA synchronous = NORMAL");
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA busy_timeout = 30000");
        }
        if (!schemaReady) {
            SchemaInitializer.createTablesAndSampleData(con);
            schemaReady = true;
        }
        return con;
    }

    static Path resolveDbFile() {
        Path[] candidates = {
                Paths.get("database", "college_sms.db"),
                Paths.get("..", "database", "college_sms.db"),
                Paths.get("college_sms.db")
        };
        for (Path p : candidates) {
            if (Files.exists(p)) {
                return p.toAbsolutePath().normalize();
            }
        }
        // Default when the file does not exist yet
        Path preferred = Paths.get("..", "database", "college_sms.db");
        if (Files.isDirectory(Paths.get("src")) || Files.isDirectory(Paths.get("out"))) {
            return preferred.toAbsolutePath().normalize();
        }
        return Paths.get("database", "college_sms.db").toAbsolutePath().normalize();
    }
}
