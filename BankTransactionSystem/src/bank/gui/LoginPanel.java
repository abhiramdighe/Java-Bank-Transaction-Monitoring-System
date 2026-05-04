package bank.gui;

import bank.exception.AccountLockedException;
import bank.exception.AccountNotFoundException;
import bank.exception.AuthenticationException;
import bank.model.UserAccount;
import bank.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginPanel extends JPanel {

    private final MainWindow mainWindow;
    private final AuthService authService;

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private Theme.RoundedButton adminLoginButton;
    private Theme.RoundedButton userLoginButton;
    private Theme.RoundedButton createAccountButton;
    private Theme.RoundedButton exitButton;

    public LoginPanel(MainWindow mainWindow, AuthService authService) {
        this.mainWindow = mainWindow;
        this.authService = authService;
        initComponents();
        layoutComponents();
        attachListeners();
    }

    private void initComponents() {
        Theme.stylePanel(this);

        titleLabel = new JLabel("SECURE BANK TRANSACTION SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        subtitleLabel = new JLabel("Please select an option to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(Theme.FONT_BODY);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);

        usernameField = new JTextField(22);
        usernameField.setFont(Theme.FONT_BODY);

        passwordField = new JPasswordField(22);
        passwordField.setFont(Theme.FONT_BODY);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_BODY);
        statusLabel.setForeground(Color.RED);

        adminLoginButton = new Theme.RoundedButton("Admin Login");
        userLoginButton = new Theme.RoundedButton("User Login");
        createAccountButton = new Theme.RoundedButton("Create New Account");
        exitButton = new Theme.RoundedButton("Exit");

        Theme.styleButton(adminLoginButton);
        Theme.styleButton(userLoginButton);
        Theme.styleButton(createAccountButton);
        Theme.styleButton(exitButton);
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_PRIMARY);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        g.insets = new Insets(30, 10, 4, 10);
        add(titleLabel, g);

        g.gridy = 1; g.insets = new Insets(0, 10, 20, 10);
        add(subtitleLabel, g);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.BG_SECONDARY);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        card.add(new JLabel("Username:"), gc);
        gc.gridx = 1; gc.weightx = 1;
        card.add(usernameField, gc);

        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        card.add(new JLabel("Password:"), gc);
        gc.gridx = 1; gc.weightx = 1;
        card.add(passwordField, gc);

        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2;
        gc.insets = new Insets(4, 6, 0, 6);
        card.add(statusLabel, gc);

        g.gridy = 2; g.insets = new Insets(0, 60, 10, 60);
        add(card, g);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(Theme.BG_PRIMARY);
        btnPanel.add(adminLoginButton);
        btnPanel.add(userLoginButton);
        btnPanel.add(createAccountButton);
        btnPanel.add(exitButton);

        g.gridy = 3; g.insets = new Insets(6, 10, 20, 10);
        add(btnPanel, g);
    }

    private void attachListeners() {
        adminLoginButton.addActionListener(e -> handleAdminLogin());
        userLoginButton.addActionListener(e -> handleUserLogin());
        passwordField.addActionListener(e -> handleUserLogin());

        createAccountButton.addActionListener(e -> {
            clearFields();
            mainWindow.showScreen(MainWindow.SCREEN_CREATE_ACCOUNT);
        });

        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?", "Exit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) System.exit(0);
        });
    }

    private void handleAdminLogin() {
        String username = usernameField.getText().trim();
        char[] pwdChars = passwordField.getPassword();
        String password = new String(pwdChars);
        wipe(pwdChars);

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Username and password are required.", true); return;
        }
        try {
            authService.authenticateAdmin(username, password);
            clearFields();
            mainWindow.showAdminDashboard();
        } catch (AccountNotFoundException ex) {
            showStatus("Admin account not found.", true);
        } catch (AccountLockedException ex) {
            showStatus(ex.getMessage(), true);
        } catch (AuthenticationException ex) {
            showStatus("Incorrect password. " + ex.getAttemptsRemaining() + " attempt(s) left.", true);
        } catch (SecurityException ex) {
            showStatus("This account does not have admin access.", true);
        }
    }

    private void handleUserLogin() {
        String username = usernameField.getText().trim();
        char[] pwdChars = passwordField.getPassword();
        String password = new String(pwdChars);
        wipe(pwdChars);

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Username and password are required.", true); return;
        }
        try {
            UserAccount user = authService.authenticate(username, password);
            clearFields();
            mainWindow.showUserDashboard(user.getUsername());
        } catch (AccountNotFoundException ex) {
            showStatus("User not found.", true);
        } catch (AccountLockedException ex) {
            showStatus(ex.getMessage(), true);
        } catch (AuthenticationException ex) {
            showStatus("Incorrect password. " + ex.getAttemptsRemaining() + " attempt(s) left.", true);
        }
    }

    private void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 128, 0));
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }

    private void wipe(char[] arr) {
        Arrays.fill(arr, '\0');
    }
}
