package bank.ui.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import bank.repository.UserRepository;

public class DashboardView extends VBox {
    public DashboardView() {
        setSpacing(32);
        setPadding(new Insets(36, 36, 36, 36));
        setStyle("-fx-background-color: transparent;");

        VBox glassCard = new VBox(28);
        glassCard.setAlignment(Pos.TOP_CENTER);
        glassCard.setPadding(new Insets(32, 36, 36, 36));
        glassCard.setMaxWidth(900);
        glassCard.setStyle(
                "-fx-background-color: rgba(30,34,54,0.82);" +
                "-fx-background-radius: 22;" +
                "-fx-border-radius: 22;" +
                "-fx-border-color: rgba(255,255,255,0.13);" +
                "-fx-border-width: 1.5;" +
                "-fx-effect: dropshadow(gaussian, #00000088, 32, 0.18, 0, 8);"
        );

        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-family: 'Poppins', 'Segoe UI', 'Arial'; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #fff; -fx-effect: dropshadow(gaussian, #00000044, 0, 0, 0, 2);");

        HBox cardsContainer = new HBox(28);
        cardsContainer.setAlignment(Pos.CENTER_LEFT);

        UserRepository userRepo = new UserRepository();
        long totalUsers = userRepo.getUsers("ALL").size();
        long pendingUsers = userRepo.getUsers("PENDING").size();
        long activeAccounts = userRepo.getUsers("ACTIVE").size();

        cardsContainer.getChildren().addAll(
                createCard("Total Users", String.valueOf(totalUsers), "#3b82f6"),
                createCard("Pending Approvals", String.valueOf(pendingUsers), "#eab308"),
                createCard("Active Accounts", String.valueOf(activeAccounts), "#10b981"),
                createCard("System Uptime", "99.99%", "#f472b6")
        );

        // Example: Recent Activity (placeholder)
        VBox activityBox = new VBox(10);
        activityBox.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 14; -fx-padding: 18 24; -fx-effect: dropshadow(gaussian, #00000022, 0, 0, 0, 2);");
        Label activityTitle = new Label("Recent Activity");
        activityTitle.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");
        Label activity1 = new Label("• User JohnDoe approved");
        activity1.setStyle("-fx-text-fill: #a5b4fc; -fx-font-size: 14px;");
        Label activity2 = new Label("• 2 new accounts created");
        activity2.setStyle("-fx-text-fill: #f9fafb; -fx-font-size: 14px;");
        Label activity3 = new Label("• Transaction flagged for review");
        activity3.setStyle("-fx-text-fill: #facc15; -fx-font-size: 14px;");
        activityBox.getChildren().addAll(activityTitle, activity1, activity2, activity3);

        glassCard.getChildren().addAll(title, cardsContainer, activityBox);
        getChildren().setAll(glassCard);
    }

    private VBox createCard(String title, String value, String accentColor) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 14; -fx-border-color: " + accentColor + "; -fx-border-width: 0 0 0 4; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        card.setPrefWidth(200);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 32px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
}
