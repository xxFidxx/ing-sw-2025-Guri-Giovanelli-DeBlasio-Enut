package it.polimi.ingsw.gui;

import it.polimi.ingsw.Rmi.ClientRmi;
import it.polimi.ingsw.Rmi.VirtualServerRmi;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainApp extends Application {

    private SceneManager sceneManager;
    private ClientRmi clientRmi;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.sceneManager = new SceneManager(primaryStage);
        final String serverName = "ServerRmi";

        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1234);
        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);

        this.clientRmi = new ClientRmi(server);
        this.clientRmi.run(1);

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
        controller.setClientRmi(this.clientRmi);

        this.sceneManager.addScene(key, new Scene(root));
    }

    public static void main(String[] args) {
        launch(args);
    }
}