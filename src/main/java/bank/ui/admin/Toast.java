package bank.ui.admin;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Toast {
    public static void show(StackPane root, String message, boolean isError) {
        Platform.runLater(() -> {
            Label label = new Label(message);
            label.setStyle("-fx-text-fill: white; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold;");
            
            StackPane toastLayout = new StackPane(label);
            toastLayout.setPadding(new Insets(15, 30, 15, 30));
            toastLayout.setMaxWidth(400);
            toastLayout.setMaxHeight(50);
            
            String bg = isError ? "#f87171" : "#4ade80";
            toastLayout.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8;");
            StackPane.setAlignment(toastLayout, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(toastLayout, new Insets(0, 30, 30, 0));

            root.getChildren().add(toastLayout);

            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> root.getChildren().remove(toastLayout));
            delay.play();
        });
    }
}
