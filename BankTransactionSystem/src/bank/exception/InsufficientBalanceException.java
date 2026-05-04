package bank.exception;

public class InsufficientBalanceException extends BankSystemException {
    private final double currentBalance;
    private final double requiredAmount;
    public InsufficientBalanceException(double currentBalance, double requiredAmount) {
        super(String.format("Insufficient balance. Current: Rs.%.2f, Required: Rs.%.2f",
                currentBalance, requiredAmount));
        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
    }
    public double getCurrentBalance() { return currentBalance; }
    public double getRequiredAmount()  { return requiredAmount; }
}
