package bank.gui;

import bank.service.AccountService;
import bank.service.AuthService;
import bank.service.TransactionMonitorService;
import bank.service.TransactionService;
import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public static final String SCREEN_WELCOME       = "WELCOME";
    public static final String SCREEN_LOGIN         = "LOGIN";
    public static final String SCREEN_ADMIN         = "ADMIN_DASHBOARD";
    public static final String SCREEN_USER          = "USER_DASHBOARD";
    public static final String SCREEN_CREATE_ACCOUNT= "CREATE_ACCOUNT";

    private final CardLayout   cardLayout;
    private final JPanel       cardPanel;
    private final JPanel       sidebarPanel;

    private final AccountService            accountService;
    private final AuthService               authService;
    private final TransactionService        transactionService;
    private final TransactionMonitorService monitorService;

    private WelcomePanel      welcomePanel;
    private LoginPanel        loginPanel;
    private AdminDashboardPanel adminDashboardPanel;
    private UserDashboardPanel  userDashboardPanel;
    private CreateAccountPanel  createAccountPanel;

    private Theme.RoundedButton dashboardBtn;
    private Theme.RoundedButton transactionsBtn;
    private Theme.RoundedButton depositBtn;
    private Theme.RoundedButton withdrawBtn;
    private Theme.RoundedButton ratesBtn;
    private Theme.RoundedButton profileBtn;
    private Theme.RoundedButton logoutBtn;

    public MainWindow(AccountService accountService,
                      AuthService authService,
                      TransactionService transactionService,
                      TransactionMonitorService monitorService) {

        this.accountService     = accountService;
        this.authService        = authService;
        this.transactionService = transactionService;
        this.monitorService     = monitorService;

        this.cardLayout = new CardLayout();
        this.cardPanel  = new JPanel(cardLayout);
        this.sidebarPanel = new JPanel(new GridLayout(8, 1, 5, 5));

        initFrame();
        initPanels();
        initSidebar();
        showScreen(SCREEN_WELCOME);
    }

    private void initFrame() {
        setTitle("Secure Bank Transaction System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Theme.stylePanel(sidebarPanel);
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        add(sidebarPanel, BorderLayout.WEST);

        Theme.stylePanel(cardPanel);
        add(cardPanel, BorderLayout.CENTER);
    }

    private void initPanels() {
        welcomePanel         = new WelcomePanel(this);
        loginPanel           = new LoginPanel(this, authService);
        createAccountPanel   = new CreateAccountPanel(this, accountService);
        userDashboardPanel   = new UserDashboardPanel(this, accountService, transactionService);
        adminDashboardPanel  = new AdminDashboardPanel(this, accountService,
                                                       transactionService, authService, monitorService);

        cardPanel.add(welcomePanel,          SCREEN_WELCOME);
        cardPanel.add(loginPanel,            SCREEN_LOGIN);
        cardPanel.add(createAccountPanel,    SCREEN_CREATE_ACCOUNT);
        cardPanel.add(userDashboardPanel,    SCREEN_USER);
        cardPanel.add(adminDashboardPanel,   SCREEN_ADMIN);
    }

    private void initSidebar() {
        sidebarPanel.removeAll();

        dashboardBtn = new Theme.RoundedButton("Dashboard");
        transactionsBtn = new Theme.RoundedButton("Transactions");
        depositBtn = new Theme.RoundedButton("Deposit");
        withdrawBtn = new Theme.RoundedButton("Withdraw");
        ratesBtn = new Theme.RoundedButton("Currency Rates");
        profileBtn = new Theme.RoundedButton("Profile");
        logoutBtn = new Theme.RoundedButton("Logout");

        Theme.styleButton(dashboardBtn);
        Theme.styleButton(transactionsBtn);
        Theme.styleButton(depositBtn);
        Theme.styleButton(withdrawBtn);
        Theme.styleButton(ratesBtn);
        Theme.styleButton(profileBtn);
        Theme.styleButton(logoutBtn);

        dashboardBtn.addActionListener(e -> {
            setSidebarSelection(dashboardBtn);
            showUserDashboard(userDashboardPanel.getCurrentUsername());
        });
        transactionsBtn.addActionListener(e -> {
            setSidebarSelection(transactionsBtn);
            userDashboardPanel.showTransactions();
        });
        depositBtn.addActionListener(e -> {
            setSidebarSelection(depositBtn);
            userDashboardPanel.openDepositDialog();
        });
        withdrawBtn.addActionListener(e -> {
            setSidebarSelection(withdrawBtn);
            userDashboardPanel.openWithdrawDialog();
        });
        ratesBtn.addActionListener(e -> {
            setSidebarSelection(ratesBtn);
            userDashboardPanel.refreshCurrencyRate();
        });
        profileBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Profile area is under development. Use dashboard features.", "Profile", JOptionPane.INFORMATION_MESSAGE));
        logoutBtn.addActionListener(e -> logout());

        sidebarPanel.add(Box.createVerticalStrut(15));
        sidebarPanel.add(dashboardBtn);
        sidebarPanel.add(transactionsBtn);
        sidebarPanel.add(depositBtn);
        sidebarPanel.add(withdrawBtn);
        sidebarPanel.add(ratesBtn);
        sidebarPanel.add(profileBtn);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(logoutBtn);

        sidebarPanel.setVisible(false);
    }

    private void setSidebarSelection(Theme.RoundedButton selected) {
        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof Theme.RoundedButton) {
                Theme.RoundedButton btn = (Theme.RoundedButton) comp;
                btn.setBackground(btn == selected ? Theme.ACCENT.brighter() : Theme.ACCENT);
            }
        }
    }

    public void showScreen(String screenName) {
        cardLayout.show(cardPanel, screenName);
        boolean showSidebar = SCREEN_USER.equals(screenName) || SCREEN_ADMIN.equals(screenName);
        sidebarPanel.setVisible(showSidebar);
        if (!showSidebar) {
            setSidebarSelection(null);
        }
    }

    public void showUserDashboard(String username) {
        userDashboardPanel.loadUser(username);
        showScreen(SCREEN_USER);
        setSidebarSelection(dashboardBtn);
    }

    public void showAdminDashboard() {
        adminDashboardPanel.refresh();
        showScreen(SCREEN_ADMIN);
        setSidebarSelection(null);
    }

    public void logout() {
        userDashboardPanel.clearSession();
        showScreen(SCREEN_LOGIN);
    }

    public AccountService            getAccountService()     { return accountService; }
    public AuthService               getAuthService()        { return authService; }
    public TransactionService        getTransactionService() { return transactionService; }
    public TransactionMonitorService getMonitorService()     { return monitorService; }
}
