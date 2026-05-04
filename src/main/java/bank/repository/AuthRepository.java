package bank.repository;

import bank.config.DatabaseConfig;
import bank.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthRepository {
    private static final Logger logger = LoggerFactory.getLogger(AuthRepository.class);

    public boolean registerUser(String username, String passwordHash, String email, String phone, byte[] faceData) {
        String query = "INSERT INTO Users (username, password_hash, email, phone, role, status, face_data) VALUES (?, ?, ?, ?, 'USER', 'PENDING', ?)";
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, passwordHash);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setBytes(5, faceData);
                
                int rows = stmt.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e) {
            logger.error("Error inserting new pending user: ", e);
            return false;
        }
    }

    public User getUserByUsername(String username) {
        String query = "SELECT id, username, password_hash, email, phone, role, status FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) return null;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                            rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"),
                            rs.getString("email"), rs.getString("phone"), rs.getString("role"),
                            rs.getString("status")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user: ", e);
        }
        return null;
    }
    
    public byte[] getUserFaceData(int userId) {
        String query = "SELECT face_data FROM Users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) return null;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getBytes("face_data");
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user face data", e);
        }
        return null;
    }

    public String getAdminEmail() {
        String query = "SELECT email FROM Users WHERE role = 'ADMIN' LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            logger.error("Error retrieving admin email", e);
        }
        return "prachetasatapathy@gmail.com";
    }
}
