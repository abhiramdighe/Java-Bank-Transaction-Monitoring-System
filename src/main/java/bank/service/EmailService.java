package bank.service;

import bank.config.DatabaseConfig;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    /**
     * Sends an email asynchronously so the main JavaFX thread never blocks.
     */
    public void sendEmailAsync(String to, String subject, String body) {
        CompletableFuture.runAsync(() -> {
            logger.info("[EMAIL] Sending to {}...", to);

            String host = DatabaseConfig.getProperty("mail.smtp.host");
            String port = DatabaseConfig.getProperty("mail.smtp.port");
            String username = DatabaseConfig.getProperty("mail.username");
            String password = DatabaseConfig.getProperty("mail.password");

            if (host == null) host = "smtp.gmail.com";
            if (port == null) port = "587";
            if (username == null) username = "prachetasatapathy@gmail.com";
            if (password == null) password = "rcanwunwbsshjrik";

            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            final String authUser = username;
            final String authPass = password;

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(authUser, authPass);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(authUser, "Java Mail"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject);
                // If body starts with <!DOCTYPE or <div, treat as HTML
                if (body.trim().startsWith("<") && body.contains("</")) {
                    message.setContent(body, "text/html; charset=utf-8");
                } else {
                    message.setText(body);
                }

                Transport.send(message);
                logger.info("[EMAIL] Sent successfully to {}", to);
            } catch (Exception e) {
                logger.error("[EMAIL ERROR] Failed to send to {}: {}", to, e.getMessage());
            }
        });
    }
}
