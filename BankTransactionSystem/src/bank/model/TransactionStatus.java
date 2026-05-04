package bank.model;

public enum TransactionStatus {
    APPROVED, REJECTED;

    public static TransactionStatus fromString(String value) {
        for (TransactionStatus s : values())
            if (s.name().equalsIgnoreCase(value)) return s;
        throw new IllegalArgumentException("Unknown TransactionStatus: " + value);
    }

    @Override
    public String toString() {
        String s = name().toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
