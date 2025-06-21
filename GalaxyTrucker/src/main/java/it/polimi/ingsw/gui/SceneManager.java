package it.polimi.ingsw.gui;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private Stage stage;
    private Map<String, Scene> scenes = new HashMap<>();

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    // Add a scene with a key
    public void addScene(String name, Scene scene) {
        scenes.put(name, scene);
    }

    // Switch to a scene by key
    public void switchTo(String name) {
        Platform.runLater(() -> {
            Scene scene = scenes.get(name);
            if (scene != null) {
                stage.setScene(scene);
            }
        });
    }
}