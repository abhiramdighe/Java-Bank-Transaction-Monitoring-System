package bank.ui;

import bank.repository.AuthRepository;
import bank.security.AuthService;
import bank.security.FaceRecognitionService;
import bank.service.EmailService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import static javafx.embed.swing.SwingFXUtils.toFXImage;
import javafx.scene.layout.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_videoio;
import java.awt.image.BufferedImage;

public class RegistrationScreen extends StackPane {

    private final AuthRepository authRepository = new AuthRepository();
    private final EmailService emailService = new EmailService();
    private byte[] faceData = null;
    
    private volatile boolean cameraActive = true;
    private volatile BufferedImage currentFrame = null;

    public RegistrationScreen() {

        // 🌌 MONOCHROME BACKGROUND (matches welcome screen)
        setStyle("-fx-background-color: #0f172a;");

        VBox card = new VBox(32);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(60, 48, 60, 48));
        card.setMaxWidth(500);
        card.setMinWidth(420);
        card.setMinHeight(540);
        card.setStyle(
            "-fx-background-color: #1c1c27;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #23233A;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 14;" +
            "-fx-effect: dropshadow(gaussian, #00000088, 48, 0.25, 0, 10);"
        );

        // 🏷 Title
        Label title = new Label("Secure Smart Bank");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0; -fx-font-family: 'Segoe UI';");

        Label subtitle = new Label("Create your account");
        subtitle.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 15px; -fx-font-family: 'Segoe UI';");

        // 📋 FORM
        GridPane form = new GridPane();
        form.setVgap(16);
        form.setHgap(12);
        form.setAlignment(Pos.CENTER);

        TextField username = field("Username");
        PasswordField password = password("Password");
        TextField email = field("Email");
        TextField phone = field("Phone");

        form.add(label("Username"), 0, 0);
        form.add(username, 1, 0);

        form.add(label("Password"), 0, 1);
        form.add(password, 1, 1);

        form.add(label("Email"), 0, 2);
        form.add(email, 1, 2);

        form.add(label("Phone"), 0, 3);
        form.add(phone, 1, 3);

        // 🎥 FACE CAPTURE (monochrome style)
        // Webcam preview for face capture
        Button capture = new Button("Capture Face");
        capture.setStyle(
            "-fx-background-color: #23233A;" +
            "-fx-text-fill: #e2e8f0;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-size: 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #3b3b4f;" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 8 24;"
        );

        Label status = new Label("● Not Captured");
        status.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");

        ImageView webcamPreview = new ImageView();
        webcamPreview.setFitWidth(120);
        webcamPreview.setFitHeight(90);
        webcamPreview.setPreserveRatio(true);
        webcamPreview.setStyle("-fx-effect: dropshadow(gaussian, #23233A, 8, 0.2, 0, 2); -fx-background-radius: 8; -fx-background-color: #23233A;");

        Label cameraError = new Label("");
        cameraError.setStyle("-fx-text-fill: #F44336; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';");

