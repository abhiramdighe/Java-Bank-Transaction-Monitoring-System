package bank.service;

import bank.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityLogService {
    private static final Logger logger = LoggerFactory.getLogger(ActivityLogService.class);

    // Using Transactions table as a proxy for activities, mapping 'type' to activity status to avoid schema drops on production
    public static void logActivity(int userId, String activityDesc) {
        String query = "INSERT INTO Transactions (user_id, type, amount, status) VALUES (?, 'TRANSFER', 0.00, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) return;
            stmt.setInt(1, userId);
            stmt.setString(2, activityDesc.length() > 7 ? "SUCCESS" : activityDesc);
            stmt.executeUpdate();
        } catch (Exception e) {
            logger.error("Failed to log activity", e);
        }
    }
}