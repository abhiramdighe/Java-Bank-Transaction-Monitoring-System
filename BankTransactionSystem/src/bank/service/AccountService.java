package bank.service;

import bank.exception.AccountNotFoundException;
import bank.exception.ValidationException;
import bank.model.UserAccount;
import bank.repository.UserRepository;
import bank.util.EncryptionUtil;
import bank.util.ValidationUtil;
import java.util.List;
import java.util.Optional;

public class AccountService {

    private final UserRepository userRepository;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD  = "admin123";
    private static final String ADMIN_FULLNAME  = "Admin User";
    private static final String ADMIN_PHONE     = "9876543210";
    private static final String ADMIN_EMAIL     = "admin@bank.com";

    public AccountService(UserRepository userRepository) {
        if (userRepository == null) throw new IllegalArgumentException("userRepository is null");
        this.userRepository = userRepository;
    }

    /**
     * Validates all fields then creates and persists a new user account.
     * Mirrors C++ createUserFlow() validation sequence exactly.
     */
    public UserAccount createAccount(String username, String password,
                                      String fullName, String gender,
                                      int age, String phone, String email) {

        if (!ValidationUtil.isValidUsername(username))
            throw new ValidationException("username", "Username cannot be empty.");
        if (userRepository.existsByUsername(username))
            throw new ValidationException("username", "Username '" + username + "' is already taken.");
        if (!ValidationUtil.isValidPassword(password))
            throw new ValidationException("password", "Password cannot be empty.");
        if (fullName == null || fullName.trim().isEmpty())
            throw new ValidationException("fullName", "Full name cannot be empty.");
        if (!ValidationUtil.isValidGender(gender))
            throw new ValidationException("gender", "Invalid gender. Use Male/Female/Other or M/F/O.");
        if (!ValidationUtil.isValidAge(age))
            throw new ValidationException("age", "Age must be between 18 and 100.");
        if (!ValidationUtil.isValidPhone(phone))
            throw new ValidationException("phone", "Phone must be exactly 10 digits.");
        if (!ValidationUtil.isValidEmailDomain(email))
            throw new ValidationException("email",
                    "Unsupported email domain. Allowed: gmail.com, yahoo.com, outlook.com, hotmail.com, bank.com");

        String normalizedGender  = ValidationUtil.normalizeGender(gender);
        char[] encryptedPassword = EncryptionUtil.encrypt(password).toCharArray();
        String encryptedPhone    = EncryptionUtil.encrypt(phone);
        String encryptedEmail    = EncryptionUtil.encrypt(email);

        UserAccount newAccount = new UserAccount(
                username.trim(), encryptedPassword, fullName.trim(),
                normalizedGender, age, encryptedPhone, encryptedEmail);

        if (!userRepository.save(newAccount))
            throw new RuntimeException("Failed to save account: " + username);

        return newAccount;
    }

    public UserAccount getAccount(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException(username));
    }

    public Optional<UserAccount> findAccount(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserAccount> getAllAccounts() {
        return userRepository.findAll();
    }

    /**
     * Ensures the default admin account exists at startup.
     * Mirrors C++ ensureFiles() admin seed.
     */
    public void ensureAdminExists() {
        if (!userRepository.existsByUsername(ADMIN_USERNAME)) {
            char[] encPass  = EncryptionUtil.encrypt(ADMIN_PASSWORD).toCharArray();
            String encPhone = EncryptionUtil.encrypt(ADMIN_PHONE);
            String encEmail = EncryptionUtil.encrypt(ADMIN_EMAIL);

            UserAccount admin = new UserAccount(
                    ADMIN_USERNAME, encPass, ADMIN_FULLNAME, "Male", 30,
                    encPhone, encEmail,
                    UserAccount.Role.ADMIN, 5000.00, 0.0, 0, 0, null);

            userRepository.save(admin);
        }
    }

    public String getMaskedPhone(UserAccount account) {
        String plain = EncryptionUtil.decrypt(account.getPhoneEncrypted());
        return EncryptionUtil.maskShow(plain, 3);
    }

    public String getMaskedEmail(UserAccount account) {
        String plain = EncryptionUtil.decrypt(account.getEmailEncrypted());
        return EncryptionUtil.maskEmail(plain);
    }
}
