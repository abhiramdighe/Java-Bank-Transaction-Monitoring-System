import bank.gui.MainWindow;
import bank.repository.*;
import bank.service.AccountService;
import bank.service.AuthService;
import bank.service.TransactionMonitorService;
import bank.service.TransactionService;
import bank.util.DBConnection;
import javax.swing.*;

/**
 * Entry point for the Secure Bank Transaction System.
 *
 * Phase 1: Uses flat-file repositories (users.txt + bills/ directory).
 * Phase 2: Uses MySQL database repositories if MySQL Connector/J is available
 *           and database connection succeeds. Falls back to flat-file storage.
 */
public class BankApplication {

    public static void main(String[] args) {

        // ── 1. Determine storage type ─────────────────────────────────────
        boolean useDatabase = checkDatabaseAvailability();
        UserRepository userRepo;
        TransactionRepository txRepo;

        if (useDatabase) {
            System.out.println("Using MySQL database storage...");
            userRepo = new MySQLUserRepository();
            txRepo = new MySQLTransactionRepository();
        } else {
            System.out.println("Using flat-file storage...");
            userRepo = new FlatFileUserRepository();
            txRepo = new FlatFileTransactionRepository();
        }

        // ── 2. Service layer ─────────────────────────────────────
        AccountService            accountService = new AccountService(userRepo);
        AuthService               authService    = new AuthService(userRepo);
        TransactionMonitorService monitorService = new TransactionMonitorService();
        TransactionService        txService      = new TransactionService(userRepo, txRepo);
        txService.setMonitorService(monitorService);

        // ── 3. System initialisation ─────────────────────────────
        accountService.ensureAdminExists();

        // ── 4. Launch Swing GUI on the Event Dispatch Thread ─────
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }

            MainWindow mainWindow = new MainWindow(
                    accountService, authService, txService, monitorService);
            mainWindow.setVisible(true);
        });
    }

    private static boolean checkDatabaseAvailability() {
        try {
            // Check if MySQL driver is available
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Check if database connection works
            return DBConnection.testConnection();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found. Using flat-file storage.");
            return false;
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage() + ". Using flat-file storage.");
            return false;
        }
    }
}
