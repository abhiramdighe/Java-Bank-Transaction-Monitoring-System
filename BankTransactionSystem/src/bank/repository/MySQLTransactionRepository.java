package bank.repository;

import bank.model.Transaction;
import bank.model.TransactionStatus;
import bank.model.TransactionType;
import bank.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL implementation of TransactionRepository.
 * Stores transactions in the 'transactions' table.
 */
public class MySQLTransactionRepository implements TransactionRepository {

    @Override
    public boolean save(Transaction transaction) {
        String sql = """
            INSERT INTO transactions (transaction_id, username, type, amount, status, balance_after, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaction.getTransactionId());
            stmt.setString(2, transaction.getUsername());
            stmt.setString(3, transaction.getType().name());
            stmt.setDouble(4, transaction.getAmount());
            stmt.setString(5, transaction.getStatus().name());
            stmt.setDouble(6, transaction.getBalanceAfter());
            stmt.setTimestamp(7, Timestamp.valueOf(transaction.getTimestamp()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Transaction> findByUsername(String username) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE username = ? ORDER BY timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = mapResultSetToTransaction(rs);
                    if (tx != null) {
                        transactions.add(tx);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding transactions by username: " + e.getMessage());
        }

        return transactions;
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Transaction tx = mapResultSetToTransaction(rs);
                if (tx != null) {
                    transactions.add(tx);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding all transactions: " + e.getMessage());
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByUsernameAndDateRange(String username, LocalDateTime from, LocalDateTime to) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = """
            SELECT * FROM transactions
            WHERE username = ? AND timestamp BETWEEN ? AND ?
            ORDER BY timestamp DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setTimestamp(2, Timestamp.valueOf(from));
            stmt.setTimestamp(3, Timestamp.valueOf(to));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = mapResultSetToTransaction(rs);
                    if (tx != null) {
                        transactions.add(tx);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding transactions by date range: " + e.getMessage());
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE type = ? ORDER BY timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = mapResultSetToTransaction(rs);
                    if (tx != null) {
                        transactions.add(tx);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding transactions by type: " + e.getMessage());
        }

        return transactions;
    }

    @Override
    public int countTodayTransactions(String username) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999);

        String sql = """
            SELECT COUNT(*) FROM transactions
            WHERE username = ? AND DATE(timestamp) = CURDATE()
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error counting today's transactions: " + e.getMessage());
        }

        return 0;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        try {
            String transactionId = rs.getString("transaction_id");
            String username = rs.getString("username");
            TransactionType type = TransactionType.fromString(rs.getString("type"));
            double amount = rs.getDouble("amount");
            TransactionStatus status = TransactionStatus.fromString(rs.getString("status"));
            double balanceAfter = rs.getDouble("balance_after");
            LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();

            return new Transaction(transactionId, username, type, amount, status, balanceAfter, timestamp);

        } catch (Exception e) {
            System.err.println("Error mapping result set to Transaction: " + e.getMessage());
            return null;
        }
    }
}
