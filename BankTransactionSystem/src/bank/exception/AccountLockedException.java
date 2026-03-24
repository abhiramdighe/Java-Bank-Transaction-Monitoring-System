package bank.exception;

public class AccountLockedException extends BankSystemException {
    private final int hoursRemaining;
    public AccountLockedException(int hoursRemaining) {
        super("Account is locked. Try again after " + hoursRemaining + " hour(s).");
        this.hoursRemaining = hoursRemaining;
    }
    public int getHoursRemaining() { return hoursRemaining; }
}
