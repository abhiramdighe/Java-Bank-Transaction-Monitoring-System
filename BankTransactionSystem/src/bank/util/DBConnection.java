package bank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database configuration and connection management for MySQL.
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/bank_system?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";

    // ✅ Your MySQL password added
    private static final String PASSWORD = "PracSat@123";

    static {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver Loaded Successfully");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Test connection method
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Database connected successfully!");
            return conn.isValid(5);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}