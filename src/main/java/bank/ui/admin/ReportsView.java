package bank.ui.admin;

import bank.service.TransactionService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Map;

public class ReportsView extends VBox {
    private final TransactionService transactionService = new TransactionService();

    public ReportsView() {
        setSpacing(20);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: transparent;");

        Label title = new Label("Reports & Analytics");
        title.setStyle("-fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button btnExportCSV = new Button("Export CSV");
        btnExportCSV.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
        Button btnExportPDF = new Button("Export PDF");
        btnExportPDF.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
        actions.getChildren().addAll(btnExportCSV, btnExportPDF);

        VBox statsBox = new VBox(10);
        statsBox.setPadding(new Insets(10, 0, 0, 0));
        statsBox.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10; -fx-padding: 20;");

        Label totalTxLabel = new Label("Total Transactions: ...");
        totalTxLabel.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 16px;");
        Label totalAmountLabel = new Label("Total Amount: ...");
        totalAmountLabel.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 16px;");
        Label depositLabel = new Label("Deposits: ...");
        depositLabel.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 15px;");
        Label withdrawLabel = new Label("Withdrawals: ...");
        withdrawLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 15px;");
        Label transferLabel = new Label("Transfers: ...");
        transferLabel.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 15px;");
        statsBox.getChildren().addAll(totalTxLabel, totalAmountLabel, depositLabel, withdrawLabel, transferLabel);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label comingSoon = new Label("Charts and analytics coming soon...");
        comingSoon.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 16px; -fx-font-family: 'Poppins';");

        getChildren().addAll(title, actions, statsBox, spacer, comingSoon);

        // Load stats in background
        new Thread(() -> {
            Map<String, Object> stats = transactionService.getTransactionStats();
            Platform.runLater(() -> {
                totalTxLabel.setText("Total Transactions: " + stats.getOrDefault("count", 0));
                totalAmountLabel.setText("Total Amount: $" + stats.getOrDefault("total", 0.0));
                depositLabel.setText("Deposits: " + stats.getOrDefault("deposit", 0));
                withdrawLabel.setText("Withdrawals: " + stats.getOrDefault("withdraw", 0));
                transferLabel.setText("Transfers: " + stats.getOrDefault("transfer", 0));
            });
        }).start();
    }
}
