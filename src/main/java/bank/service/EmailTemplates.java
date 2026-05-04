package bank.service;

public class EmailTemplates {
        public static String notificationTemplate(String username, String title, String message) {
            StringBuilder sb = new StringBuilder();
            sb.append("<div style=\"max-width:480px;margin:auto;background:#16213e;border-radius:12px;padding:32px 24px;font-family:'Segoe UI',Arial,sans-serif;color:#e2e8f0;box-shadow:0 4px 24px #0002;\">");
            sb.append("  <div style=\"text-align:center;margin-bottom:24px;\">");
            sb.append("    <img src='https://upload.wikimedia.org/wikipedia/commons/5/59/Empty.png' alt='Bank Logo' style='height:40px;margin-bottom:12px;'>");
            sb.append("    <h2 style=\"color:#3b82f6;margin:0;font-size:24px;\">").append(title).append("</h2>");
            sb.append("  </div>");
            sb.append("  <p style=\"font-size:16px;\">Dear <b>").append(username).append("</b>,</p>");
            sb.append("  <div style=\"font-size:15px;margin:18px 0 24px 0;\">").append(message).append("</div>");
            sb.append("  <a href=\"#\" style=\"display:inline-block;background:#3b82f6;color:#fff;padding:12px 32px;border-radius:6px;text-decoration:none;font-weight:bold;margin-bottom:18px;\">Login to Dashboard</a>");
            sb.append("  <p style=\"font-size:12px;color:#64748b;margin-top:32px;\">If you did not initiate this action, please contact support immediately.</p>");
            sb.append("</div>");
            return sb.toString();
        }
    public static String otpTemplate(String username, String type, double amount, String otp) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"max-width:480px;margin:auto;background:#16213e;border-radius:12px;padding:32px 24px;font-family:'Segoe UI',Arial,sans-serif;color:#e2e8f0;box-shadow:0 4px 24px #0002;\">");
        sb.append("  <div style=\"text-align:center;margin-bottom:24px;\">");
        sb.append("    <img src='https://upload.wikimedia.org/wikipedia/commons/5/59/Empty.png' alt='Bank Logo' style='height:40px;margin-bottom:12px;'>");
        sb.append("    <h2 style=\"color:#3b82f6;margin:0;font-size:24px;\">SmartBank Notification</h2>");
        sb.append("  </div>");
        sb.append("  <p style=\"font-size:16px;\">Dear <b>").append(username).append("</b>,</p>");
        sb.append("  <p style=\"font-size:15px;\">You are attempting to <b>").append(type).append("</b> <span style=\"color:#10b981;\">Rs. ").append(String.format("%.2f", amount)).append("</span>.</p>");
        if (otp != null && !otp.equals("-") && !otp.isEmpty()) {
            sb.append("  <div style=\"background:#232946;padding:18px 0;border-radius:8px;text-align:center;margin:24px 0;\">");
            sb.append("    <span style=\"font-size:17px;color:#94a3b8;\">Your OTP:</span>");
            sb.append("    <div style=\"font-size:28px;font-weight:bold;letter-spacing:4px;color:#3b82f6;\">").append(otp).append("</div>");
            sb.append("    <div style=\"font-size:12px;color:#64748b;\">Valid for 1 minute</div>");
            sb.append("  </div>");
        }
        sb.append("  <a href=\"#\" style=\"display:inline-block;background:#3b82f6;color:#fff;padding:12px 32px;border-radius:6px;text-decoration:none;font-weight:bold;margin-bottom:18px;\">Login to Dashboard</a>");
        sb.append("  <p style=\"font-size:12px;color:#64748b;margin-top:32px;\">If you did not initiate this transaction, please contact support immediately.<br>Never share your OTP with anyone.</p>");
        sb.append("</div>");
        return sb.toString();
    }
}
