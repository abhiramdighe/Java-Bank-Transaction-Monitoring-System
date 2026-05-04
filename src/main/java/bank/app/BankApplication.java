package bank.app;

import bank.ui.WelcomeScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BankApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Main landing route
        WelcomeScreen root = new WelcomeScreen(primaryStage);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Smart Banking System - Secured Portal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
