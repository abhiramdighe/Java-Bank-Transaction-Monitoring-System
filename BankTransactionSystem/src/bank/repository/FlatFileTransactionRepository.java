package bank.repository;

import bank.model.Transaction;
import bank.model.TransactionStatus;
import bank.model.TransactionType;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Stores transactions in per-user flat files under bills/<username>_activity.txt
 * Format (pipe-delimited, one transaction per line):
 *   transactionId|username|type|amount|status|balanceAfter|timestamp
 *
 * Timestamp stored as ISO_LOCAL_DATE_TIME ("yyyy-MM-dd'T'HH:mm:ss").
 */
public class FlatFileTransactionRepository implements TransactionRepository {

    private static final String BILLS_DIR = "bills";
    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ---------------------------------------------------------------
    // Write
    // ---------------------------------------------------------------

    @Override
    public boolean save(Transaction tx) {
        File dir = new File(BILLS_DIR);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(BILLS_DIR + File.separator + tx.getUsername() + "_activity.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.println(serialize(tx));
            return true;
        } catch (IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    @Override
    public List<Transaction> findByUsername(String username) {
        List<Transaction> result = new ArrayList<>();
        File file = new File(BILLS_DIR + File.separator + username + "_activity.txt");
        if (!file.exists()) return result;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Transaction tx = parse(line);
                if (tx != null) result.add(tx);
            }
        } catch (IOException e) {
            System.err.println("Error reading activity for " + username + ": " + e.getMessage());
        }
        // Return newest first
        result.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
        return result;
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> all = new ArrayList<>();
        File dir = new File(BILLS_DIR);
        if (!dir.exists()) return all;

        File[] files = dir.listFiles((d, name) -> name.endsWith("_activity.txt"));
        if (files == null) return all;

        for (File f : files) {
            String name = f.getName();
            String username = name.substring(0, name.length() - "_activity.txt".length());
            all.addAll(findByUsername(username));
        }
        all.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
        return all;
    }

    @Override
    public List<Transaction> findByUsernameAndDateRange(String username,
                                                         LocalDateTime from,
                                                         LocalDateTime to) {
        return findByUsername(username).stream()
                .filter(tx -> !tx.getTimestamp().isBefore(from) && !tx.getTimestamp().isAfter(to))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        return findAll().stream()
                .filter(tx -> tx.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public int countTodayTransactions(String username) {
        LocalDate today = LocalDate.now();
        return (int) findByUsername(username).stream()
                .filter(tx -> tx.getTimestamp().toLocalDate().equals(today))
                .count();
    }

    // ---------------------------------------------------------------
    // Serialisation helpers
    // ---------------------------------------------------------------

    private String serialize(Transaction tx) {
        return String.join("|",
                tx.getTransactionId(),
                tx.getUsername(),
                tx.getType().name(),
                String.format("%.2f", tx.getAmount()),
                tx.getStatus().name(),
                String.format("%.2f", tx.getBalanceAfter()),
                tx.getTimestamp().format(TS_FMT)
        );
    }

    private Transaction parse(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 7) return null;
        try {
            return new Transaction(
                    p[0],
                    p[1],
                    TransactionType.fromString(p[2]),
                    Double.parseDouble(p[3].trim()),
                    TransactionStatus.fromString(p[4]),
                    Double.parseDouble(p[5].trim()),
                    LocalDateTime.parse(p[6].trim(), TS_FMT)
            );
        } catch (Exception e) {
            System.err.println("Error parsing transaction record: " + line);
            return null;
        }
    }
}
