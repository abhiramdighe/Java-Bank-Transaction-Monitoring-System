package bank.service;

import bank.exception.InsufficientBalanceException;
import bank.exception.TransactionLimitException;
import bank.model.Transaction;
import bank.model.TransactionStatus;
import bank.model.TransactionType;
import bank.model.UserAccount;
import bank.repository.TransactionRepository;
import bank.repository.UserRepository;
import bank.util.EncryptionUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionService {

    private static final DateTimeFormatter RECEIPT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserRepository        userRepository;
    private final TransactionRepository transactionRepository;
    private       TransactionMonitorService monitorService;

    public TransactionService(UserRepository userRepository,
                               TransactionRepository transactionRepository) {
        if (userRepository == null)        throw new IllegalArgumentException("userRepository is null");
        if (transactionRepository == null) throw new IllegalArgumentException("transactionRepository is null");
        this.userRepository        = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void setMonitorService(TransactionMonitorService monitorService) {
        this.monitorService = monitorService;
    }

    // ── Deposit ────────────────────────────────────────────────────

    /**
     * Processes a DEPOSIT. C++ rule: amount <= Rs.50,000.
     */
    public Transaction processDeposit(UserAccount account, double amount) {
        validatePositiveAmount(amount);

        if (amount > UserAccount.MAX_DEPOSIT_AMOUNT) {
            Transaction rejected = build(account, TransactionType.DEPOSIT,
                    amount, TransactionStatus.REJECTED, account.getBalance());
            persist(rejected, account);
            throw new TransactionLimitException(amount, UserAccount.MAX_DEPOSIT_AMOUNT);
        }

        double newBalance = account.getBalance() + amount;
        updateAccount(account, newBalance, amount);

        Transaction tx = build(account, TransactionType.DEPOSIT,
                amount, TransactionStatus.APPROVED, newBalance);
        persist(tx, account);
        notifyMonitor(tx, account);
        return tx;
    }

    // ── Withdrawal ─────────────────────────────────────────────────

    /**
     * Processes a WITHDRAWAL. C++ rule: amount <= balance.
     */
    public Transaction processWithdrawal(UserAccount account, double amount) {
        validatePositiveAmount(amount);

        if (amount > account.getBalance()) {
            Transaction rejected = build(account, TransactionType.WITHDRAWAL,
                    amount, TransactionStatus.REJECTED, account.getBalance());
            persist(rejected, account);
            throw new InsufficientBalanceException(account.getBalance(), amount);
        }

        double newBalance = account.getBalance() - amount;
        updateAccount(account, newBalance, amount);

        Transaction tx = build(account, TransactionType.WITHDRAWAL,
                amount, TransactionStatus.APPROVED, newBalance);
        persist(tx, account);
        notifyMonitor(tx, account);
        return tx;
    }

    // ── Loan ───────────────────────────────────────────────────────

    /**
     * Processes a LOAN. C++ rule: balance >= amount * 10%.
     */
    public Transaction processLoan(UserAccount account, double amount) {
        validatePositiveAmount(amount);

        double required = amount * UserAccount.LOAN_BALANCE_RATIO;
        if (account.getBalance() < required) {
            Transaction rejected = build(account, TransactionType.LOAN,
                    amount, TransactionStatus.REJECTED, account.getBalance());
            persist(rejected, account);
            throw new InsufficientBalanceException(account.getBalance(), required);
        }

        double newBalance = account.getBalance() + amount;
        updateAccount(account, newBalance, amount);

        Transaction tx = build(account, TransactionType.LOAN,
                amount, TransactionStatus.APPROVED, newBalance);
        persist(tx, account);
        notifyMonitor(tx, account);
        return tx;
    }

    // ── History ────────────────────────────────────────────────────

    public List<Transaction> getTransactionsForUser(String username) {
        return transactionRepository.findByUsername(username);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // ── Receipt ────────────────────────────────────────────────────

    /**
     * Formats a receipt string. Mirrors C++ printReceiptTerminal().
     */
    public String formatReceipt(Transaction tx, UserAccount account) {
        String sep = "**************************************************";
        String maskedPhone = EncryptionUtil.maskShow(
                EncryptionUtil.decrypt(account.getPhoneEncrypted()), 3);
        String maskedEmail = EncryptionUtil.maskEmail(
                EncryptionUtil.decrypt(account.getEmailEncrypted()));

        return sep + "\n"
             + "           TRANSACTION RECEIPT\n"
             + sep + "\n"
             + String.format("Date/Time       : %s%n", tx.getTimestamp().format(RECEIPT_FMT))
             + String.format("Account Holder  : %s (%s)%n", account.getFullName(), account.getUsername())
             + String.format("Contact         : %s%n", maskedPhone)
             + String.format("Email           : %s%n", maskedEmail)
             + String.format("Transaction     : %s%n", tx.getType())
             + String.format("Amount          : Rs.%.2f%n", tx.getAmount())
             + String.format("Status          : %s%n", tx.getStatus())
             + String.format("Updated Balance : Rs.%.2f%n", tx.getBalanceAfter())
             + sep;
    }

    // ── Private helpers ────────────────────────────────────────────

    private Transaction build(UserAccount account, TransactionType type,
                               double amount, TransactionStatus status,
                               double balanceAfter) {
        return new Transaction(account.getUsername(), type, amount,
                status, balanceAfter, LocalDateTime.now());
    }

    private void persist(Transaction tx, UserAccount account) {
        account.addTransaction(tx);
        transactionRepository.save(tx);
    }

    private void updateAccount(UserAccount account, double newBalance, double amount) {
        account.setBalance(newBalance);
        account.setLastTransactionAmount(Math.abs(amount));
        account.setDailyTransactionCount(account.getDailyTransactionCount() + 1);
        userRepository.updateFinancialState(account.getUsername(), newBalance,
                Math.abs(amount), account.getDailyTransactionCount());
    }

    private void notifyMonitor(Transaction tx, UserAccount account) {
        if (monitorService != null) monitorService.evaluate(tx, account);
    }

    private void validatePositiveAmount(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be positive. Got: " + amount);
    }
}
