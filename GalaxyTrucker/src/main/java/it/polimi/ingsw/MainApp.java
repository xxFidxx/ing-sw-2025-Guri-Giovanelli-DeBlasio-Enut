package it.polimi.ingsw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello JavaFX!");
        primaryStage.setScene(new Scene(new Label("Hello, World!"), 300, 100));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}