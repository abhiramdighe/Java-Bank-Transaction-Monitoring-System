package bank.service;

import bank.model.Transaction;
import bank.model.TransactionStatus;
import bank.model.TransactionType;
import bank.model.UserAccount;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionMonitorService {

    public enum AlertSeverity { INFO, WARNING, CRITICAL }

    // ── Alert ──────────────────────────────────────────────────────
    public static class Alert {
        private final String          username;
        private final AlertSeverity   severity;
        private final String          ruleName;
        private final String          message;
        private final Transaction     triggeringTransaction;
        private final LocalDateTime   timestamp;

        public Alert(String username, AlertSeverity severity, String ruleName,
                     String message, Transaction tx) {
            this.username              = username;
            this.severity              = severity;
            this.ruleName              = ruleName;
            this.message               = message;
            this.triggeringTransaction = tx;
            this.timestamp             = LocalDateTime.now();
        }

        public String        getUsername()              { return username; }
        public AlertSeverity getSeverity()              { return severity; }
        public String        getRuleName()              { return ruleName; }
        public String        getMessage()               { return message; }
        public Transaction   getTriggeringTransaction() { return triggeringTransaction; }
        public LocalDateTime getTimestamp()             { return timestamp; }

        @Override
        public String toString() {
            return String.format("[%s] %s | User: %s | %s: %s",
                    timestamp, severity, username, ruleName, message);
        }
    }

    // ── MonitorRule interface (Strategy pattern) ───────────────────
    public interface MonitorRule {
        String getRuleName();
        Alert  evaluate(Transaction tx, UserAccount account);
    }

    // ── Thresholds ─────────────────────────────────────────────────
    public static final double HIGH_VALUE_DEPOSIT_THRESHOLD    = 30_000.0;
    public static final double HIGH_VALUE_WITHDRAWAL_THRESHOLD = 20_000.0;
    public static final int    HIGH_DAILY_COUNT_THRESHOLD      = 10;
    public static final double HIGH_LOAN_RATIO                 = 5.0;

    private final List<MonitorRule> rules;
    private final List<Alert>       alertLog;

    public TransactionMonitorService() {
        this.rules    = new ArrayList<>();
        this.alertLog = Collections.synchronizedList(new ArrayList<>());
        registerBuiltInRules();
    }

    // ── Public API ─────────────────────────────────────────────────

    public void evaluate(Transaction tx, UserAccount account) {
        if (tx == null || account == null) return;
        if (tx.getStatus() != TransactionStatus.APPROVED) return;
        for (MonitorRule rule : rules) {
            try {
                Alert alert = rule.evaluate(tx, account);
                if (alert != null) {
                    alertLog.add(alert);
                    handleAlert(alert);
                }
            } catch (Exception e) {
                System.err.println("Rule error [" + rule.getRuleName() + "]: " + e.getMessage());
            }
        }
    }

    public void addRule(MonitorRule rule) {
        if (rule != null) rules.add(rule);
    }

    public List<Alert> getAlertLog() {
        return Collections.unmodifiableList(alertLog);
    }

    public List<Alert> getAlertsForUser(String username) {
        List<Alert> result = new ArrayList<>();
        for (Alert a : alertLog)
            if (a.getUsername().equals(username)) result.add(a);
        return result;
    }

    // ── Rule registration ──────────────────────────────────────────

    private void registerBuiltInRules() {

        // Rule 1 – High-value deposit
        addRule(new MonitorRule() {
            @Override public String getRuleName() { return "HIGH_VALUE_DEPOSIT"; }
            @Override public Alert evaluate(Transaction tx, UserAccount account) {
                if (tx.getType() == TransactionType.DEPOSIT
                        && tx.getAmount() > HIGH_VALUE_DEPOSIT_THRESHOLD) {
                    return new Alert(account.getUsername(), AlertSeverity.WARNING,
                            getRuleName(),
                            String.format("Deposit of Rs.%.2f exceeds threshold Rs.%.2f",
                                    tx.getAmount(), HIGH_VALUE_DEPOSIT_THRESHOLD), tx);
                }
                return null;
            }
        });

        // Rule 2 – High-value withdrawal
        addRule(new MonitorRule() {
            @Override public String getRuleName() { return "HIGH_VALUE_WITHDRAWAL"; }
            @Override public Alert evaluate(Transaction tx, UserAccount account) {
                if (tx.getType() == TransactionType.WITHDRAWAL
                        && tx.getAmount() > HIGH_VALUE_WITHDRAWAL_THRESHOLD) {
                    return new Alert(account.getUsername(), AlertSeverity.CRITICAL,
                            getRuleName(),
                            String.format("Withdrawal of Rs.%.2f exceeds threshold Rs.%.2f",
                                    tx.getAmount(), HIGH_VALUE_WITHDRAWAL_THRESHOLD), tx);
                }
                return null;
            }
        });

        // Rule 3 – High daily count
        addRule(new MonitorRule() {
            @Override public String getRuleName() { return "HIGH_DAILY_COUNT"; }
            @Override public Alert evaluate(Transaction tx, UserAccount account) {
                if (account.getDailyTransactionCount() > HIGH_DAILY_COUNT_THRESHOLD) {
                    return new Alert(account.getUsername(), AlertSeverity.WARNING,
                            getRuleName(),
                            "Daily transaction count (" + account.getDailyTransactionCount()
                                    + ") exceeds limit " + HIGH_DAILY_COUNT_THRESHOLD, tx);
                }
                return null;
            }
        });

        // Rule 4 – Disproportionately large loan
        addRule(new MonitorRule() {
            @Override public String getRuleName() { return "LARGE_LOAN"; }
            @Override public Alert evaluate(Transaction tx, UserAccount account) {
                if (tx.getType() == TransactionType.LOAN) {
                    double preBalance = tx.getBalanceAfter() - tx.getAmount();
                    if (preBalance > 0 && tx.getAmount() > preBalance * HIGH_LOAN_RATIO) {
                        return new Alert(account.getUsername(), AlertSeverity.WARNING,
                                getRuleName(),
                                String.format("Loan Rs.%.2f is >%.0fx pre-loan balance Rs.%.2f",
                                        tx.getAmount(), HIGH_LOAN_RATIO, preBalance), tx);
                    }
                }
                return null;
            }
        });

        // Rule 5 – Rapid balance drain (>80% withdrawn in one tx)
        addRule(new MonitorRule() {
            @Override public String getRuleName() { return "RAPID_DRAIN"; }
            @Override public Alert evaluate(Transaction tx, UserAccount account) {
                if (tx.getType() == TransactionType.WITHDRAWAL) {
                    double preBalance = tx.getBalanceAfter() + tx.getAmount();
                    if (preBalance > 0 && tx.getAmount() / preBalance > 0.80) {
                        return new Alert(account.getUsername(), AlertSeverity.CRITICAL,
                                getRuleName(),
                                String.format("%.0f%% of balance drained in one withdrawal",
                                        (tx.getAmount() / preBalance) * 100), tx);
                    }
                }
                return null;
            }
        });
    }

    private void handleAlert(Alert alert) {
        System.err.println("MONITOR ALERT: " + alert);
    }
}
