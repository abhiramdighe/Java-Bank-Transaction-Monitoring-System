package bank.exception;

public class AccountNotFoundException extends BankSystemException {
    private final String username;
    public AccountNotFoundException(String username) {
        super("Account not found: " + username);
        this.username = username;
    }
    public String getUsername() { return username; }
}
