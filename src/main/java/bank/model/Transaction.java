package bank.model;

public class Transaction {
    private String transactionId;
    private String userId;
    private String type;
    private double amount;
    private String status;

    public Transaction(String transactionId, String userId, String type, double amount, String status) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.status = status;
    }

    public String getTransactionId() { return transactionId; }
    public String getUserId() { return userId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
}
