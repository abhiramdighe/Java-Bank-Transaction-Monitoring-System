package bank.repository;

import bank.config.DatabaseConfig;
import bank.model.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AccountRepository.class);

    public List<UserAccount> getAllAccounts() {
        List<UserAccount> accounts = new ArrayList<>();
        String query = "SELECT id as account_number, user_id, balance, 'ACTIVE' as status FROM Accounts";
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) return accounts;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        accounts.add(new UserAccount(
                            rs.getString("account_number"),
                            rs.getString("user_id"),
                            rs.getDouble("balance"),
                            rs.getString("status")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching accounts: ", e);
        }
        return accounts;
    }

    public boolean createAccountForUser(int userId) {
        String query = "INSERT INTO Accounts (user_id, balance) VALUES (?, 0.00)";
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logger.error("Error creating account: ", e);
            return false;
        }
    }

    public double getBalance(int userId) {
        String query = "SELECT balance FROM Accounts WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) return 0.0;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return rs.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting balance: ", e);
        }
        return 0.0;
    }
}
