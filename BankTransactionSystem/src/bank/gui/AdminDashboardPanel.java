package bank.gui;

import bank.model.Transaction;
import bank.model.UserAccount;
import bank.service.AccountService;
import bank.service.AuthService;
import bank.service.TransactionMonitorService;
import bank.service.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardPanel extends JPanel {

    private final MainWindow mainWindow;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final AuthService authService;
    private final TransactionMonitorService monitorService;

    private JLabel totalUsersLabel;
    private JLabel totalTransactionsLabel;
    private JLabel totalBalanceLabel;

    private JTable usersTable;
    private DefaultTableModel usersTableModel;

    private JTable transactionsTable;
    private DefaultTableModel transactionsTableModel;

    private Theme.RoundedButton refreshButton;
    private Theme.RoundedButton logoutButton;

    public AdminDashboardPanel(MainWindow mainWindow,
                                AccountService accountService,
                                TransactionService transactionService,
                                AuthService authService,
                                TransactionMonitorService monitorService) {
        this.mainWindow = mainWindow;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.authService = authService;
        this.monitorService = monitorService;
        initComponents();
        layoutComponents();
        attachListeners();
    }

    public void refresh() {
        loadStats();
        loadUsersTable();
        loadTransactionsTable();
    }

    private void initComponents() {
        Theme.stylePanel(this);
        setBackground(Theme.BG_PRIMARY);

        totalUsersLabel = makeStatLabel("Total Users", "0");
        totalTransactionsLabel = makeStatLabel("Total Transactions", "0");
        totalBalanceLabel = makeStatLabel("Total Balance", "Rs. 0.00");

        String[] userCols = {"Username", "Full Name", "Balance", "Phone", "Email", "Role", "Locked"};
        usersTableModel = new DefaultTableModel(userCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        usersTable = new JTable(usersTableModel);
        usersTable.putClientProperty("JTable.isFileList", Boolean.TRUE);
        usersTable.setFillsViewportHeight(true);
        usersTable.setRowHeight(24);
        usersTable.setBackground(Theme.BG_SECONDARY);
        usersTable.setForeground(Theme.TEXT_PRIMARY);
        usersTable.getTableHeader().setBackground(Theme.BG_CARD);
        usersTable.getTableHeader().setForeground(Theme.TEXT_PRIMARY);

        String[] txCols = {"ID", "Username", "Type", "Amount", "Status", "Time"};
        transactionsTableModel = new DefaultTableModel(txCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        transactionsTable = new JTable(transactionsTableModel);
        transactionsTable.setFillsViewportHeight(true);
        transactionsTable.setRowHeight(24);
        transactionsTable.setBackground(Theme.BG_SECONDARY);
        transactionsTable.setForeground(Theme.TEXT_PRIMARY);
        transactionsTable.getTableHeader().setBackground(Theme.BG_CARD);
        transactionsTable.getTableHeader().setForeground(Theme.TEXT_PRIMARY);

        refreshButton = new Theme.RoundedButton("Refresh All");
        logoutButton = new Theme.RoundedButton("Admin Logout");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel statPanel = new JPanel(new GridLayout(1, 3, 12, 12));
        Theme.stylePanel(statPanel);
        statPanel.add(createStatCard(totalUsersLabel));
        statPanel.add(createStatCard(totalTransactionsLabel));
        statPanel.add(createStatCard(totalBalanceLabel));
        add(statPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setTopComponent(new JScrollPane(usersTable));
        splitPane.setBottomComponent(new JScrollPane(transactionsTable));
        add(splitPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setOpaque(false);
        bottom.add(refreshButton);
        bottom.add(logoutButton);
        add(bottom, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        refreshButton.addActionListener(e -> refresh());
        logoutButton.addActionListener(e -> mainWindow.logout());
    }

    private void loadStats() {
        try {
            List<UserAccount> users = accountService.getAllAccounts();
            List<Transaction> transactions = transactionService.getAllTransactions();

            double totalBalance = users.stream().mapToDouble(UserAccount::getBalance).sum();
            totalUsersLabel.setText(String.valueOf(users.size()));
            totalTransactionsLabel.setText(String.valueOf(transactions.size()));
            totalBalanceLabel.setText(String.format("Rs. %,.2f", totalBalance));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load stats: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsersTable() {
        usersTableModel.setRowCount(0);
        try {
            List<UserAccount> users = accountService.getAllAccounts();
            for (UserAccount user : users) {
                usersTableModel.addRow(new Object[]{
                        user.getUsername(),
                        user.getFullName(),
                        String.format("%.2f", user.getBalance()),
                        user.getPhoneEncrypted(),
                        user.getEmailEncrypted(),
                        user.getRole().name(),
                        user.isLocked() ? "YES" : "NO"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load users table: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTransactionsTable() {
        transactionsTableModel.setRowCount(0);
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            for (Transaction tx : transactions) {
                transactionsTableModel.addRow(new Object[]{
                        tx.getTransactionId(),
                        tx.getUsername(),
                        tx.getType().toString(),
                        String.format("%.2f", tx.getAmount()),
                        tx.getStatus().toString(),
                        tx.getFormattedTimestamp()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load transactions table: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel makeStatLabel(String label, String value) {
        JLabel l = new JLabel("<html><center>" + label + "<br><b>" + value + "</b></center></html>", SwingConstants.CENTER);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setFont(Theme.FONT_BODY);
        return l;
    }

    private JPanel createStatCard(JLabel content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        card.add(content, BorderLayout.CENTER);
        return card;
    }
}
