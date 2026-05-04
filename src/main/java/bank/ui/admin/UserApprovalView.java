package bank.ui.admin;

import bank.model.User;
import bank.repository.AccountRepository;
import bank.repository.UserRepository;
import bank.service.EmailService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.Optional;

public class UserApprovalView extends VBox {
    private final UserRepository userRepository = new UserRepository();
    private final EmailService emailService = new EmailService();
    private final TableView<User> table = new TableView<>();
    private final ObservableList<User> masterData = FXCollections.observableArrayList();
    private final TextField searchField = new TextField();
    private final ComboBox<String> filterCombo = new ComboBox<>();
    private final StackPane rootPane;

    public UserApprovalView(StackPane rootPane) {
        this.rootPane = rootPane;
        setSpacing(20);
        setPadding(new Insets(20));

        Label title = new Label("User Approvals & Management");
        title.setStyle("-fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        // Top Toolbar
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);

        searchField.setPromptText("Search users...");
        searchField.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #e2e8f0; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
        searchField.setPrefWidth(250);

        filterCombo.getItems().addAll("ALL", "PENDING", "ACTIVE", "REJECTED");
        filterCombo.setValue("ALL");
        filterCombo.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #e2e8f0; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6;");

        Button btnRefresh = createStyledButton("🔄 Refresh", "#3b82f6");
        btnRefresh.setOnAction(e -> loadData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnApprove = createStyledButton("✔ Approve", "#10b981");
        btnApprove.setOnAction(e -> confirmAction("Approve"));

        Button btnReject = createStyledButton("✖ Reject", "#ef4444");
        btnReject.setOnAction(e -> confirmAction("Reject"));

        topBar.getChildren().addAll(searchField, filterCombo, btnRefresh, spacer, btnApprove, btnReject);

        setupTable();

        FilteredList<User> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldV, newV) -> {
            applyFilters(filteredData);
        });

        filterCombo.valueProperty().addListener((obs, oldV, newV) -> {
            applyFilters(filteredData);
            loadData(); // Re-fetch from DB based on filter if needed, but since we're using in-memory list we can just re-filter
        });

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        VBox.setVgrow(table, Priority.ALWAYS);
        getChildren().addAll(title, topBar, table);

        loadData();
    }

    private void applyFilters(FilteredList<User> filteredData) {
        filteredData.setPredicate(user -> {
            String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
            String f = filterCombo.getValue();
            boolean matchFilter = f.equals("ALL") || user.getStatus().equals(f);
            
            if (q.isEmpty()) return matchFilter;
            boolean matchSearch = user.getUsername().toLowerCase().contains(q) 
                               || user.getEmail().toLowerCase().contains(q);
            return matchFilter && matchSearch;
        });
    }

    private Button createStyledButton(String text, String baseColor) {
        Button btn = new Button(text);
        String normalStyle = "-fx-background-color: " + baseColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;";
        String hoverStyle = "-fx-background-color: derive(" + baseColor + ", 15%); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        return btn;
    }

    private void setupTable() {
        table.setStyle("-fx-background-color: #1e293b; -fx-control-inner-background: #1e293b; -fx-table-cell-border-color: transparent; -fx-font-family: 'Segoe UI'; -fx-fixed-cell-size: 45px;");
        
        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        idCol.setPrefWidth(60);

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        usernameCol.setPrefWidth(150);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        emailCol.setPrefWidth(220);

        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));
        phoneCol.setPrefWidth(120);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole()));
        roleCol.setPrefWidth(100);

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        statusCol.setPrefWidth(130);
        
        // Status Badge Logic
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setStyle("-fx-padding: 4 12; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 11px;");
                    if (status.equals("ACTIVE")) badge.setStyle(badge.getStyle() + "-fx-background-color: #166534; -fx-text-fill: #4ade80;");
                    else if (status.equals("PENDING")) badge.setStyle(badge.getStyle() + "-fx-background-color: #854d0e; -fx-text-fill: #facc15;");
                    else if (status.equals("REJECTED")) badge.setStyle(badge.getStyle() + "-fx-background-color: #7f1d1d; -fx-text-fill: #f87171;");
                    
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(idCol, usernameCol, emailCol, phoneCol, roleCol, statusCol);

        Platform.runLater(() -> applyTableHacks());
    }

    private void applyTableHacks() {
        if (getScene() == null) return;
        String css = ".table-view .column-header-background { -fx-background-color: #0f172a; } " +
                     ".table-view .column-header { -fx-background-color: #0f172a; -fx-border-color: #334155; -fx-border-width: 0 0 1 0; } " +
                     ".table-view .column-header .label { -fx-text-fill: #94a3b8; -fx-font-weight: bold; } " +
                     ".table-row-cell { -fx-background-color: #1e293b; -fx-border-color: transparent transparent #334155 transparent; -fx-border-width: 1; } " +
                     ".table-row-cell:empty { -fx-background-color: #1e293b; -fx-border-color: transparent; } " +
                     ".table-row-cell:hover { -fx-background-color: #334155; } " +
                     ".table-row-cell:selected { -fx-background-color: #3b82f6; } " +
                     ".table-view .table-cell { -fx-text-fill: #e2e8f0; -fx-alignment: center-left; -fx-padding: 0 10; }";
        try {
            java.io.File cssFile = java.io.File.createTempFile("admin_tx", ".css");
            cssFile.deleteOnExit();
            java.nio.file.Files.writeString(cssFile.toPath(), css);
            getScene().getStylesheets().add(cssFile.toURI().toString());
        } catch (Exception ignored) {}
    }

    private void loadData() {
        new Thread(() -> {
            String selection = filterCombo.getValue();
            var users = userRepository.getUsers(selection); // Backend handles filtering by status
            Platform.runLater(() -> {
                masterData.setAll(users);
            });
        }).start();
    }

    private void confirmAction(String action) {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Toast.show(rootPane, "Please select a user first.", true);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm " + action);
        alert.setHeaderText("Do you really want to " + action.toLowerCase() + " user: " + selected.getUsername() + "?");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #e2e8f0;");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            processAction(action.equals("Approve"), selected);
        }
    }

    private void processAction(boolean approve, User selected) {
        AccountRepository accountRepo = new AccountRepository();
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
                    Toast.show(rootPane, "User status updated to " + newStatus, false);
                    loadData();
                });
            } else {
                Platform.runLater(() -> {
                    Toast.show(rootPane, "Database Error while updating user.", true);
                });
            }
        }).start();
    }
}
