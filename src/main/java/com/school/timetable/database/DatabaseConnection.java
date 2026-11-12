package com.school.timetable.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple local database connection helper for MySQL.
 * Edit the USER and PASSWORD constants below to match your local MySQL setup.
 */
public final class DatabaseConnection {

    // Standard local JDBC connection URL
    private static final String URL = "jdbc:mysql://gosoftcloud.com:3306/school_timetable_db";
    
    // Default local MySQL credentials (default for XAMPP is 'root' with empty password)
    private static final String USER = "root";
    private static final String PASSWORD = "79aXC&n2Cx7&"; // Change to your MySQL password if one is set

    private DatabaseConnection() {
        // Utility class
    }

    /**
     * Connect to the local MySQL database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Helper to test whether the local MySQL database is reachable.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
