package bank.repository;

import bank.model.UserAccount;
import bank.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL implementation of UserRepository.
 * Stores user accounts in the 'users' table.
 */
public class MySQLUserRepository implements UserRepository {

    @Override
    public List<UserAccount> findAll() {
        List<UserAccount> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UserAccount user = mapResultSetToUserAccount(rs);
                if (user != null) {
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
        }

        return users;
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserAccount user = mapResultSetToUserAccount(rs);
                    return Optional.ofNullable(user);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean save(UserAccount account) {
        String sql = """
            INSERT INTO users (username, password, full_name, gender, age,
                             phone_encrypted, email_encrypted, role, balance,
                             last_transaction_amount, daily_transaction_count,
                             failed_login_attempts, lock_timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                password = VALUES(password),
                full_name = VALUES(full_name),
                gender = VALUES(gender),
                age = VALUES(age),
                phone_encrypted = VALUES(phone_encrypted),
                email_encrypted = VALUES(email_encrypted),
                role = VALUES(role),
                balance = VALUES(balance),
                last_transaction_amount = VALUES(last_transaction_amount),
                daily_transaction_count = VALUES(daily_transaction_count),
                failed_login_attempts = VALUES(failed_login_attempts),
                lock_timestamp = VALUES(lock_timestamp)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getUsername());
            stmt.setString(2, new String(account.getPasswordEncrypted()));
            stmt.setString(3, account.getFullName());
            stmt.setString(4, account.getGender());
            stmt.setInt(5, account.getAge());
            stmt.setString(6, account.getPhoneEncrypted());
            stmt.setString(7, account.getEmailEncrypted());
            stmt.setString(8, account.getRole().name());
            stmt.setDouble(9, account.getBalance());
            stmt.setDouble(10, account.getLastTransactionAmount());
            stmt.setInt(11, account.getDailyTransactionCount());
            stmt.setInt(12, account.getFailedLoginAttempts());

            if (account.getLockTimestamp() != null) {
                stmt.setTimestamp(13, Timestamp.valueOf(account.getLockTimestamp()));
            } else {
                stmt.setNull(13, Types.TIMESTAMP);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean resetFailedAttempts(String username) {
        return updateSecurityState(username, 0, null);
    }

    @Override
    public boolean updateSecurityState(String username, int failedAttempts, LocalDateTime lockTimestamp) {
        String sql = "UPDATE users SET failed_login_attempts = ?, lock_timestamp = ? WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, failedAttempts);
            if (lockTimestamp != null) {
                stmt.setTimestamp(2, Timestamp.valueOf(lockTimestamp));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }
            stmt.setString(3, username);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating security state: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateFinancialState(String username, double newBalance, double lastAmount, int dailyCount) {
        String sql = "UPDATE users SET balance = ?, last_transaction_amount = ?, daily_transaction_count = ? WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.setDouble(2, Math.abs(lastAmount));
            stmt.setInt(3, dailyCount);
            stmt.setString(4, username);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating financial state: " + e.getMessage());
            return false;
        }
    }

    private UserAccount mapResultSetToUserAccount(ResultSet rs) throws SQLException {
        try {
            String username = rs.getString("username");
            char[] passwordEncrypted = rs.getString("password").toCharArray();
            String fullName = rs.getString("full_name");
            String gender = rs.getString("gender");
            int age = rs.getInt("age");
            String phoneEncrypted = rs.getString("phone_encrypted");
            String emailEncrypted = rs.getString("email_encrypted");
            UserAccount.Role role = UserAccount.Role.valueOf(rs.getString("role"));
            double balance = rs.getDouble("balance");
            double lastAmount = rs.getDouble("last_transaction_amount");
            int dailyCount = rs.getInt("daily_transaction_count");
            int failedAttempts = rs.getInt("failed_login_attempts");

            Timestamp lockTs = rs.getTimestamp("lock_timestamp");
            LocalDateTime lockTimestamp = lockTs != null ? lockTs.toLocalDateTime() : null;

            return new UserAccount(username, passwordEncrypted, fullName, gender, age,
                    phoneEncrypted, emailEncrypted, role, balance, lastAmount,
                    dailyCount, failedAttempts, lockTimestamp);

        } catch (Exception e) {
            System.err.println("Error mapping result set to UserAccount: " + e.getMessage());
            return null;
        }
    }
}
