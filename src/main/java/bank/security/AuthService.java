package bank.security;

import org.mindrot.jbcrypt.BCrypt;
import java.util.Random;

public class AuthService {
    
    public static String hashPassword(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt(12));
    }
    
    public static boolean checkPassword(String plainText, String hashed) {
        if (hashed == null || !hashed.startsWith("$2a$")) return false;
        try {
            return BCrypt.checkpw(plainText, hashed);
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String generateOTP() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
