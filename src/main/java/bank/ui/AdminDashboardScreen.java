package bank.ui;

import bank.ui.admin.DashboardView;
import bank.ui.admin.Navbar;
import bank.ui.admin.Sidebar;
import bank.ui.admin.UserApprovalView;
import bank.ui.admin.AccountsView;
import bank.ui.admin.TransactionsView;
import bank.ui.admin.ReportsView;
import bank.ui.admin.AdminSettingsView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminDashboardScreen {

    public void show(Stage stage) {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #0f172a;"); // Slate 900 base background

        StackPane rootPane = new StackPane(layout); // Allows stacking toasts on top
        rootPane.setAlignment(Pos.TOP_RIGHT); // Toasts will appear here

        // Setup Layout Modules
        Navbar navbar = new Navbar();
        
        // Core central content area (swappable)
        StackPane contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #0f172a;");
        layout.setCenter(contentArea);

        // Initialize Views
        DashboardView dashboardView = new DashboardView();
        UserApprovalView userApprovalView = new UserApprovalView(rootPane);
        AccountsView accountsView = new AccountsView();
        TransactionsView transactionsView = new TransactionsView();
        ReportsView reportsView = new ReportsView();
        AdminSettingsView settingsView = new AdminSettingsView();

        Sidebar sidebar = new Sidebar((view) -> {
            switch (view.toUpperCase()) {
                case "DASHBOARD":
                    contentArea.getChildren().setAll(dashboardView);
                    break;
                case "USERS":
                    contentArea.getChildren().setAll(userApprovalView);
                    break;
                case "ACCOUNTS":
                    contentArea.getChildren().setAll(accountsView);
                    break;
                case "TRANSACTIONS":
                    contentArea.getChildren().setAll(transactionsView);
                    break;
                case "REPORTS":
                    contentArea.getChildren().setAll(reportsView);
                    break;
                case "SETTINGS":
                    contentArea.getChildren().setAll(settingsView);
                    break;
            }
        });

        layout.setTop(navbar);
        layout.setLeft(sidebar);

        // Default view
        contentArea.getChildren().setAll(dashboardView);

        // Finalize Scene
        Scene scene = new Scene(rootPane, 1280, 720);
        stage.setTitle("Fintech Admin Panel");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
