package bank.service;

import bank.config.DatabaseConfig;
import bank.model.OTP;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

public class OTPService {
        private final EmailService emailService = new EmailService();
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 1;

    public String generateOTP() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }

    public OTP createAndStoreOTP(String email) {
        String otp = generateOTP();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) return null;
            String sql = "INSERT INTO OTP (email, otp, expiry_time) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, otp);
                stmt.setTimestamp(3, Timestamp.valueOf(expiry));
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
            // Send OTP email (HTML)
            String username = email.split("@")[0];
            String htmlBody = EmailTemplates.otpTemplate(username, "OTP Verification", 0.0, otp);
            emailService.sendEmailAsync(email, "Your OTP Code", htmlBody);
        return new OTP(email, otp, expiry);
    }

    public boolean verifyOTP(String email, String otp) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) return false;
            String sql = "SELECT otp, expiry_time FROM OTP WHERE email = ? ORDER BY id DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String dbOtp = rs.getString("otp");
                        Timestamp expiry = rs.getTimestamp("expiry_time");
                        LocalDateTime expiryTime = expiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        if (LocalDateTime.now().isAfter(expiryTime)) return false;
                        return dbOtp.equals(otp);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
