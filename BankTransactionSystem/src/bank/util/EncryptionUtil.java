package bank.util;

public final class EncryptionUtil {

    public static final int DEFAULT_KEY = 5;

    private EncryptionUtil() {}

    public static String encrypt(String plainText) {
        return encrypt(plainText, DEFAULT_KEY);
    }

    public static String encrypt(String plainText, int key) {
        if (plainText == null) throw new IllegalArgumentException("plainText must not be null");
        char[] chars = plainText.toCharArray();
        for (int i = 0; i < chars.length; i++) chars[i] = (char)(chars[i] + key);
        return new String(chars);
    }

    public static String decrypt(String encryptedText) {
        return decrypt(encryptedText, DEFAULT_KEY);
    }

    public static String decrypt(String encryptedText, int key) {
        if (encryptedText == null) throw new IllegalArgumentException("encryptedText must not be null");
        char[] chars = encryptedText.toCharArray();
        for (int i = 0; i < chars.length; i++) chars[i] = (char)(chars[i] - key);
        return new String(chars);
    }

    public static String encryptChars(char[] plainChars) {
        return encryptChars(plainChars, DEFAULT_KEY);
    }

    public static String encryptChars(char[] plainChars, int key) {
        if (plainChars == null) throw new IllegalArgumentException("plainChars must not be null");
        char[] encrypted = new char[plainChars.length];
        for (int i = 0; i < plainChars.length; i++) encrypted[i] = (char)(plainChars[i] + key);
        return new String(encrypted);
    }

    public static String maskShow(String plain, int showLast) {
        if (plain == null || plain.isEmpty()) return "";
        int n = plain.length();
        int last = Math.min(showLast, n);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n - last; i++) sb.append('*');
        sb.append(plain, n - last, n);
        return sb.toString();
    }

    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) return "";
        int atPos = email.indexOf('@');
        if (atPos == -1) return maskShow(email, 4);
        String local  = email.substring(0, atPos);
        String domain = email.substring(atPos);
        int show  = Math.min(3, local.length());
        int stars = Math.max(1, local.length() - show);
        StringBuilder sb = new StringBuilder();
        sb.append(local, 0, show);
        for (int i = 0; i < stars; i++) sb.append('*');
        sb.append(domain);
        return sb.toString();
    }
}
