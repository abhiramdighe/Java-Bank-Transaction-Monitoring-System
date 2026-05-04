package bank.repository;

import bank.model.UserAccount;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<UserAccount>     findAll();
    Optional<UserAccount> findByUsername(String username);
    boolean               existsByUsername(String username);
    boolean               save(UserAccount account);
    boolean               delete(String username);
    boolean               resetFailedAttempts(String username);
    boolean               updateSecurityState(String username, int failedAttempts, LocalDateTime lockTimestamp);
    boolean               updateFinancialState(String username, double newBalance, double lastAmount, int dailyCount);
}
