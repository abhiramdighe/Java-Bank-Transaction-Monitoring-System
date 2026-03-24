package bank.gui;

import bank.exception.ValidationException;
import bank.service.AccountService;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class CreateAccountPanel extends JPanel {

    private final MainWindow     mainWindow;
    private final AccountService accountService;

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JTextField     fullNameField;
    private JComboBox<String> genderCombo;
    private JSpinner       ageSpinner;
    private JTextField     phoneField;
    private JTextField     emailField;

    private JLabel usernameError, passwordError, fullNameError;
    private JLabel genderError,   ageError,      phoneError,  emailError;

    private JLabel  formStatusLabel;
    private JButton createButton;
    private JButton backButton;

    public CreateAccountPanel(MainWindow mainWindow, AccountService accountService) {
        this.mainWindow     = mainWindow;
        this.accountService = accountService;
        initComponents();
        layoutComponents();
        attachListeners();
    }

    private void initComponents() {
        usernameField = new JTextField(22);
        passwordField = new JPasswordField(22);
        fullNameField = new JTextField(22);
        genderCombo   = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        ageSpinner    = new JSpinner(new SpinnerNumberModel(18, 18, 100, 1));
        phoneField    = new JTextField(22);
        emailField    = new JTextField(22);

        usernameError = errorLabel(); passwordError = errorLabel();
        fullNameError = errorLabel(); genderError   = errorLabel();
        ageError      = errorLabel(); phoneError    = errorLabel();
        emailError    = errorLabel();

        formStatusLabel = new JLabel(" ", SwingConstants.CENTER);
        formStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        createButton = makeButton("Create Account", new Color(0x1A, 0x85, 0x4E));
        backButton   = makeButton("Back to Login",   new Color(0x55, 0x55, 0x55));
    }

    private JLabel errorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setForeground(Color.RED);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        return lbl;
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        return btn;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF4, 0xF6, 0xF9));

        JLabel title = new JLabel("Create New Account", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(0x1A, 0x5F, 0x8C));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCC, 0xCC, 0xCC)),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(2, 6, 0, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        int row = 0;
        row = addRow(form, g, row, "Username:",  usernameField, usernameError);
        row = addRow(form, g, row, "Password:",  passwordField, passwordError);
        row = addRow(form, g, row, "Full Name:", fullNameField, fullNameError);
        row = addRow(form, g, row, "Gender:",    genderCombo,   genderError);
        row = addRow(form, g, row, "Age:",       ageSpinner,    ageError);
        row = addRow(form, g, row, "Phone (10 digits):", phoneField, phoneError);
        row = addRow(form, g, row, "Email:", emailField, emailError);

        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        g.insets = new Insets(10, 6, 4, 6);
        form.add(formStatusLabel, g);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        scroll.setBackground(new Color(0xF4, 0xF6, 0xF9));
        add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(0xF4, 0xF6, 0xF9));
        btnPanel.add(createButton);
        btnPanel.add(backButton);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private int addRow(JPanel form, GridBagConstraints g,
                       int row, String label, JComponent field, JLabel errLabel) {
        g.gridx = 0; g.gridy = row; g.weightx = 0; g.gridwidth = 1;
        g.insets = new Insets(6, 6, 0, 6);
        form.add(new JLabel(label), g);

        g.gridx = 1; g.weightx = 1;
        form.add(field, g);

        g.gridx = 1; g.gridy = row + 1; g.weightx = 1;
        g.insets = new Insets(0, 6, 0, 6);
        form.add(errLabel, g);
        return row + 2;
    }

    private void attachListeners() {
        createButton.addActionListener(e -> handleCreate());
        backButton.addActionListener(e -> {
            clearForm();
            mainWindow.showScreen(MainWindow.SCREEN_LOGIN);
        });
    }

    private void handleCreate() {
        clearErrors();

        String username = usernameField.getText().trim();
        char[] pwdChars = passwordField.getPassword();
        String password = new String(pwdChars);
        Arrays.fill(pwdChars, '\0');
        String fullName = fullNameField.getText().trim();
        String gender   = (String) genderCombo.getSelectedItem();
        int    age      = (Integer) ageSpinner.getValue();
        String phone    = phoneField.getText().trim();
        String email    = emailField.getText().trim();

        try {
            accountService.createAccount(username, password, fullName, gender, age, phone, email);
            formStatusLabel.setForeground(new Color(0, 128, 0));
            formStatusLabel.setText("Account created successfully! You can now log in.");
            clearForm();
        } catch (ValidationException ex) {
            showFieldError(ex.getField(), ex.getMessage());
        } catch (Exception ex) {
            formStatusLabel.setForeground(Color.RED);
            formStatusLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void showFieldError(String field, String message) {
        formStatusLabel.setText(" ");
        switch (field) {
            case "username":  usernameError.setText(message); break;
            case "password":  passwordError.setText(message); break;
            case "fullName":  fullNameError.setText(message); break;
            case "gender":    genderError.setText(message);   break;
            case "age":       ageError.setText(message);      break;
            case "phone":     phoneError.setText(message);    break;
            case "email":     emailError.setText(message);    break;
            default:
                formStatusLabel.setForeground(Color.RED);
                formStatusLabel.setText(message);
        }
        revalidate();
        repaint();
    }

    private void clearErrors() {
        usernameError.setText(" "); passwordError.setText(" ");
        fullNameError.setText(" "); genderError.setText(" ");
        ageError.setText(" ");      phoneError.setText(" ");
        emailError.setText(" ");    formStatusLabel.setText(" ");
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        fullNameField.setText("");
        genderCombo.setSelectedIndex(0);
        ageSpinner.setValue(18);
        phoneField.setText("");
        emailField.setText("");
        clearErrors();
    }
}
