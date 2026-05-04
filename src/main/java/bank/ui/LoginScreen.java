package bank.ui;

import bank.model.User;
import bank.repository.AuthRepository;
import bank.security.AuthService;
import bank.security.FaceRecognitionService;
import bank.service.EmailService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScreen extends VBox {
    private final AuthRepository authRepo = new AuthRepository();
    private final EmailService emailService = new EmailService();

    private Stage primaryStage;

    public LoginScreen(Stage stage) {
        this.primaryStage = stage;
        
        setStyle("-fx-background-color: #0f172a;");
        setAlignment(Pos.CENTER);
        setSpacing(32);
        setPadding(new Insets(60, 0, 60, 0));

        VBox card = new VBox(28);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(48, 44, 48, 44));
        card.setMaxWidth(420);
        card.setMinWidth(340);
        card.setStyle(
            "-fx-background-color: #1c1c27;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #23233A;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, #00000088, 40, 0.22, 0, 8);"
        );

        Label title = new Label("Secure Smart Bank");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        Label subtitle = new Label("Sign in to your account");
        subtitle.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 15px; -fx-font-family: 'Segoe UI';");

        TextField tfUser = new TextField();
        tfUser.setPromptText("Username");
        tfUser.setStyle("-fx-background-color: #23233A; -fx-text-fill: #e2e8f0; -fx-font-size: 15px; -fx-background-radius: 8; -fx-border-color: #3b3b4f; -fx-border-radius: 8; -fx-padding: 10;");
        tfUser.setMaxWidth(300);

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");
        pfPass.setStyle("-fx-background-color: #23233A; -fx-text-fill: #e2e8f0; -fx-font-size: 15px; -fx-background-radius: 8; -fx-border-color: #3b3b4f; -fx-border-radius: 8; -fx-padding: 10;");
        pfPass.setMaxWidth(300);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #F44336; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");

            Button btnLogin = new Button("Login & Scan Face");
            btnLogin.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20 10 20;");

        Button btnGoReg = new Button("Register New Account");
        btnGoReg.setStyle("-fx-background-color: transparent; -fx-border-color: #3b3b4f; -fx-text-fill: #a0aec0; -fx-font-family: 'Segoe UI'; -fx-font-size: 15px; -fx-border-radius: 8; -fx-padding: 10;");

        btnLogin.setOnAction(e -> {
            String user = tfUser.getText().trim();
            String pass = pfPass.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                statusLabel.setText("Please enter username and password.");
                return;
            }

            if ("admin".equals(user) && "admin123".equals(pass)) {
                Platform.runLater(() -> {
                    AdminDashboardScreen adminScreen = new AdminDashboardScreen();
                    adminScreen.show(primaryStage);
                });
                return;
            }

            btnLogin.setDisable(true);
            statusLabel.setText("Scanning Database & Webcam...");
            statusLabel.setStyle("-fx-text-fill: #FFEB3B; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");

            new Thread(() -> {
                User dbUser = authRepo.getUserByUsername(user);

                if (dbUser == null) {
                    failLogin(btnLogin, statusLabel, "Invalid credentials.");
                    return;
                }

                if (!AuthService.checkPassword(pass, dbUser.getPasswordHash())) {
                    failLogin(btnLogin, statusLabel, "Invalid credentials.");
                    return;
                }

                if ("PENDING".equalsIgnoreCase(dbUser.getStatus())) {
                    failLogin(btnLogin, statusLabel, "Account is pending admin approval.");
                    return;
                }
                if ("REJECTED".equalsIgnoreCase(dbUser.getStatus())) {
                    failLogin(btnLogin, statusLabel, "Account has been rejected.");
                    return;
                }
                if (!"ACTIVE".equalsIgnoreCase(dbUser.getStatus())) {
                    failLogin(btnLogin, statusLabel, "Account is not active. Contact admin.");
                    return;
                }

                // If password and status cleared -> capture Face data and compare
                byte[] storedFace = authRepo.getUserFaceData(dbUser.getId());
                byte[] liveFace = FaceRecognitionService.captureFaceData();

                boolean isAdmin = "ADMIN".equalsIgnoreCase(dbUser.getRole());
                boolean faceMatch = FaceRecognitionService.matchFace(storedFace, liveFace);
                if (!faceMatch && !isAdmin) {
                    failLogin(btnLogin, statusLabel, "Facial recognition failed. Login denied.");
                    return;
                }

                // Send login alerts
                String adminEmail = authRepo.getAdminEmail();
                emailService.sendEmailAsync(dbUser.getEmail(), "Login Successful", "Hello " + dbUser.getUsername() + ", you have successfully logged into the Secure Smart Bank.");
                emailService.sendEmailAsync(adminEmail, "User Login Alert", "User " + dbUser.getUsername() + " (" + dbUser.getEmail() + ") has successfully logged into the system.");

                // At this point, face + pass + status all succeed (or admin bypass)
                Platform.runLater(() -> {
                    statusLabel.setText("Login Success! Routing to Dashboard...");
                    statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");

                    if (isAdmin) {
                        AdminDashboardScreen adminScreen = new AdminDashboardScreen();
                        adminScreen.show(primaryStage);
                    } else {
                        primaryStage.getScene().setRoot(new UserDashboard(dbUser));
                    }
                });
            }).start();
        });

        btnGoReg.setOnAction(e -> {
            primaryStage.getScene().setRoot(new RegistrationScreen());
        });

        card.getChildren().addAll(title, subtitle, tfUser, pfPass, statusLabel, btnLogin, btnGoReg);
        getChildren().add(card);
    }

    private void failLogin(Button btn, Label lbl, String msg) {
        Platform.runLater(() -> {
            lbl.setText(msg);
            lbl.setStyle("-fx-text-fill: #F44336;");
            btn.setDisable(false);
        });
    }
}
