package bank.ui;

import bank.model.User;
import bank.repository.AccountRepository;
import bank.service.TransactionService;
import bank.service.OTPService;
import bank.service.EmailService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import bank.model.Transaction;

public class UserDashboard extends BorderPane {

    private final User currentUser;
    private final AccountRepository accountRepo = new AccountRepository();
    private final TransactionService txService = new TransactionService();

    private final Label balanceLabel = new Label("Rs. 0.00");
    private StackPane contentArea;
    private final OTPService otpService = new OTPService();
    private final EmailService emailService = new EmailService();

    // Admin aesthetic palette
    private final String ADMIN_BG = "#0f172a";
    private final String ADMIN_SIDEBAR = "#020617";
    private final String ADMIN_CARD = "#1e293b";
    private final String ADMIN_ACCENT = "#3b82f6";
    private final String ADMIN_TEXT_PRIMARY = "#e2e8f0";
    private final String ADMIN_TEXT_DIM = "#94a3b8";
    private final String ADMIN_BORDER = "#334155";
    private final String ADMIN_SHADOW = "#00000044";

    public UserDashboard(User user) {
        this.currentUser = user;
        setStyle("-fx-background-color: " + ADMIN_BG + ";");
        setLeft(createSidebar());
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30, 40, 30, 40));
        setCenter(contentArea);
        Platform.runLater(() -> applyTableStyles());
        loadDashboardView();
        refreshBalance();
    }

    private void applyTableStyles() {
        if (getScene() != null) {
            String css = ".table-view { -fx-background-color: transparent; -fx-border-color: " + ADMIN_BORDER + "; }\n" +
                         ".table-view .column-header-background { -fx-background-color: #1e293b; }\n" +
                         ".table-view .column-header { -fx-background-color: #1e293b; -fx-border-color: " + ADMIN_BORDER + "; -fx-border-width: 0 1 1 0; }\n" +
                         ".table-view .column-header .label { -fx-text-fill: " + ADMIN_TEXT_DIM + "; -fx-alignment: center; -fx-font-family: 'Poppins', 'Segoe UI'; }\n" +
                         ".table-row-cell { -fx-background-color: #1e293b; -fx-table-cell-border-color: " + ADMIN_BORDER + "; }\n" +
                         ".table-row-cell:empty { -fx-background-color: #1e293b; }\n" +
                         ".table-view .table-cell { -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-alignment: center; -fx-font-family: 'Poppins', 'Segoe UI'; }";
            try {
                java.io.File cssFile = java.io.File.createTempFile("table_admin", ".css");
                cssFile.deleteOnExit();
                java.nio.file.Files.writeString(cssFile.toPath(), css);
                getScene().getStylesheets().add(cssFile.toURI().toString());
            } catch (Exception ignored) {}
        }
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: " + ADMIN_SIDEBAR + "; -fx-padding: 30 0 20 0; -fx-border-color: " + ADMIN_BORDER + "; -fx-border-width: 0 2 0 0;");
        sidebar.getChildren().addAll(
            createSidebarItem("⊞", "Dashboard", true, e -> loadDashboardView()),
            createSidebarItem("☷", "Transactions", false, e -> loadTransactionsView()),
            createSidebarItem("↓", "Deposit", false, e -> loadActionView("Deposit")),
            createSidebarItem("↑", "Withdraw", false, e -> loadActionView("Withdraw")),
            createSidebarItem("⇄", "Transfer", false, e -> loadActionView("Transfer")),
            createSidebarItem("🏛", "IFSC Lookup", false, e -> loadIfscLookupView()),
            createSidebarItem("💱", "Currency Rates", false, e -> loadCurrencyRatesView()),
            createSidebarItem("👤", "Profile", false, e -> loadProfileView()),
            createSpacer(),
            createSidebarItem("⏻", "Logout", false, e -> logout())
        );
        return sidebar;
    }

    @SuppressWarnings("unchecked")
    private void loadProfileView() {
        contentArea.getChildren().clear();
        VBox layout = new VBox(24);
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setPadding(new Insets(40, 60, 40, 60));
        layout.setStyle("-fx-background-color: " + ADMIN_CARD + "; -fx-background-radius: 14;");
        Label title = new Label("Profile Information");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");
        Label usernameLbl = new Label("Username:");
        usernameLbl.setStyle("-fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-size: 15px; -fx-font-family: 'Poppins', 'Segoe UI';");
        TextField username = new TextField(currentUser.getUsername());
        username.setStyle("-fx-font-size: 16px; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-background-color: #1e293b; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8; -fx-padding: 10; -fx-prompt-text-fill: #64748b;");
        username.setPromptText("Username");
        Label phoneLbl = new Label("Phone:");
        phoneLbl.setStyle("-fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-size: 15px; -fx-font-family: 'Poppins', 'Segoe UI';");
        TextField phone = new TextField(currentUser.getPhone());
        phone.setStyle("-fx-font-size: 16px; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-background-color: #1e293b; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8; -fx-padding: 10; -fx-prompt-text-fill: #64748b;");
        phone.setPromptText("Phone");
        Label email = new Label("Email: " + currentUser.getEmail());
        email.setStyle("-fx-font-size: 16px; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");
        Label role = new Label("Role: " + currentUser.getRole());
        role.setStyle("-fx-font-size: 16px; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");
        Label status = new Label("Status: " + currentUser.getStatus());
        status.setStyle("-fx-font-size: 16px; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");
        Button btnSendOtp = new Button("Send OTP");
        btnSendOtp.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 30; -fx-background-radius: 6; -fx-font-family: 'Poppins', 'Segoe UI';");
        TextField otpField = new TextField();
        otpField.setPromptText("Enter OTP");
        otpField.setVisible(false);
        Button btnSave = new Button("Save Changes");
        btnSave.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 30; -fx-background-radius: 6; -fx-font-family: 'Poppins', 'Segoe UI';");
        btnSave.setVisible(false);
        Label statusLbl = new Label();
        statusLbl.setStyle("-fx-text-fill: #A0A0A0; -fx-font-family: 'Poppins', 'Segoe UI';");
        btnSendOtp.setOnAction(e -> {
            String newUsername = username.getText().trim();
            String newPhone = phone.getText().trim();
            if (newUsername.isEmpty() || newPhone.isEmpty()) {
                statusLbl.setText("Username and phone cannot be empty.");
                statusLbl.setStyle("-fx-text-fill: #F44336;");
                return;
            }
            statusLbl.setText("Sending OTP...");
            btnSendOtp.setDisable(true);
            new Thread(() -> {
                var otpObj = otpService.createAndStoreOTP(currentUser.getEmail());
                emailService.sendEmailAsync(currentUser.getEmail(), "Profile Update OTP", "Your OTP for profile update is: " + otpObj.getOtp() + "\nThis OTP is valid for 1 minute.");
                Platform.runLater(() -> {
                    statusLbl.setText("OTP sent to your email. Enter it below.");
                    otpField.setVisible(true);
                    btnSave.setVisible(true);
                    btnSendOtp.setDisable(false);
                });
            }).start();
        });
        btnSave.setOnAction(e -> {
            String otpInput = otpField.getText().trim();
            String newUsername = username.getText().trim();
            String newPhone = phone.getText().trim();
            if (otpInput.isEmpty()) {
                statusLbl.setText("Enter the OTP sent to your email.");
                statusLbl.setStyle("-fx-text-fill: #F44336;");
                return;
            }
            statusLbl.setText("Verifying OTP...");
            btnSave.setDisable(true);
            new Thread(() -> {
                boolean otpValid = otpService.verifyOTP(currentUser.getEmail(), otpInput);
                if (otpValid) {
                    // Update user info in DB
                    boolean updated = updateUserProfile(currentUser.getId(), newUsername, newPhone);
                    Platform.runLater(() -> {
                        if (updated) {
                            statusLbl.setText("Profile updated successfully.");
                            statusLbl.setStyle("-fx-text-fill: #4CAF50;");
                            currentUser.setStatus(currentUser.getStatus());
                            currentUser.setStatus(currentUser.getStatus());
                        } else {
                            statusLbl.setText("Failed to update profile.");
                            statusLbl.setStyle("-fx-text-fill: #F44336;");
                        }
                        btnSave.setDisable(false);
                    });
                } else {
                    Platform.runLater(() -> {
                        statusLbl.setText("OTP mismatch or expired.");
                        statusLbl.setStyle("-fx-text-fill: #F44336;");
                        btnSave.setDisable(false);
                    });
                }
            }).start();
        });
        layout.getChildren().setAll(title, usernameLbl, username, email, phoneLbl, phone, role, status, btnSendOtp, otpField, btnSave, statusLbl);
        contentArea.getChildren().add(layout);
    }
    private boolean updateUserProfile(int userId, String username, String phone) {
        String query = "UPDATE Users SET username = ?, phone = ? WHERE id = ?";
        try (java.sql.Connection conn = bank.config.DatabaseConfig.getConnection()) {
            if (conn == null) return false;
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, phone);
                stmt.setInt(3, userId);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void loadIfscLookupView() {
        contentArea.getChildren().clear();
        Label label = new Label("IFSC Lookup coming soon...");
        label.setStyle("-fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-size: 22px; -fx-font-family: 'Poppins', 'Segoe UI';");
        contentArea.getChildren().add(label);
    }
    private void loadCurrencyRatesView() {
        contentArea.getChildren().clear();
        Label label = new Label("Currency Rates coming soon...");
        label.setStyle("-fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-size: 22px; -fx-font-family: 'Poppins', 'Segoe UI';");
        contentArea.getChildren().add(label);
    }
    private void loadTransactionsView() {
        contentArea.getChildren().clear();
        VBox layout = new VBox(24);
        layout.setAlignment(Pos.TOP_LEFT);
        Label title = new Label("Transaction History");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");
        TableView<Transaction> table = new TableView<>();
        table.setStyle("-fx-background-color: " + ADMIN_CARD + "; -fx-background-radius: 10; -fx-font-family: 'Poppins', 'Segoe UI';");
        TableColumn<Transaction, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTransactionId()));
        TableColumn<Transaction, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType()));
        TableColumn<Transaction, String> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f", d.getValue().getAmount())));
        TableColumn<Transaction, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        table.getColumns().addAll(colId, colType, colAmount, colStatus);
        ObservableList<Transaction> txs = FXCollections.observableArrayList();
        int userId = currentUser.getId();
        for (Transaction t : txService.getAllTransactions()) {
            try {
                int txUserId = Integer.parseInt(t.getUserId());
                if (txUserId == userId) {
                    txs.add(t);
                }
            } catch (NumberFormatException ex) {
                // Skip malformed userId
            }
        }
        if (txs.isEmpty()) {
            table.setPlaceholder(new Label("No transactions found for your account."));
        }
        table.setItems(txs);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        layout.getChildren().addAll(title, table);
        contentArea.getChildren().add(layout);
    }

    private Region createSpacer() {
        Region r = new Region();
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }

    private HBox createSidebarItem(String icon, String text, boolean isActive, javafx.event.EventHandler<javafx.event.ActionEvent> event) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(15, 20, 15, 25));
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-text-fill: " + (isActive ? ADMIN_ACCENT : ADMIN_TEXT_DIM) + "; -fx-font-size: 16px;");
        Label textLbl = new Label(text);
        textLbl.setStyle("-fx-text-fill: " + (isActive ? ADMIN_ACCENT : ADMIN_TEXT_PRIMARY) + "; -fx-font-size: 15px; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-weight: bold;");
        box.getChildren().addAll(iconLbl, textLbl);
        if (isActive) {
            box.setStyle("-fx-background-color: #1e293b; -fx-border-color: " + ADMIN_ACCENT + "; -fx-border-width: 0 0 0 3;");
        } else {
            box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: #1e293b; -fx-cursor: hand;"));
            box.setOnMouseExited(e -> box.setStyle("-fx-background-color: transparent;"));
        }
        if (event != null) {
            box.setOnMouseClicked(e -> event.handle(null));
        }
        return box;
    }

    private void loadDashboardView() {
        contentArea.getChildren().clear();
        VBox mainLayout = new VBox(30);
        mainLayout.setAlignment(Pos.TOP_LEFT);
        // Header
        VBox header = new VBox(5);
        Label welcome = new Label("Welcome, " + currentUser.getUsername());
        welcome.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");
        Label subtitle = new Label("User: " + currentUser.getUsername() + "" + currentUser.getId());
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ADMIN_TEXT_DIM + "; -fx-font-family: 'Poppins', 'Segoe UI';");
        header.getChildren().addAll(welcome, subtitle);
        // Cards
        HBox cardsContainer = new HBox(20);
        VBox balanceCard = new VBox(20);
        balanceCard.setAlignment(Pos.CENTER);
        balanceCard.setPadding(new Insets(40));
        balanceCard.setStyle("-fx-background-color: " + ADMIN_CARD + "; -fx-background-radius: 14; -fx-border-color: " + ADMIN_ACCENT + "; -fx-border-width: 0 0 0 4; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, " + ADMIN_SHADOW + ", 10, 0, 0, 5);");
        HBox.setHgrow(balanceCard, Priority.ALWAYS);
        balanceCard.setMaxWidth(Double.MAX_VALUE);
        Label balTitle = new Label("Current Balance");
        balTitle.setStyle("-fx-text-fill: " + ADMIN_TEXT_DIM + "; -fx-font-size: 15px; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-weight: bold;");
        balanceLabel.setStyle("-fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-size: 22px; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-weight: bold;");
        // Always refresh balance when loading dashboard
        balanceLabel.setText(String.format("Rs. %.2f", accountRepo.getBalance(currentUser.getId())));
        balanceCard.getChildren().addAll(balTitle, balanceLabel);

        // Replace currency card with notifications card (consistent style)
        VBox notifCard = new VBox(18);
        notifCard.setAlignment(Pos.CENTER);
        notifCard.setPadding(new Insets(40));
        notifCard.setStyle("-fx-background-color: " + ADMIN_CARD + "; -fx-background-radius: 14; -fx-border-color: #eab308; -fx-border-width: 0 0 0 4; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, " + ADMIN_SHADOW + ", 10, 0, 0, 5);");
        HBox.setHgrow(notifCard, Priority.ALWAYS);
        notifCard.setMaxWidth(Double.MAX_VALUE);
        Label notifTitle = new Label("Recent Notifications");
        notifTitle.setStyle("-fx-text-fill: " + ADMIN_TEXT_DIM + "; -fx-font-size: 15px; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-weight: bold;");
        VBox notifList = new VBox(8);
        notifList.setAlignment(Pos.TOP_LEFT);
        notifList.setStyle("-fx-background-color: transparent;");
        String notifStyle = "-fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-size: 15px; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-background-color: transparent; -fx-padding: 0 0 0 0;";
        Label n1 = new Label("Welcome to Smart Bank!");
        n1.setStyle(notifStyle);
        Label n2 = new Label("Your last login was successful.");
        n2.setStyle(notifStyle);
        Label n3 = new Label("Contact support for help.");
        n3.setStyle(notifStyle);
        notifList.getChildren().addAll(n1, n2, n3);
        notifCard.getChildren().addAll(notifTitle, notifList);
        cardsContainer.getChildren().addAll(balanceCard, notifCard);

        // Transactions Section
        VBox txSection = new VBox(15);
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        Label txTitle = new Label("Recent Transactions");
        txTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label filterLbl = new Label("Filter:");
        filterLbl.setStyle("-fx-text-fill: " + ADMIN_TEXT_DIM + "; -fx-font-size: 13px; -fx-font-family: 'Poppins', 'Segoe UI';");
        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.setPrefWidth(120);
        filterCombo.setStyle("-fx-background-color: #1e293b; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");
        // Custom cell factory for dark theme dropdown
        filterCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-background-color: #1e293b; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");
            }
        });
        filterCombo.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-background-color: #1e293b; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");
            }
        });
        filterCombo.getItems().addAll("All", "DEPOSIT", "WITHDRAW", "TRANSFER");
        filterCombo.getSelectionModel().selectFirst();
        Label dateLbl = new Label("Date:");
        dateLbl.setStyle("-fx-text-fill: " + ADMIN_TEXT_DIM + "; -fx-font-size: 13px; -fx-font-family: 'Poppins', 'Segoe UI';");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("yyyy-MM-dd");
        datePicker.setStyle("-fx-background-color: #1e293b; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + ";");
        Button btnSearch = new Button("Search");
        btnSearch.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-weight: bold; -fx-padding: 6 15;");
        filterBar.getChildren().addAll(txTitle, spacer, filterLbl, filterCombo, dateLbl, datePicker, btnSearch);

        TableView<Transaction> txTable = new TableView<>();
        txTable.setPlaceholder(new Label("No transactions found for your account."));
        TableColumn<Transaction, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTransactionId()));
        TableColumn<Transaction, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType()));
        TableColumn<Transaction, String> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f", d.getValue().getAmount())));
        TableColumn<Transaction, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        // Set column widths to fill table evenly
        colId.prefWidthProperty().bind(txTable.widthProperty().multiply(0.20));
        colType.prefWidthProperty().bind(txTable.widthProperty().multiply(0.20));
        colAmount.prefWidthProperty().bind(txTable.widthProperty().multiply(0.30));
        colStatus.prefWidthProperty().bind(txTable.widthProperty().multiply(0.30));
        txTable.getColumns().setAll(colId, colType, colAmount, colStatus);
        VBox.setVgrow(txTable, Priority.ALWAYS);

        ObservableList<Transaction> allTxs = FXCollections.observableArrayList();
        int userId = currentUser.getId();
        for (Transaction t : txService.getAllTransactions()) {
            try {
                int txUserId = Integer.parseInt(t.getUserId());
                if (txUserId == userId) {
                    allTxs.add(t);
                }
            } catch (NumberFormatException ex) {
                // Skip malformed userId
            }
        }
        txTable.setItems(FXCollections.observableArrayList(allTxs));

        // Filtering logic
        btnSearch.setOnAction(e -> {
            String selectedType = filterCombo.getValue();
            var selectedDate = datePicker.getValue();
            ObservableList<Transaction> filtered = FXCollections.observableArrayList();
            for (Transaction t : allTxs) {
                boolean matchesType = selectedType.equals("All") || t.getType().equalsIgnoreCase(selectedType);
                boolean matchesDate = true;
                // If Transaction had a date field, filter here. (Assume future extension)
                // For now, skip date filtering as Transaction model has no date.
                if (matchesType && matchesDate) {
                    filtered.add(t);
                }
            }
            txTable.setItems(filtered);
        });

        // Also filter on ComboBox change
        filterCombo.setOnAction(e -> btnSearch.fire());

        txSection.getChildren().addAll(filterBar, txTable);
        VBox.setVgrow(txSection, Priority.ALWAYS);
        mainLayout.getChildren().addAll(header, cardsContainer, txSection);
        contentArea.getChildren().add(mainLayout);
        applyTableStyles();
    }

    private void loadActionView(String type) {
        contentArea.getChildren().clear();
        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: #16213e; -fx-background-radius: 16; -fx-padding: 48 36 48 36; -fx-effect: dropshadow(gaussian, #00000044, 16, 0.2, 0, 4);");
        container.setMaxWidth(480);
        container.setMaxHeight(420);

        Label title = new Label(type + " Funds");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + ADMIN_TEXT_PRIMARY + "; -fx-font-family: 'Poppins', 'Segoe UI';");

        String inputStyle = "-fx-background-color: #232946; -fx-text-fill: #e2e8f0; -fx-prompt-text-fill: #64748b; -fx-padding: 12; -fx-font-size: 15px; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8; -fx-font-family: 'Poppins', 'Segoe UI';";

        TextField tfAmount = new TextField();
        tfAmount.setPromptText("Enter Amount");
        tfAmount.setStyle(inputStyle);

        TextField tfTarget = new TextField();
        tfTarget.setPromptText("Target Username");
        tfTarget.setStyle(inputStyle);

        TextField tfOtp = new TextField();
        tfOtp.setPromptText("Enter OTP sent to your email");
        tfOtp.setStyle(inputStyle);
        tfOtp.setVisible(false);

        Button btnSendOtp = new Button("Send OTP");
        btnSendOtp.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 8; -fx-font-family: 'Poppins', 'Segoe UI';");

        Button btnSubmit = new Button("Submit " + type);
        btnSubmit.setStyle("-fx-background-color: #334155; -fx-text-fill: #e2e8f0; -fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 8; -fx-font-family: 'Poppins', 'Segoe UI';");
        btnSubmit.setMaxWidth(Double.MAX_VALUE);
        btnSubmit.setDisable(true);

        Label statusLbl = new Label();
        statusLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-family: 'Poppins', 'Segoe UI';");

        btnSendOtp.setOnAction(e -> {
            try {
                double amt = Double.parseDouble(tfAmount.getText());
                String target = tfTarget.getText().trim();
                if (amt <= 0) {
                    statusLbl.setText("Amount must be positive.");
                    statusLbl.setStyle("-fx-text-fill: #F44336;");
                    return;
                }
                if ("Transfer".equals(type) && target.isEmpty()) {
                    statusLbl.setText("Target username required for transfer.");
                    statusLbl.setStyle("-fx-text-fill: #F44336;");
                    return;
                }
                statusLbl.setText("Sending OTP and transaction details...");
                btnSendOtp.setDisable(true);
                new Thread(new Runnable() {
                    public void run() {
                        var otpObj = otpService.createAndStoreOTP(currentUser.getEmail());
                        System.out.println("[DEBUG] OTP generated for: " + currentUser.getEmail() + " OTP: " + (otpObj != null ? otpObj.getOtp() : "null"));
                        Platform.runLater(new Runnable() {
                            public void run() {
                                if (otpObj == null) {
                                    statusLbl.setText("Failed to generate OTP. Please try again later.");
                                    statusLbl.setStyle("-fx-text-fill: #F44336;");
                                    btnSendOtp.setDisable(false);
                                    tfOtp.setVisible(false);
                                    btnSubmit.setDisable(true);
                                    return;
                                }
                                String userMsg = "You are attempting to " + type.toLowerCase() + " Rs. " + String.format("%.2f", amt);
                                if ("Transfer".equals(type)) {
                                    userMsg += " to user: " + target;
                                }
                                userMsg += ".\nYour OTP for this transaction is: " + otpObj.getOtp() + "\nThis OTP is valid for 1 minute.";
                                try {
                                    String htmlBody = bank.service.EmailTemplates.notificationTemplate(currentUser.getUsername(), "Transaction Attempt: " + type, userMsg.replace("\n", "<br>"));
                                    emailService.sendEmailAsync(currentUser.getEmail(), "Transaction Attempt: " + type, htmlBody);
                                    String adminMsg = "User <b>" + currentUser.getUsername() + "</b> (ID: " + currentUser.getId() + ", Email: " + currentUser.getEmail() + ") is attempting to <b>" + type.toLowerCase() + "</b> Rs. <span style='color:#10b981;'>" + String.format("%.2f", amt) + "</span>";
                                    if ("Transfer".equals(type)) {
                                        adminMsg += " to user: <b>" + target + "</b>";
                                    }
                                    adminMsg += ".";
                                    String htmlAdmin = bank.service.EmailTemplates.notificationTemplate("Admin", "User Transaction Attempt: " + type, adminMsg);
                                    emailService.sendEmailAsync("abhiramdighe@gmail.com", "User Transaction Attempt: " + type, htmlAdmin);
                                    statusLbl.setText("OTP and transaction details sent to your email (" + currentUser.getEmail() + "). Enter OTP below.");
                                    statusLbl.setStyle("-fx-text-fill: #A0A0A0;");
                                    tfOtp.setVisible(true);
                                    btnSubmit.setDisable(false);
                                    // Show popup with email info
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("OTP Sent");
                                    alert.setHeaderText(null);
                                    alert.setContentText("OTP sent to: " + currentUser.getEmail());
                                    alert.showAndWait();
                                } catch (Exception emailEx) {
                                    statusLbl.setText("Failed to send OTP email. Please check your email settings.");
                                    statusLbl.setStyle("-fx-text-fill: #F44336;");
                                    btnSendOtp.setDisable(false);
                                    tfOtp.setVisible(false);
                                    btnSubmit.setDisable(true);
                                }
                            }
                        });
                    }
                }).start();
            } catch (NumberFormatException ex) {
                statusLbl.setText("Invalid amount format.");
                statusLbl.setStyle("-fx-text-fill: #F44336;");
            }
        });

        btnSubmit.setOnAction(e -> {
            try {
                double amt = Double.parseDouble(tfAmount.getText());
                String target = tfTarget.getText().trim();
                String otpInput = tfOtp.getText().trim();
                if (amt <= 0) {
                    statusLbl.setText("Amount must be positive.");
                    statusLbl.setStyle("-fx-text-fill: #F44336;");
                    return;
                }
                if ("Transfer".equals(type) && target.isEmpty()) {
                    statusLbl.setText("Target username required for transfer.");
                    statusLbl.setStyle("-fx-text-fill: #F44336;");
                    return;
                }
                statusLbl.setText("Verifying OTP...");
                btnSubmit.setDisable(true);
                new Thread(() -> {
                    boolean otpValid = otpService.verifyOTP(currentUser.getEmail(), otpInput);
                    boolean success = false;
                    String errorMsg = null;
                    boolean hasAccount = false;
                    try {
                        double bal = accountRepo.getBalance(currentUser.getId());
                        hasAccount = true;
                    } catch (Exception ex) {
                        hasAccount = false;
                    }
                    if (otpValid) {
                        if (!hasAccount) {
                            accountRepo.createAccountForUser(currentUser.getId());
                        }
                        if ("Deposit".equals(type)) {
                            success = txService.deposit(currentUser, amt);
                        } else if ("Withdraw".equals(type)) {
                            success = txService.withdraw(currentUser, amt);
                        } else if ("Transfer".equals(type)) {
                            success = txService.transfer(currentUser, amt, target);
                            // Notify receiver if transfer is successful
                            if (success) {
                                var receiver = txService.getReceiverUser(target);
                                if (receiver != null) {
                                    String htmlBody = bank.service.EmailTemplates.otpTemplate(receiver.getUsername(), "Funds Received", amt, "");
                                    emailService.sendEmailAsync(receiver.getEmail(), "Funds Received", htmlBody);
                                }
                            }
                        }
                        String adminResultMsg = "User <b>" + currentUser.getUsername() + "</b> (ID: " + currentUser.getId() + ", Email: " + currentUser.getEmail() + ") completed a <b>" + type.toLowerCase() + "</b> of Rs. <span style='color:#10b981;'>" + String.format("%.2f", amt) + "</span>";
                        if ("Transfer".equals(type)) {
                            adminResultMsg += " to user: <b>" + target + "</b>";
                        }
                        adminResultMsg += ". Status: <b>" + (success ? "SUCCESS" : "FAILED") + "</b>";
                        String htmlAdmin = bank.service.EmailTemplates.notificationTemplate("Admin", "User Transaction Result: " + type, adminResultMsg);
                        emailService.sendEmailAsync("abhiramdighe@gmail.com", "User Transaction Result: " + type, htmlAdmin);
                        if (!success) {
                            errorMsg = "Transaction failed: Insufficient funds, invalid target, or DB error.";
                        }
                    } else {
                        String failMsg = "User <b>" + currentUser.getUsername() + "</b> failed OTP verification for <b>" + type + "</b>.";
                        String htmlFail = bank.service.EmailTemplates.notificationTemplate("Admin", "OTP Failure Alert", failMsg);
                        emailService.sendEmailAsync("abhiramdighe@gmail.com", "OTP Failure Alert", htmlFail);
                    }
                    boolean finalSuccess = success;
                    String finalErrorMsg = errorMsg;
                    Platform.runLater(() -> {
                        btnSubmit.setDisable(false);
                        if (otpValid && finalSuccess) {
                            statusLbl.setText("Success! Loading dashboard...");
                            statusLbl.setStyle("-fx-text-fill: #4CAF50;");
                            refreshBalance();
                            loadTransactionsView();
                            new Thread(() -> {
                                try { Thread.sleep(1000); } catch(Exception ex){}
                                Platform.runLater(() -> loadDashboardView());
                            }).start();
                        } else if (!otpValid) {
                            statusLbl.setText("Transaction failed: OTP mismatch or expired.");
                            statusLbl.setStyle("-fx-text-fill: #F44336;");
                            showPopup("Transaction Failure", "OTP mismatch or expired. Admin has been notified.");
                        } else {
                            statusLbl.setText(finalErrorMsg != null ? finalErrorMsg : "Transaction failed.");
                            statusLbl.setStyle("-fx-text-fill: #F44336;");
                        }
                    });
                }).start();
            } catch (NumberFormatException ex) {
                statusLbl.setText("Invalid amount format.");
                statusLbl.setStyle("-fx-text-fill: #F44336;");
            }
        });

        if ("Transfer".equals(type)) {
            tfTarget.setVisible(true);
            btnSendOtp.setVisible(true);
            tfOtp.setVisible(false);
            btnSubmit.setDisable(true);
            container.getChildren().addAll(title, tfAmount, tfTarget, btnSendOtp, tfOtp, btnSubmit, statusLbl);
        } else {
            tfTarget.setVisible(false);
            btnSendOtp.setVisible(true);
            tfOtp.setVisible(false);
            btnSubmit.setDisable(true);
            container.getChildren().addAll(title, tfAmount, btnSendOtp, tfOtp, btnSubmit, statusLbl);
        }
        StackPane wrapper = new StackPane(container);
        contentArea.getChildren().add(wrapper);
    }
    private void showPopup(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void logout() {
        Stage stage = (Stage) getScene().getWindow();
        stage.getScene().setRoot(new LoginScreen(stage));
    }

    private void refreshBalance() {
        new Thread(() -> {
            double bal = accountRepo.getBalance(currentUser.getId());
            Platform.runLater(() -> balanceLabel.setText(String.format("Rs. %.2f", bal)));
        }).start();
    }
}