        // Start webcam preview in a background thread, but never block UI
        Thread cameraThread = new Thread(() -> {
            try (OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0)) {
                grabber.setImageWidth(320);
                grabber.setImageHeight(240);
                grabber.start();
                Java2DFrameConverter converter = new Java2DFrameConverter();
                while (cameraActive) {
                    Frame frame = grabber.grab();
                    if (frame != null) {
                        BufferedImage img = converter.getBufferedImage(frame);
                        if (img != null) {
                            currentFrame = img;
                            javafx.scene.image.Image fxImg = toFXImage(img, null);
                            Platform.runLater(() -> webcamPreview.setImage(fxImg));
                        }
                    }
                    Thread.sleep(100);
                }
                grabber.stop();
                grabber.release();
            } catch (Throwable ex) {
                Platform.runLater(() -> {
                    cameraError.setText("Webcam preview unavailable. Please check your camera and permissions.");
                });
            }
        });
        cameraThread.setDaemon(true);
        cameraThread.start();

        // Ensure camera stops if window is closed
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin != null) {
                        newWin.setOnCloseRequest(e -> cameraActive = false);
                    }
                });
            }
        });

        capture.setOnAction(e -> {
            capture.setDisable(true);
            status.setText("● Capturing...");
            status.setStyle("-fx-text-fill: #94a3b8;");

            Platform.runLater(() -> {
                try {
                    if (currentFrame != null) {
                        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                            javax.imageio.ImageIO.write(currentFrame, "jpg", baos);
                            faceData = baos.toByteArray();
                        }
                    }
                    
                    if (faceData != null && faceData.length > 0) {
                        status.setText("● Captured");
                        status.setStyle("-fx-text-fill: #e2e8f0;");
                    } else {
                        status.setText("● Failed");
                        status.setStyle("-fx-text-fill: #64748b;");
                        capture.setDisable(false);
                    }
                } catch (Exception ex) {
                    status.setText("● Error");
                    status.setStyle("-fx-text-fill: #F44336;");
                    capture.setDisable(false);
                }
            });
        });

        HBox faceBox = new HBox(14, webcamPreview, capture, status);
        faceBox.setAlignment(Pos.CENTER_LEFT);
        VBox faceSection = new VBox(6, faceBox, cameraError);
        form.add(faceSection, 1, 4);

        // 🔘 PRIMARY BUTTON (no green, clean fintech look)
        Button submit = new Button("Submit");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setStyle(
            "-fx-background-color: #3b82f6;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12 0;"
        );

        // 🔘 SECONDARY BUTTON
        Button back = new Button("Back to Login");
        back.setMaxWidth(Double.MAX_VALUE);
        back.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #3b3b4f;" +
            "-fx-text-fill: #a0aec0;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-size: 15px;" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 10;"
        );

        back.setOnAction(e -> {
            cameraActive = false;
            getScene().setRoot(new LoginScreen((javafx.stage.Stage) getScene().getWindow()));
        });

        // ⚙️ LOGIC (unchanged, safe)
        submit.setOnAction(e -> {
            if (username.getText().isEmpty() ||
                password.getText().isEmpty() ||
                email.getText().isEmpty() ||
                phone.getText().isEmpty()) {
                alert("All fields are required");
                return;
            }
            if (faceData == null) {
                alert("Capture face data first");
                return;
            }
            submit.setDisable(true);
            new Thread(() -> {
                String hash = AuthService.hashPassword(password.getText());
                boolean success = false;
                String errorMsg = null;
                try {
                    success = authRepository.registerUser(
                        username.getText(), hash,
                        email.getText(), phone.getText(), faceData
                    );
                } catch (Exception ex) {
                    errorMsg = ex.getMessage();
                }
                boolean finalSuccess = success;
                String finalErrorMsg = errorMsg;
                Platform.runLater(() -> {
                    submit.setDisable(false);
                    if (finalSuccess) {
                        cameraActive = false;
                        emailService.sendEmailAsync(
                                email.getText(),
                                "Registration Complete (Pending Approval)",
                                "Welcome " + username.getText() + ". Your account is pending admin approval. You will be notified once approved."
                        );
                        alert("Registration successful! Await admin approval before login.");
                        getScene().setRoot(new LoginScreen((javafx.stage.Stage) getScene().getWindow()));
                    } else {
                        if (finalErrorMsg != null && finalErrorMsg.contains("Duplicate")) {
                            alert("Username, email, or phone already exists.");
                        } else {
                            alert("Registration failed. Please try again or contact support.");
                        }
                    }
                });
            }).start();
        });

        card.getChildren().addAll(title, subtitle, form, submit, back);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(60, 0, 60, 0));
        getChildren().add(card);
    }

    // 🔹 COMPONENT HELPERS

    private Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 15px; -fx-font-family: 'Segoe UI';");
        return l;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color: #23233A;" +
            "-fx-text-fill: #e2e8f0;" +
            "-fx-prompt-text-fill: #475569;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-size: 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #3b3b4f;" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 10;"
        );
        return tf;
    }

    private PasswordField password(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setStyle(
            "-fx-background-color: #23233A;" +
            "-fx-text-fill: #e2e8f0;" +
            "-fx-prompt-text-fill: #475569;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-size: 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #3b3b4f;" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 10;"
        );
        return pf;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}