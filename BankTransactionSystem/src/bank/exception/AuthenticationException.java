package bank.exception;

public class AuthenticationException extends BankSystemException {
    private final int attemptsRemaining;
    public AuthenticationException(int attemptsRemaining) {
        super("Incorrect password. Attempts remaining: " + attemptsRemaining);
        this.attemptsRemaining = attemptsRemaining;
    }
    public int getAttemptsRemaining() { return attemptsRemaining; }
}
