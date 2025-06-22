package it.polimi.ingsw.gui;

import it.polimi.ingsw.Rmi.ClientRmi;
import it.polimi.ingsw.Rmi.VirtualServerRmi;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainApp extends Application {

    private SceneManager sceneManager;
    private ClientRmi clientRmi;
    private Map<String, Controller> controllers = new HashMap<>();

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
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public void startClient() throws Exception {
        final String serverName = "ServerRmi";

        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1234);
        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);

        this.clientRmi = new ClientRmi(server);
        this.clientRmi.setMainApp(this);
        this.clientRmi.run(1);

        for (Controller controller : controllers.values()) {
            controller.setClientRmi(this.clientRmi);
        }
    }

    private void loadScene(String key, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.setSceneManager(this.sceneManager);
        controller.setMainApp(this);
        controllers.put(key, controller);

        this.sceneManager.addScene(key, new Scene(root));
    }

    public Map<String, Controller> getControllers() {
        return controllers;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void gameInit() {
        sceneManager.switchTo("assembly");
    }

    public void lobbyPhase() {
        ((LobbyController) controllers.get("lobby")).lobbyPhase();
    }
}