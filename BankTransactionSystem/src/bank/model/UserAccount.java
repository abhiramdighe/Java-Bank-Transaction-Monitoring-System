package bank.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserAccount {

    public enum Role { ADMIN, USER }

    public static final int    MAX_FAILED_ATTEMPTS  = 3;
    public static final int    LOCK_DURATION_HOURS  = 24;
    public static final double MAX_DEPOSIT_AMOUNT   = 50_000.0;
    public static final double LOAN_BALANCE_RATIO   = 0.10;

    private String        username;
    private char[]        passwordEncrypted;
    private String        fullName;
    private String        gender;
    private int           age;
    private String        phoneEncrypted;
    private String        emailEncrypted;
    private Role          role;
    private double        balance;
    private double        lastTransactionAmount;
    private int           dailyTransactionCount;
    private int           failedLoginAttempts;
    private LocalDateTime lockTimestamp;
    private List<Transaction> transactionHistory;

    public UserAccount(String username, char[] passwordEncrypted, String fullName,
                       String gender, int age, String phoneEncrypted, String emailEncrypted,
                       Role role, double balance, double lastTransactionAmount,
                       int dailyTransactionCount, int failedLoginAttempts,
                       LocalDateTime lockTimestamp) {
        this.username               = username;
        this.passwordEncrypted      = passwordEncrypted;
        this.fullName               = fullName;
        this.gender                 = gender;
        this.age                    = age;
        this.phoneEncrypted         = phoneEncrypted;
        this.emailEncrypted         = emailEncrypted;
        this.role                   = role;
        this.balance                = balance;
        this.lastTransactionAmount  = lastTransactionAmount;
        this.dailyTransactionCount  = dailyTransactionCount;
        this.failedLoginAttempts    = failedLoginAttempts;
        this.lockTimestamp          = lockTimestamp;
        this.transactionHistory     = new ArrayList<>();
    }

    // New user constructor — defaults all security/financial state to safe values
    public UserAccount(String username, char[] passwordEncrypted, String fullName,
                       String gender, int age, String phoneEncrypted, String emailEncrypted) {
        this(username, passwordEncrypted, fullName, gender, age,
             phoneEncrypted, emailEncrypted,
             Role.USER, 0.0, 0.0, 0, 0, null);
    }

    // Getters
    public String        getUsername()              { return username; }
    public char[]        getPasswordEncrypted()      { return passwordEncrypted; }
    public String        getFullName()               { return fullName; }
    public String        getGender()                 { return gender; }
    public int           getAge()                    { return age; }
    public String        getPhoneEncrypted()         { return phoneEncrypted; }
    public String        getEmailEncrypted()         { return emailEncrypted; }
    public Role          getRole()                   { return role; }
    public double        getBalance()                { return balance; }
    public double        getLastTransactionAmount()  { return lastTransactionAmount; }
    public int           getDailyTransactionCount()  { return dailyTransactionCount; }
    public int           getFailedLoginAttempts()    { return failedLoginAttempts; }
    public LocalDateTime getLockTimestamp()           { return lockTimestamp; }
    public List<Transaction> getTransactionHistory() { return new ArrayList<>(transactionHistory); }

    // Setters
    public void setBalance(double balance)                        { this.balance = balance; }
    public void setLastTransactionAmount(double amount)           { this.lastTransactionAmount = Math.abs(amount); }
    public void setDailyTransactionCount(int count)               { this.dailyTransactionCount = count; }
    public void setFailedLoginAttempts(int attempts)              { this.failedLoginAttempts = attempts; }
    public void setLockTimestamp(LocalDateTime lockTimestamp)      { this.lockTimestamp = lockTimestamp; }
    public void setTransactionHistory(List<Transaction> history)  { this.transactionHistory = new ArrayList<>(history); }
    public void addTransaction(Transaction tx)                    { if (tx != null) transactionHistory.add(tx); }

    public boolean isAdmin()  { return role == Role.ADMIN; }
    public boolean isLocked() { return lockTimestamp != null; }

    public void clearPassword() {
        if (passwordEncrypted != null) Arrays.fill(passwordEncrypted, '\0');
    }

    @Override
    public String toString() {
        return String.format("UserAccount{username='%s', role=%s, balance=Rs.%.2f, locked=%b}",
                username, role, balance, isLocked());
    }
}
