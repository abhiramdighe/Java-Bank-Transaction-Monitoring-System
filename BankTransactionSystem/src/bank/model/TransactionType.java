package bank.model;

public enum TransactionType {
    DEPOSIT, WITHDRAWAL, LOAN;

    public static TransactionType fromString(String value) {
        for (TransactionType t : values())
            if (t.name().equalsIgnoreCase(value)) return t;
        throw new IllegalArgumentException("Unknown TransactionType: " + value);
    }

    @Override
    public String toString() {
        String s = name().toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
