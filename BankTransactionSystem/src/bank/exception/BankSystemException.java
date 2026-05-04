package bank.exception;

public class BankSystemException extends RuntimeException {
    public BankSystemException(String message) { super(message); }
    public BankSystemException(String message, Throwable cause) { super(message, cause); }
}
