package bank.service;

import bank.config.DatabaseConfig;
import bank.model.Transaction;
import bank.model.User;
import bank.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TransactionService {
                // Fetch receiver User object by username (for notification)
                public User getReceiverUser(String username) {
                    return authRepository.getUserByUsername(username);
                }
            public Map<String, Object> getTransactionStats() {
                Map<String, Object> stats = new HashMap<>();
                String sql = "SELECT COUNT(*) as count, SUM(amount) as total FROM Transactions";
                String sqlType = "SELECT type, COUNT(*) as cnt FROM Transactions GROUP BY type";
                try (Connection conn = DatabaseConfig.getConnection()) {
                    if (conn == null) return stats;
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                stats.put("count", rs.getInt("count"));
                                stats.put("total", rs.getDouble("total"));
                            }
                        }
                    }
                    try (PreparedStatement stmt2 = conn.prepareStatement(sqlType)) {
                        try (ResultSet rs2 = stmt2.executeQuery()) {
                            while (rs2.next()) {
                                stats.put(rs2.getString("type").toLowerCase(), rs2.getInt("cnt"));
                            }
                        }
                    }
                } catch (SQLException e) {
                    logger.error("Error fetching transaction stats: ", e);
                }
                return stats;
            }
        public List<Transaction> getAllTransactions() {
            List<Transaction> txs = new ArrayList<>();
            String query = "SELECT id, user_id, type, amount, status FROM Transactions";
            try (Connection conn = DatabaseConfig.getConnection()) {
                if (conn == null) return txs;
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            txs.add(new Transaction(
                                rs.getString("id"),
                                rs.getString("user_id"),
                                rs.getString("type"),
                                rs.getDouble("amount"),
                                rs.getString("status")
                            ));
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("Error fetching transactions: ", e);
            }
            return txs;
        }
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final EmailService emailService = new EmailService();
    private final AuthRepository authRepository = new AuthRepository();

    public boolean deposit(User user, double amount) {
        return executeTransaction(user, amount, "DEPOSIT", null);
    }

    public boolean withdraw(User user, double amount) {
        return executeTransaction(user, amount, "WITHDRAW", null);
    }

    public boolean transfer(User sender, double amount, String targetUsername) {
        if (sender.getUsername().equals(targetUsername)) return false; // cannot transfer to self

        User receiver = authRepository.getUserByUsername(targetUsername);
        if (receiver == null || !"ACTIVE".equals(receiver.getStatus())) {
            return false;
        }

        return executeTransaction(sender, amount, "TRANSFER", receiver);
    }

    private boolean executeTransaction(User primaryUser, double amount, String type, User targetUser) {
        String balCheck = "SELECT balance FROM Accounts WHERE user_id = ?";
        String updateBal = "UPDATE Accounts SET balance = balance + ? WHERE user_id = ?";
        String logTx = "INSERT INTO Transactions (user_id, type, amount, status) VALUES (?, ?, ?, 'SUCCESS')";

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false); // Start atomicity

            try {
                double currentBal = 0.0;
                try (PreparedStatement chk = conn.prepareStatement(balCheck)) {
                    chk.setInt(1, primaryUser.getId());
                    try (ResultSet rs = chk.executeQuery()) {
                        if (rs.next()) currentBal = rs.getDouble("balance");
                    }
                }

                if (("WITHDRAW".equals(type) || "TRANSFER".equals(type)) && currentBal < amount) {
                    conn.rollback();
                    return false; // Insufficient Funds
                }

                // Apply logic
                try (PreparedStatement upd = conn.prepareStatement(updateBal)) {
                    upd.setDouble(1, "DEPOSIT".equals(type) ? amount : -amount);
                    upd.setInt(2, primaryUser.getId());
                    upd.executeUpdate();
                }

                if ("TRANSFER".equals(type) && targetUser != null) {
                    try (PreparedStatement updReceiver = conn.prepareStatement(updateBal)) {
                        updReceiver.setDouble(1, amount);
                        updReceiver.setInt(2, targetUser.getId());
                        updReceiver.executeUpdate();
                    }
                }

                try (PreparedStatement log = conn.prepareStatement(logTx)) {
                    log.setInt(1, primaryUser.getId());
                    log.setString(2, type);
                    log.setDouble(3, amount);
                    log.executeUpdate();

                    if ("TRANSFER".equals(type) && targetUser != null) {
                        try (PreparedStatement logRx = conn.prepareStatement(logTx)) {
                            logRx.setInt(1, targetUser.getId());
                            logRx.setString(2, "DEPOSIT"); // Internal log of receiving end
                            logRx.setDouble(3, amount);
                            logRx.executeUpdate();
                        }
                    }
                }

                conn.commit(); // Safely push transaction to DB


                // Send all transaction notifications as branded HTML emails
                String msg = "A <b>" + type + "</b> of <span style='color:#10b981;'>Rs. " + String.format("%.2f", amount) + "</span> was successful on your account.";
                String htmlBody = EmailTemplates.notificationTemplate(primaryUser.getUsername(), "Transaction Alert: " + type, msg);
                emailService.sendEmailAsync(primaryUser.getEmail(), "Transaction Alert: " + type, htmlBody);
                if ("TRANSFER".equals(type) && targetUser != null) {
                    String rxMsg = "You have received <span style='color:#10b981;'>Rs. " + String.format("%.2f", amount) + "</span> from <b>" + primaryUser.getUsername() + "</b>.";
                    String htmlBodyRx = EmailTemplates.notificationTemplate(targetUser.getUsername(), "Funds Received", rxMsg);
                    emailService.sendEmailAsync(targetUser.getEmail(), "Funds Received", htmlBodyRx);
                }

                return true;

            } catch (SQLException e) {
                conn.rollback();
                logger.error("Transaction failed & rolled back: ", e);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Database error during transaction execution", e);
            return false;
        }
    }
}