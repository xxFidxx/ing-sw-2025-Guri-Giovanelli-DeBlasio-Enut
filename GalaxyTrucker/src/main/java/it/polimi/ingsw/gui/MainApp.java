package it.polimi.ingsw.gui;

import it.polimi.ingsw.Rmi.ClientRmi;
import it.polimi.ingsw.Rmi.VirtualServerRmi;
import it.polimi.ingsw.controller.network.data.ListCabinAliens;
import it.polimi.ingsw.controller.network.data.TileData;
import it.polimi.ingsw.gui.pageControllers.AssemblyController;
import it.polimi.ingsw.gui.pageControllers.GameController;
import it.polimi.ingsw.gui.pageControllers.LobbyController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class MainApp extends Application {

    private SceneManager sceneManager;
    private ClientRmi clientRmi;
    private Map<String, Controller> controllers = new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.sceneManager = new SceneManager(primaryStage);

        loadScene("home", "/fxmls/home.fxml");
        loadScene("lobby", "/fxmls/lobby.fxml");
        loadScene("assembly", "/fxmls/assembly.fxml");
        loadScene("game", "/fxmls/game.fxml");
        loadScene("end", "/fxmls/end.fxml");

        // Start with the menu scene
        sceneManager.switchTo("home");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public void startClient(String serverIp) throws Exception {
        final String serverName = "ServerRmi";

        Registry registry = LocateRegistry.getRegistry(serverIp, 1234);
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

    public void updateSpaceship(TileData[][] tileIds) {
        ((AssemblyController) controllers.get("assembly")).setLastSpaceship(tileIds);
    }

    public void adjustShip(TileData[][] tileIds) {
        Platform.runLater(() -> {
            ((AssemblyController) controllers.get("assembly")).adjustShip(tileIds);
        });
    }

    public void selectShip(TileData[][] tileIds) {
        Platform.runLater(() -> {
            ((AssemblyController) controllers.get("assembly")).selectShip(tileIds);
        });
    }

    public void chooseAlien(ListCabinAliens cabinAliens) {
        Platform.runLater(() -> {
            ((AssemblyController) controllers.get("assembly")).chooseAlien(cabinAliens);
        });
    }

    public void turnStart(int [] boardInfo, HashMap<String, Integer> playerColor) {
            sceneManager.switchTo("game");
        Platform.runLater(() -> {
            ((GameController) controllers.get("game")).updateBoard(boardInfo);
            ((GameController) controllers.get("game")).setPlayerColorArea(playerColor);
        });
    }

    public void drawCard(String name, Integer level) {
        Platform.runLater(() -> {
            ((GameController) controllers.get("game")).setCard(name,level);
        });
    }


}