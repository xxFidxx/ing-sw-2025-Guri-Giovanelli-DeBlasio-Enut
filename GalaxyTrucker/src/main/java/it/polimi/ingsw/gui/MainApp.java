package it.polimi.ingsw.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private SceneManager sceneManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.sceneManager = new SceneManager(primaryStage);

        loadScene("home", "/home.fxml");
        loadScene("lobby", "/lobby.fxml");
        loadScene("assembly", "/assembly.fxml");
        loadScene("game", "/game.fxml");
        loadScene("end", "/end.fxml");

        // Start with the menu scene
        sceneManager.switchTo("home");
        primaryStage.show();
    }

    private void loadScene(String key, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.setSceneManager(this.sceneManager);

        this.sceneManager.addScene(key, new Scene(root));
    }

    public static void main(String[] args) {
        launch(args);
    }
}