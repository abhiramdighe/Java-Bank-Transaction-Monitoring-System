package bank.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties props = new Properties();
    private static Dotenv dotenv;

    static {
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e) {
            logger.warn("Could not load .env file, continuing with environment variables or properties.");
        }
        
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                logger.warn("Could not find application.properties. Will use fallback defaults.");
            }
        } catch (Exception ex) {
            logger.error("Error loading properties", ex);
        }
    }

    public static Connection getConnection() {
        try {
            String url = getProperty("db.url", "jdbc:mysql://localhost:3306/smart_bank_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
            String user = getProperty("db.user", "root");
            String password = getProperty("db.password", "password");
            
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            logger.error("Database connection failed. Ensure MySQL is running.", e);
            return null;
        }
    }
    
    public static String getProperty(String key) {
        return getProperty(key, null);
    }
    
    public static String getProperty(String key, String defaultValue) {
        // 1. Check Dotenv (.env file)
        if (dotenv != null) {
            String envKey = key.replace(".", "_").toUpperCase();
            String val = dotenv.get(envKey);
            if (val != null) return val;
        }
        
        // 2. Check System Environment variables
        String envKey = key.replace(".", "_").toUpperCase();
        String sysEnv = System.getenv(envKey);
        if (sysEnv != null && !sysEnv.isEmpty()) {
            return sysEnv;
        }
        
        // 3. Check application.properties
        return props.getProperty(key, defaultValue);
    }
}
