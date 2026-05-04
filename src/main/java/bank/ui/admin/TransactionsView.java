package bank.ui.admin;

import bank.model.Transaction;
import bank.service.TransactionService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class TransactionsView extends VBox {
    private final TransactionService transactionService = new TransactionService();
    private final TableView<Transaction> table = new TableView<>();
    private final ObservableList<Transaction> masterData = FXCollections.observableArrayList();

    public TransactionsView() {
        setSpacing(20);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: transparent;");

        // Consistent dark theme CSS for TableView with card effect
        String tableCss = ".table-view { -fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, #00000044, 10, 0, 0, 5); }\n" +
            ".table-view .column-header-background { -fx-background-color: #1e293b; }\n" +
            ".table-view .column-header { -fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 1 1 0; }\n" +
            ".table-view .column-header .label { -fx-text-fill: #94a3b8; -fx-alignment: center; -fx-font-family: 'Poppins', 'Segoe UI'; }\n" +
            ".table-row-cell { -fx-background-color: #1e293b; -fx-table-cell-border-color: #334155; }\n" +
            ".table-row-cell:empty { -fx-background-color: #1e293b; }\n" +
            ".table-view .table-cell { -fx-text-fill: #e2e8f0; -fx-alignment: center; -fx-font-family: 'Poppins', 'Segoe UI'; }";
        try {
            java.io.File cssFile = java.io.File.createTempFile("admin_table", ".css");
            cssFile.deleteOnExit();
            java.nio.file.Files.writeString(cssFile.toPath(), tableCss);
            table.getStylesheets().add(cssFile.toURI().toString());
        } catch (Exception ignored) {}

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label title = new Label("Transactions");
        title.setStyle("-fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        searchField.setPromptText("Search transactions...");
        searchField.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #e2e8f0; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-font-family: 'Poppins', 'Segoe UI';");
        searchField.setPrefWidth(250);
        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-font-family: 'Poppins', 'Segoe UI';");
        btnRefresh.setOnAction(e -> loadData());
        topBar.getChildren().addAll(searchField, btnRefresh);

        setupTable();
        table.setItems(masterData);
        VBox.setVgrow(table, Priority.ALWAYS);
        getChildren().addAll(title, topBar, table);
        loadData();

        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String q = newV == null ? "" : newV.toLowerCase();
            table.setItems(masterData.filtered(tx ->
                tx.getTransactionId().toLowerCase().contains(q) ||
                tx.getUserId().toLowerCase().contains(q)
            ));
        });
    }

    private void setupTable() {
        TableColumn<Transaction, String> idCol = new TableColumn<>("Txn ID");
        idCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTransactionId()));
        idCol.setPrefWidth(120);
        TableColumn<Transaction, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUserId()));
        userIdCol.setPrefWidth(100);
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType()));
        typeCol.setPrefWidth(100);
        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getAmount())));
        amountCol.setPrefWidth(120);
        TableColumn<Transaction, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        statusCol.setPrefWidth(100);
        table.getColumns().addAll(idCol, userIdCol, typeCol, amountCol, statusCol);
    }

    private void loadData() {
        new Thread(() -> {
            List<Transaction> txs = transactionService.getAllTransactions();
            Platform.runLater(() -> masterData.setAll(txs));
        }).start();
    }
}
