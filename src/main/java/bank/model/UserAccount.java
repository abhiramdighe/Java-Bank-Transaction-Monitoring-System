package bank.model;

public class UserAccount {
    private String accountNumber;
    private String userId;
    private double balance;
    private String status;

    public UserAccount(String accountNumber, String userId, double balance, String status) {
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = balance;
        this.status = status;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getUserId() { return userId; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
}
