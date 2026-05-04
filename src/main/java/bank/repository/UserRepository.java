package bank.repository;

import bank.config.DatabaseConfig;
import bank.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public List<User> getPendingUsers() {
        return getUsers("PENDING");
    }

    public List<User> getUsers(String statusFilter) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        if (statusFilter != null && !statusFilter.equals("ALL")) {
            query += " WHERE status = ?";
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
             if (conn == null) {
                 logger.error("No Database Connection in UserRepository.");
                 return users;
             }
             try (PreparedStatement stmt = conn.prepareStatement(query)) {
                 if (statusFilter != null && !statusFilter.equals("ALL")) {
                     stmt.setString(1, statusFilter);
                 }
                 try (ResultSet rs = stmt.executeQuery()) {
                     while (rs.next()) {
                         users.add(new User(
                             rs.getInt("id"),
                             rs.getString("username"),
                             rs.getString("password_hash"),
                             rs.getString("email"),
                             rs.getString("phone"),
                             rs.getString("role"),
                             rs.getString("status")
                         ));
                     }
                 }
             }
        } catch (SQLException e) {
            logger.error("Error fetching users", e);
        }
        return users;
    }

    public boolean updateUserStatus(int userId, String status) {
        String query = "UPDATE Users SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) return false;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, status);
                stmt.setInt(2, userId);
                int updated = stmt.executeUpdate();
                return updated > 0;
            }
        } catch (SQLException e) {
            logger.error("Error updating user status", e);
        }
        return false;
    }
}
