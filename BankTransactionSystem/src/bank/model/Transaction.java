package bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaction {
    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String            transactionId;
    private final String            username;
    private final TransactionType   type;
    private final double            amount;
    private final TransactionStatus status;
    private final double            balanceAfter;
    private final LocalDateTime     timestamp;

    public Transaction(String username, TransactionType type, double amount,
                       TransactionStatus status, double balanceAfter, LocalDateTime timestamp) {
        this.transactionId = UUID.randomUUID().toString();
        this.username      = username;
        this.type          = type;
        this.amount        = amount;
        this.status        = status;
        this.balanceAfter  = balanceAfter;
        this.timestamp     = timestamp;
    }

    // Full constructor used when loading from file (preserves original ID and timestamp)
    public Transaction(String transactionId, String username, TransactionType type,
                       double amount, TransactionStatus status, double balanceAfter,
                       LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.username      = username;
        this.type          = type;
        this.amount        = amount;
        this.status        = status;
        this.balanceAfter  = balanceAfter;
        this.timestamp     = timestamp;
    }

    public String            getTransactionId() { return transactionId; }
    public String            getUsername()      { return username; }
    public TransactionType   getType()          { return type; }
    public double            getAmount()        { return amount; }
    public TransactionStatus getStatus()        { return status; }
    public double            getBalanceAfter()  { return balanceAfter; }
    public LocalDateTime     getTimestamp()     { return timestamp; }

    public String getFormattedTimestamp() {
        return timestamp.format(DISPLAY_FMT);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-10s Rs.%10.2f %-8s Balance: Rs.%.2f",
                getFormattedTimestamp(), type, amount, status, balanceAfter);
    }
}
