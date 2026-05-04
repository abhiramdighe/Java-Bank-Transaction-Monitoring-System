package bank.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.\\-]+@([\\w.\\-]+\\.[a-zA-Z]+)$");
    private static final List<String> ALLOWED_DOMAINS =
            Arrays.asList("gmail.com","yahoo.com","outlook.com","hotmail.com","bank.com");

    private ValidationUtil() {}

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidEmailDomain(String email) {
        if (email == null || email.isEmpty()) return false;
        Matcher m = EMAIL_PATTERN.matcher(email.trim());
        if (!m.matches()) return false;
        return ALLOWED_DOMAINS.contains(m.group(1).toLowerCase());
    }

    public static boolean isValidGender(String gender) {
        if (gender == null || gender.isEmpty()) return false;
        String l = gender.trim().toLowerCase();
        return l.equals("male") || l.equals("m") ||
               l.equals("female") || l.equals("f") ||
               l.equals("other") || l.equals("o");
    }

    public static String normalizeGender(String gender) {
        if (gender == null) return "Other";
        switch (gender.trim().toLowerCase()) {
            case "m": case "male":   return "Male";
            case "f": case "female": return "Female";
            default:                 return "Other";
        }
    }

    public static boolean isValidAge(int age)             { return age >= 18 && age <= 100; }
    public static boolean isPositiveAmount(double amount) { return amount > 0.0; }
    public static boolean isValidUsername(String u)       { return u != null && !u.trim().isEmpty(); }
    public static boolean isValidPassword(String p)       { return p != null && !p.isEmpty(); }
}
