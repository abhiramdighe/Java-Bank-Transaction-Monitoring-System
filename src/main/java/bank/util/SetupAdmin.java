package bank.util;

import bank.config.DatabaseConfig;
import bank.security.AuthService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SetupAdmin {
    public static void main(String[] args) {
        System.out.println("Starting Admin Setup...");
        String adminUser = "prachetasatapathy";
        String adminPass = "prachetasatapathy"; // Will be BCrypt hashed
        String adminEmail = "prachetasatapathy@gmail.com";
        String adminPhone = "0000000000";

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("Failed to connect to the Database. Make sure MySQL is running and database_schema.sql has been imported.");
                return;
            }

            // Check if admin already exists
            String checkQuery = "SELECT id FROM Users WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, adminUser);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Admin user '" + adminUser + "' already exists in the database.");
                        return;
                    }
                }
            }

            // Insert new Admin
            String insertQuery = "INSERT INTO Users (username, password_hash, email, phone, role, status) VALUES (?, ?, ?, ?, 'ADMIN', 'ACTIVE')";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, adminUser);
                insertStmt.setString(2, AuthService.hashPassword(adminPass));
                insertStmt.setString(3, adminEmail);
                insertStmt.setString(4, adminPhone);
                
                int rows = insertStmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("SUCCESS: Admin user '" + adminUser + "' has been created!");
                } else {
                    System.err.println("FAILED: Admin user insertion returned 0 rows.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
