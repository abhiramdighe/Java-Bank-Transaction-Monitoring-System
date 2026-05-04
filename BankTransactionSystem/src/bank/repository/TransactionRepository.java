package bank.repository;

import bank.model.Transaction;
import bank.model.TransactionType;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    boolean           save(Transaction transaction);
    List<Transaction> findByUsername(String username);
    List<Transaction> findAll();
    List<Transaction> findByUsernameAndDateRange(String username, LocalDateTime from, LocalDateTime to);
    List<Transaction> findByType(TransactionType type);
    int               countTodayTransactions(String username);
}
