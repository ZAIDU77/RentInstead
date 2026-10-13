package com.rentinstead.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/rentinstead"
            + "?useSSL=false"
            + "&serverTimezone=Asia/Kolkata"
            + "&allowPublicKeyRetrieval=true"
            + "&characterEncoding=UTF-8"
            + "&connectTimeout=10000"
            + "&socketTimeout=30000";

    private static final String USER =
            System.getenv("RENTINSTEAD_DB_USER");

    private static final String PASSWORD =
            System.getenv("RENTINSTEAD_DB_PASSWORD");

    private DBConnection() {
        // Utility class; do not instantiate.
    }

    public static Connection getConnection() throws SQLException {

        if (USER == null || USER.isBlank()
                || PASSWORD == null || PASSWORD.isBlank()) {

            throw new SQLException(
                    "Database credentials missing. Set "
                    + "RENTINSTEAD_DB_USER and "
                    + "RENTINSTEAD_DB_PASSWORD environment variables."
            );
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC driver not found. Check pom.xml dependencies.",
                    e
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection.isValid(5);
        } catch (SQLException e) {
            System.err.println(
                    "RentInstead database connection failed: "
                    + e.getMessage()
            );
            return false;
        }
    }
}
