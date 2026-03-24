package bank.gui;

import bank.exception.InsufficientBalanceException;
import bank.exception.TransactionLimitException;
import bank.model.Transaction;
import bank.model.UserAccount;
import bank.service.AccountService;
import bank.service.CurrencyService;
import bank.service.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserDashboardPanel extends JPanel {

    private final MainWindow mainWindow;
    private final AccountService accountService;
    private final TransactionService transactionService;

    private String currentUsername;

    private JLabel headerLabel;
    private JLabel subHeaderLabel;
    private JLabel balanceLabel;
    private JLabel currencyLabel;
    private Theme.RoundedButton refreshCurrencyButton;

    private JTable activityTable;
    private DefaultTableModel activityTableModel;

    public UserDashboardPanel(MainWindow mainWindow,
                               AccountService accountService,
                               TransactionService transactionService) {
        this.mainWindow = mainWindow;
        this.accountService = accountService;
        this.transactionService = transactionService;
        initComponents();
        layoutComponents();
        attachListeners();
    }

    public void loadUser(String username) {
        this.currentUsername = username;
        try {
            UserAccount user = accountService.getAccount(username);
            headerLabel.setText("Welcome, " + user.getFullName());
            subHeaderLabel.setText("User: " + user.getUsername());
            updateBalance(user.getBalance());
            refreshActivityTable();
            refreshCurrencyRate();
            JOptionPane.showMessageDialog(this, "Logged in as " + user.getFullName(),
                    "Welcome", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load user data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void clearSession() {
        currentUsername = null;
        headerLabel.setText("Welcome");
        subHeaderLabel.setText("Please login to see details");
        balanceLabel.setText("Balance: Rs. 0.00");
        currencyLabel.setText("USD → INR: --");
        activityTableModel.setRowCount(0);
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void showTransactions() {
        if (currentUsername != null) {
            refreshActivityTable();
            JOptionPane.showMessageDialog(this, "Transaction list refreshed.", "Transactions", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No user loaded.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openDepositDialog() {
        if (currentUsername == null) {
            JOptionPane.showMessageDialog(this, "No user loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = JOptionPane.showInputDialog(this, "Enter deposit amount (Rs):", "Deposit", JOptionPane.PLAIN_MESSAGE);
        if (value == null) return;
        try {
            double amount = Double.parseDouble(value.trim());
            if (amount <= 0) throw new NumberFormatException();
            UserAccount user = accountService.getAccount(currentUsername);
            transactionService.processDeposit(user, amount);
            loadUser(currentUsername);
            JOptionPane.showMessageDialog(this, "Deposit successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (TransactionLimitException ex) {
            JOptionPane.showMessageDialog(this, "Deposit limit exceeded: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Deposit failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openWithdrawDialog() {
        if (currentUsername == null) {
            JOptionPane.showMessageDialog(this, "No user loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String value = JOptionPane.showInputDialog(this, "Enter withdrawal amount (Rs):", "Withdraw", JOptionPane.PLAIN_MESSAGE);
        if (value == null) return;
        try {
            double amount = Double.parseDouble(value.trim());
            if (amount <= 0) throw new NumberFormatException();
            UserAccount user = accountService.getAccount(currentUsername);
            transactionService.processWithdrawal(user, amount);
            loadUser(currentUsername);
            JOptionPane.showMessageDialog(this, "Withdrawal successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InsufficientBalanceException ex) {
            JOptionPane.showMessageDialog(this, "Insufficient balance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Withdrawal failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshCurrencyRate() {
        double rate = CurrencyService.getUSDtoINR();
        if (rate <= 0) {
            currencyLabel.setText("USD → INR: unavailable");
            JOptionPane.showMessageDialog(this, "Failed to fetch currency rate. Try again.", "Currency API", JOptionPane.WARNING_MESSAGE);
        } else {
            currencyLabel.setText(String.format("USD → INR: %.4f", rate));
            JOptionPane.showMessageDialog(this, "Exchange rate updated.", "Currency API", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initComponents() {
        Theme.stylePanel(this);

        headerLabel = new JLabel("Welcome", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Theme.TEXT_PRIMARY);

        subHeaderLabel = new JLabel("Please login to see details", SwingConstants.LEFT);
        subHeaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subHeaderLabel.setForeground(Theme.TEXT_SECONDARY);

        balanceLabel = new JLabel("Balance: Rs. 0.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        balanceLabel.setForeground(Theme.ACCENT);

        currencyLabel = new JLabel("USD → INR: --", SwingConstants.CENTER);
        currencyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        currencyLabel.setForeground(Theme.TEXT_PRIMARY);

        refreshCurrencyButton = new Theme.RoundedButton("Refresh");
        refreshCurrencyButton.setPreferredSize(new Dimension(140, 36));

        String[] cols = {"ID", "Type", "Amount", "Status", "Date"};
        activityTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        activityTable = new JTable(activityTableModel);
        activityTable.setFillsViewportHeight(true);
        activityTable.setRowHeight(24);
        activityTable.setBackground(Theme.BG_SECONDARY);
        activityTable.setForeground(Theme.TEXT_PRIMARY);
        activityTable.getTableHeader().setBackground(Theme.BG_CARD);
        activityTable.getTableHeader().setForeground(Theme.TEXT_PRIMARY);
        activityTable.getTableHeader().setFont(Theme.FONT_BODY);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel northPanel = new JPanel(new BorderLayout(6, 6));
        northPanel.setOpaque(false);

        JPanel headerInfo = new JPanel(new GridLayout(2, 1));
        headerInfo.setOpaque(false);
        headerInfo.add(headerLabel);
        headerInfo.add(subHeaderLabel);

        northPanel.add(headerInfo, BorderLayout.WEST);
        add(northPanel, BorderLayout.NORTH);

        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 14, 0));
        centerGrid.setOpaque(false);

        centerGrid.add(makeCard("Current Balance", balanceLabel));
        centerGrid.add(makeCurrencyCard());

        add(centerGrid, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);

        JLabel txLabel = new JLabel("Recent Transactions", SwingConstants.LEFT);
        txLabel.setFont(Theme.FONT_BODY);
        txLabel.setForeground(Theme.TEXT_PRIMARY);
        bottom.add(txLabel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(activityTable);
        scroll.getViewport().setBackground(Theme.BG_SECONDARY);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        bottom.add(scroll, BorderLayout.CENTER);

        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel makeCard(String title, JComponent content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        card.add(lbl, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel makeCurrencyCard() {
        JPanel card = makeCard("Currency Info", currencyLabel);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.add(refreshCurrencyButton);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private void attachListeners() {
        refreshCurrencyButton.addActionListener(e -> refreshCurrencyRate());
    }

    private void refreshActivityTable() {
        activityTableModel.setRowCount(0);
        if (currentUsername == null) return;
        try {
            List<Transaction> list = transactionService.getTransactionsForUser(currentUsername);
            for (Transaction tx : list) {
                activityTableModel.addRow(new Object[]{
                        tx.getTransactionId(),
                        tx.getType().toString(),
                        String.format("%.2f", tx.getAmount()),
                        tx.getStatus().toString(),
                        tx.getFormattedTimestamp()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load transactions: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBalance(double balance) {
        balanceLabel.setText(String.format("Rs. %,.2f", balance));
    }
}
