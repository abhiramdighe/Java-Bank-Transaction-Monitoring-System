package bank.service;

import bank.exception.AccountLockedException;
import bank.exception.AccountNotFoundException;
import bank.exception.AuthenticationException;
import bank.model.UserAccount;
import bank.repository.UserRepository;
import bank.util.EncryptionUtil;
import java.time.Duration;
import java.time.LocalDateTime;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        if (userRepository == null) throw new IllegalArgumentException("userRepository is null");
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user. Throws typed exceptions on any failure.
     * Mirrors C++ userLoginFlow() logic exactly.
     */
    public UserAccount authenticate(String username, String plainPassword) {
        UserAccount account = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException(username));

        checkLockStatus(account);

        String encryptedInput = EncryptionUtil.encrypt(plainPassword);
        String storedEncrypted = new String(account.getPasswordEncrypted());

        if (!encryptedInput.equals(storedEncrypted)) {
            recordFailedAttempt(account);
            int left = Math.max(0, UserAccount.MAX_FAILED_ATTEMPTS - account.getFailedLoginAttempts());
            throw new AuthenticationException(left);
        }

        resetSecurityState(account);
        return account;
    }

    /**
     * Authenticates and checks the ADMIN role.
     * Mirrors C++ adminFlow() logic.
     */
    public UserAccount authenticateAdmin(String username, String plainPassword) {
        UserAccount account = authenticate(username, plainPassword);
        if (!account.isAdmin()) {
            throw new SecurityException("Account '" + username + "' does not have admin privileges.");
        }
        return account;
    }

    /**
     * Checks whether an account is currently locked.
     * Auto-unlocks if the 24-hour window has expired.
     * Mirrors C++ canAttemptLogin().
     */
    public void checkLockStatus(UserAccount account) {
        if (!account.isLocked()) return;

        LocalDateTime unlockTime = account.getLockTimestamp()
                .plusHours(UserAccount.LOCK_DURATION_HOURS);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(unlockTime)) {
            resetSecurityState(account);
            return;
        }

        long minutesRemaining = Duration.between(now, unlockTime).toMinutes();
        int hoursLeft = (int) Math.ceil(minutesRemaining / 60.0);
        throw new AccountLockedException(Math.max(1, hoursLeft));
    }

    /**
     * Increments the failed-login counter; locks at MAX_FAILED_ATTEMPTS.
     * Mirrors C++ processFailedAttempt().
     */
    public void recordFailedAttempt(UserAccount account) {
        int newCount = account.getFailedLoginAttempts() + 1;
        account.setFailedLoginAttempts(newCount);
        LocalDateTime lockTime = null;
        if (newCount >= UserAccount.MAX_FAILED_ATTEMPTS) {
            lockTime = LocalDateTime.now();
            account.setLockTimestamp(lockTime);
        }
        userRepository.updateSecurityState(account.getUsername(), newCount, lockTime);
    }

    /**
     * Resets the failed-login counter and clears any lock.
     * Mirrors C++ resetFailedAttempts().
     */
    public void resetSecurityState(UserAccount account) {
        account.setFailedLoginAttempts(0);
        account.setLockTimestamp(null);
        userRepository.resetFailedAttempts(account.getUsername());
    }

    /**
     * Admin operation: forcibly unlocks any account.
     * Mirrors C++ adminUnlockUser().
     */
    public void adminUnlockAccount(String targetUsername) {
        UserAccount account = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new AccountNotFoundException(targetUsername));
        resetSecurityState(account);
    }

    public boolean verifyPassword(UserAccount account, String plainPassword) {
        if (account == null || plainPassword == null) return false;
        return EncryptionUtil.encrypt(plainPassword).equals(new String(account.getPasswordEncrypted()));
    }
}
