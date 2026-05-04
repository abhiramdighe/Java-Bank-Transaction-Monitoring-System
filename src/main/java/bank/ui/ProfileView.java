package bank.ui;

import bank.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileView extends VBox {
    public ProfileView(User user, Runnable onBack) {
        setSpacing(30);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #23233A; -fx-background-radius: 12;");

        Label title = new Label("Profile");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0; -fx-font-family: 'Segoe UI';");

        // Avatar
        ImageView avatar = new ImageView(new Image("/bank/ui/avatar.png", 80, 80, true, true));
        avatar.setStyle("-fx-effect: dropshadow(gaussian, #8D7B68, 8, 0.2, 0, 2);");

        // Info fields
        VBox infoBox = new VBox(18);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPadding(new Insets(20, 0, 0, 0));

        infoBox.getChildren().addAll(
            createField("Username", user.getUsername()),
            createField("Email", user.getEmail()),
            createField("Phone", user.getPhone()),
            createField("Role", user.getRole()),
            createField("Status", user.getStatus())
        );

        Button btnBack = new Button("← Back");
        btnBack.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 30;");
        btnBack.setOnAction(e -> onBack.run());

        getChildren().addAll(title, avatar, infoBox, btnBack);
    }

    private HBox createField(String label, String value) {
        Label lbl = new Label(label + ":");
        lbl.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 15px; -fx-font-family: 'Segoe UI';");
        TextField tf = new TextField(value);
        tf.setEditable(false);
        tf.setStyle("-fx-background-color: #2E2E3A; -fx-text-fill: #e2e8f0; -fx-font-size: 15px; -fx-background-radius: 6; -fx-border-color: #3b3b4f; -fx-border-radius: 6;");
        tf.setPrefWidth(260);
        HBox box = new HBox(18, lbl, tf);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}
