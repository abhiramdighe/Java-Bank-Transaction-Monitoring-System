package bank.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WelcomeScreen extends StackPane {

    public WelcomeScreen(Stage primaryStage) {
        setStyle("-fx-background-color: #0f172a;");

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(50));

        Label titleLabel = new Label("WELCOME TO THE BANK TRANSACTION MONITORING SYSTEM");
        titleLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-font-family: 'Segoe UI'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-letter-spacing: 1.5;");

        Label subTitle1 = new Label("Developed as part of the Internal Assessment for");
        subTitle1.setStyle("-fx-text-fill: #a0aec0; -fx-font-family: 'Segoe UI'; -fx-font-size: 15px;");

        Label subTitle2 = new Label("Java Programming & Database Management System");
        subTitle2.setStyle("-fx-text-fill: #e2e8f0; -fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label subTitle3 = new Label("(Semester IV – Diploma in Computer Engineering)");
        subTitle3.setStyle("-fx-text-fill: #a0aec0; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        VBox.setMargin(subTitle1, new Insets(20, 0, 0, 0));
        VBox.setMargin(subTitle3, new Insets(0, 0, 20, 0));

        HBox developedByBox = new HBox(15);
        developedByBox.setAlignment(Pos.CENTER);
        
        Label line1 = new Label("──────────────");
        line1.setStyle("-fx-text-fill: #3b3b4f; -fx-font-size: 12px;");
        Label devTitle = new Label("Developed By");
        devTitle.setStyle("-fx-text-fill: #e2e8f0; -fx-font-family: 'Segoe UI'; -fx-font-size: 15px;");
        Label line2 = new Label("──────────────");
        line2.setStyle("-fx-text-fill: #3b3b4f; -fx-font-size: 12px;");
        
        developedByBox.getChildren().addAll(line1, devTitle, line2);

        HBox authorBox = new HBox(50);
        authorBox.setAlignment(Pos.CENTER);
        Label author1 = new Label("Abhiram Dighe (244011)");
        author1.setStyle("-fx-text-fill: #e2e8f0; -fx-font-family: 'Segoe UI'; -fx-font-size: 15px;");
        Label author2 = new Label("Pracheta Satapathy (244012)");
        author2.setStyle("-fx-text-fill: #e2e8f0; -fx-font-family: 'Segoe UI'; -fx-font-size: 15px;");
        authorBox.getChildren().addAll(author1, author2);

        Button continueBtn = new Button("CONTINUE TO LOGIN");
        continueBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 14 120; -fx-background-radius: 8; -fx-cursor: hand;");
        
        continueBtn.setOnMouseEntered(e -> continueBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 14 120; -fx-background-radius: 8; -fx-cursor: hand;"));
        continueBtn.setOnMouseExited(e -> continueBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 14 120; -fx-background-radius: 8; -fx-cursor: hand;"));

        continueBtn.setOnAction(e -> {
            primaryStage.getScene().setRoot(new LoginScreen(primaryStage));
        });

        VBox.setMargin(continueBtn, new Insets(30, 0, 0, 0));

        contentBox.getChildren().addAll(titleLabel, subTitle1, subTitle2, subTitle3, developedByBox, authorBox, continueBtn);
        getChildren().add(contentBox);
    }
}
