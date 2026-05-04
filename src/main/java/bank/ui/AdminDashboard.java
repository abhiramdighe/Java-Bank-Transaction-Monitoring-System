package bank.ui;

import bank.model.User;
import bank.repository.UserRepository;
import bank.repository.AccountRepository;
import bank.service.EmailService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminDashboard extends BorderPane {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private TableView<User> table;

    public AdminDashboard() {
        this.userRepository = new UserRepository();
        this.emailService = new EmailService();

        setStyle("-fx-background-color: #1E1E2F;");
        setPadding(new Insets(20));

        Label title = new Label("Admin Dashboard - Pending Approvals");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        table = new TableView<>();
        table.setStyle("-fx-background-color: #2A2A40; -fx-text-fill: white; -fx-control-inner-background: #2A2A40; -fx-table-cell-border-color: transparent;");
        
        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        table.getColumns().addAll(idCol, usernameCol, emailCol, phoneCol, roleCol, statusCol);

        Button btnApprove = new Button("Approve");
        styleButton(btnApprove, "#4CAF50");
        btnApprove.setOnAction(e -> handleApproval(true));

        Button btnReject = new Button("Reject");
        styleButton(btnReject, "#F44336");
        btnReject.setOnAction(e -> handleApproval(false));

        Button btnRefresh = new Button("Refresh");
        styleButton(btnRefresh, "#2196F3");
        btnRefresh.setOnAction(e -> loadData());

        HBox actions = new HBox(15, btnApprove, btnReject, btnRefresh);
        actions.setPadding(new Insets(20, 0, 0, 0));

        VBox centerBox = new VBox(20, title, table, actions);
        setCenter(centerBox);

        loadData();
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
    }

    private void loadData() {
        new Thread(() -> {
            var pending = userRepository.getPendingUsers();
            Platform.runLater(() -> {
                ObservableList<User> data = FXCollections.observableArrayList(pending);
                table.setItems(data);
            });
        }).start();
    }

    private void handleApproval(boolean approve) {
        User selected = table.getSelectionModel().getSelectedItem();
        AccountRepository accountRepo = new AccountRepository();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a user to approve or reject.");
            return;
        }

        String newStatus = approve ? "ACTIVE" : "REJECTED";
        
        new Thread(() -> {
            boolean success = userRepository.updateUserStatus(selected.getId(), newStatus);
            if (success && approve) accountRepo.createAccountForUser(selected.getId());
            if (success) {
                String subject = "Bank Account Update";
                String body = approve ? 
                    "Congratulations! Your banking account is now ACTIVE. You can log in securely." : 
                    "Sorry, your account application was REJECTED based on administrative review.";
                
                emailService.sendEmailAsync(selected.getEmail(), subject, body);
                
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User status updated to " + newStatus);
                    loadData();
                });
            } else {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update user. Ensure database is running.");
                });
            }
        }).start();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
