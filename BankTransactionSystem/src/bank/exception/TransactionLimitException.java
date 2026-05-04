package bank.exception;

public class TransactionLimitException extends BankSystemException {
    private final double attemptedAmount;
    private final double limitAmount;
    public TransactionLimitException(double attemptedAmount, double limitAmount) {
        super(String.format("Transaction limit exceeded. Attempted: Rs.%.2f, Limit: Rs.%.2f",
                attemptedAmount, limitAmount));
        this.attemptedAmount = attemptedAmount;
        this.limitAmount     = limitAmount;
    }
    public double getAttemptedAmount() { return attemptedAmount; }
    public double getLimitAmount()     { return limitAmount; }
}
