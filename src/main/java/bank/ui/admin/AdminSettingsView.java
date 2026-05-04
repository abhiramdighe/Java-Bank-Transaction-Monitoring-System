package bank.ui.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class AdminSettingsView extends VBox {
    public AdminSettingsView() {
        setSpacing(20);
        setPadding(new Insets(30, 40, 30, 40));
        setStyle("-fx-background-color: transparent;");

        Label title = new Label("Admin Settings");
        title.setStyle("-fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        Label profileLabel = new Label("Profile");
        profileLabel.setStyle("-fx-font-size: 16px; -fx-font-family: 'Poppins'; -fx-text-fill: #cbd5e1; -fx-font-weight: bold;");
        TextField tfName = new TextField();
        tfName.setPromptText("Admin Name");
        tfName.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #e2e8f0; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #334155; -fx-border-width: 1.2; -fx-padding: 10 14;");
        tfName.setMaxWidth(300);

        Label passwordLabel = new Label("Change Password");
        passwordLabel.setStyle("-fx-font-size: 16px; -fx-font-family: 'Poppins'; -fx-text-fill: #cbd5e1; -fx-font-weight: bold;");
        PasswordField pfOld = new PasswordField();
        pfOld.setPromptText("Old Password");
        pfOld.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #e2e8f0; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #334155; -fx-border-width: 1.2; -fx-padding: 10 14;");
        pfOld.setMaxWidth(300);
        PasswordField pfNew = new PasswordField();
        pfNew.setPromptText("New Password");
        pfNew.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #e2e8f0; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #334155; -fx-border-width: 1.2; -fx-padding: 10 14;");
        pfNew.setMaxWidth(300);
        Button btnChange = new Button("Change Password");
        btnChange.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 24;");

        HBox btnRow = new HBox(15, btnChange);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(title, profileLabel, tfName, passwordLabel, pfOld, pfNew, btnRow, spacer);
    }
}
