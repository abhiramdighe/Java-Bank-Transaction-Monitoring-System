package bank.repository;

import bank.model.UserAccount;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Stores users in a pipe-delimited flat file (users.txt).
 * Format (13 fields):
 *   username|password_enc|fullName|gender|age|phone_enc|email_enc|
 *   balance|lastAmount|dailyCount|failedAttempts|lockTimestamp|role
 *
 * lockTimestamp: 0 = not locked, otherwise Unix epoch seconds (UTC).
 * Compatible with the C++ system's 12-field format (role defaults to ADMIN
 * for "admin" username, USER otherwise).
 */
public class FlatFileUserRepository implements UserRepository {

    private static final String USERS_FILE = "users.txt";

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    @Override
    public List<UserAccount> findAll() {
        List<UserAccount> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                UserAccount u = parseRecord(line);
                if (u != null) users.add(u);
            }
        } catch (IOException e) {
            System.err.println("Error reading " + USERS_FILE + ": " + e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    // ---------------------------------------------------------------
    // Write
    // ---------------------------------------------------------------

    @Override
    public boolean save(UserAccount account) {
        List<UserAccount> users = findAll();
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(account.getUsername())) {
                users.set(i, account);
                found = true;
                break;
            }
        }
        if (!found) users.add(account);
        return writeAll(users);
    }

    @Override
    public boolean delete(String username) {
        List<UserAccount> users = findAll();
        boolean removed = users.removeIf(u -> u.getUsername().equals(username));
        return removed && writeAll(users);
    }

    @Override
    public boolean resetFailedAttempts(String username) {
        return updateSecurityState(username, 0, null);
    }

    @Override
    public boolean updateSecurityState(String username, int failedAttempts,
                                        LocalDateTime lockTimestamp) {
        Optional<UserAccount> opt = findByUsername(username);
        if (!opt.isPresent()) return false;
        UserAccount account = opt.get();
        account.setFailedLoginAttempts(failedAttempts);
        account.setLockTimestamp(lockTimestamp);
        return save(account);
    }

    @Override
    public boolean updateFinancialState(String username, double newBalance,
                                         double lastAmount, int dailyCount) {
        Optional<UserAccount> opt = findByUsername(username);
        if (!opt.isPresent()) return false;
        UserAccount account = opt.get();
        account.setBalance(newBalance);
        account.setLastTransactionAmount(Math.abs(lastAmount));
        account.setDailyTransactionCount(dailyCount);
        return save(account);
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------

    private boolean writeAll(List<UserAccount> users) {
        File tempFile = new File(USERS_FILE + ".tmp");
        try (PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {
            for (UserAccount u : users) pw.println(serializeRecord(u));
        } catch (IOException e) {
            System.err.println("Error writing users: " + e.getMessage());
            return false;
        }
        File mainFile = new File(USERS_FILE);
        if (mainFile.exists()) mainFile.delete();
        return tempFile.renameTo(mainFile);
    }

    /**
     * Parses a pipe-delimited line into a UserAccount.
     * Supports both 12-field (C++ format) and 13-field (Java format).
     */
    private UserAccount parseRecord(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 12) {
            System.err.println("Skipping malformed record: " + line);
            return null;
        }
        try {
            String   username          = parts[0];
            char[]   passwordEncrypted = parts[1].toCharArray();
            String   fullName          = parts[2];
            String   gender            = parts[3];
            int      age               = Integer.parseInt(parts[4].trim());
            String   phoneEncrypted    = parts[5];
            String   emailEncrypted    = parts[6];
            double   balance           = Double.parseDouble(parts[7].trim());
            double   lastAmount        = Double.parseDouble(parts[8].trim());
            int      dailyCount        = Integer.parseInt(parts[9].trim());
            int      failedAttempts    = Integer.parseInt(parts[10].trim());
            long     lockEpoch         = Long.parseLong(parts[11].trim());
            LocalDateTime lockTimestamp = lockEpoch == 0L ? null :
                    LocalDateTime.ofEpochSecond(lockEpoch, 0, ZoneOffset.UTC);

            // Role: field 12 if present; else infer from username
            UserAccount.Role role;
            if (parts.length > 12 && !parts[12].trim().isEmpty()) {
                role = UserAccount.Role.valueOf(parts[12].trim());
            } else {
                role = "admin".equalsIgnoreCase(username)
                       ? UserAccount.Role.ADMIN : UserAccount.Role.USER;
            }

            return new UserAccount(username, passwordEncrypted, fullName, gender, age,
                    phoneEncrypted, emailEncrypted, role, balance, lastAmount,
                    dailyCount, failedAttempts, lockTimestamp);

        } catch (Exception e) {
            System.err.println("Error parsing record: " + line + " -> " + e.getMessage());
            return null;
        }
    }

    private String serializeRecord(UserAccount u) {
        long lockEpoch = u.getLockTimestamp() == null ? 0L :
                u.getLockTimestamp().toEpochSecond(ZoneOffset.UTC);
        return String.join("|",
                u.getUsername(),
                new String(u.getPasswordEncrypted()),
                u.getFullName(),
                u.getGender(),
                String.valueOf(u.getAge()),
                u.getPhoneEncrypted(),
                u.getEmailEncrypted(),
                String.format("%.2f", u.getBalance()),
                String.format("%.2f", u.getLastTransactionAmount()),
                String.valueOf(u.getDailyTransactionCount()),
                String.valueOf(u.getFailedLoginAttempts()),
                String.valueOf(lockEpoch),
                u.getRole().name()
        );
    }
}
