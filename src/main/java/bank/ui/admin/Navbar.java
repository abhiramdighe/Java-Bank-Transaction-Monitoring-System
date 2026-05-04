package bank.ui.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import bank.ui.LoginScreen;
import javafx.stage.Stage;

public class Navbar extends HBox {
    public Navbar() {
        setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 0 1 0;");
        setPadding(new Insets(15, 25, 15, 25));
        setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Bank Admin Panel");
        title.setStyle("-fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label notificationIcon = new Label("🔔 3");
        notificationIcon.setStyle("-fx-background-color: #334155; -fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 12; -fx-font-size: 13px;");

        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER);
        Circle avatarInfo = new Circle(16, Color.web("#3b82f6"));
        Label adminName = new Label("Admin Pro");
        adminName.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold; -fx-font-size: 14px;");
        profileBox.getChildren().addAll(avatarInfo, adminName);
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #f87171; -fx-cursor: hand; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.getScene().setRoot(new LoginScreen(stage));
        });

        HBox settingsArea = new HBox(20);
        settingsArea.setAlignment(Pos.CENTER);
        settingsArea.getChildren().addAll(notificationIcon, profileBox, logoutBtn);

        getChildren().addAll(title, spacer, settingsArea);
    }
}
