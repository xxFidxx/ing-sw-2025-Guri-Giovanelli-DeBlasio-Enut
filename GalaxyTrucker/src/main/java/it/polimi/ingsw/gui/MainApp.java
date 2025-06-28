package it.polimi.ingsw.gui;

import it.polimi.ingsw.Rmi.ClientRmi;
import it.polimi.ingsw.Rmi.VirtualServerRmi;
import it.polimi.ingsw.controller.network.data.*;
import it.polimi.ingsw.gui.pageControllers.*;
import it.polimi.ingsw.model.resources.GoodsContainer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class MainApp extends Application {

    private SceneManager sceneManager;
    private final Map<String, Controller> controllers = new HashMap<>();
    private Controller activeController;
    private final PopupHandler popupHandler = PopupHandler.getInstance();

    public PopupHandler popupQueue() {
        return popupHandler;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.sceneManager = new SceneManager(primaryStage);

        loadScene("home", "/fxmls/home.fxml");
        loadScene("lobby", "/fxmls/lobby.fxml");
        loadScene("assembly", "/fxmls/assembly.fxml");
        loadScene("game", "/fxmls/game.fxml");
        loadScene("cargoManagement", "/fxmls/cargoManagement.fxml");
        loadScene("crewManagement", "/fxmls/crewManagement.fxml");
        loadScene("batteriesManagement", "/fxmls/batteriesManagement.fxml");
        loadScene("removeMVGoods", "/fxmls/removeMVGoods.fxml");
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

        ClientRmi clientRmi = new ClientRmi(server);
        clientRmi.setMainApp(this);
        clientRmi.run(1);

        for (Controller controller : controllers.values()) {
            controller.setClientRmi(clientRmi);
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
        activeController = controllers.get("assembly");
        sceneManager.switchTo("assembly");
    }

    public void lobbyPhase() {
        ((LobbyController) controllers.get("lobby")).lobbyPhase();
    }

    public void updateSpaceship(TileData[][] tileIds) {
        ((AssemblyController) controllers.get("assembly")).setLastSpaceship(tileIds);
        ((CrewManagementController) controllers.get("crewManagement")).setLastSpaceship(tileIds);

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
            activeController = controllers.get("game");
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



    public void cargoManagement(ArrayList<GoodsContainer> cargos){
        activeController = controllers.get("cargoManagement");
        sceneManager.switchTo("cargoManagement");
        Platform.runLater(() -> {
            ((CargoManagementController) controllers.get("cargoManagement")).setCargos(cargos);
        });
    }

    public void choosePlanet(int size) {
        System.out.println("Show planets choice mainapp");
        Platform.runLater(() -> {
            ((GameController) controllers.get("game")).showPlanetsChoice(size);
        });
    }

    public void askSurrender(){
        System.out.println("[DEBUG] MainApp.askSurrender() chiamato - controller attivo: " + activeController.getClass().getSimpleName());
        Platform.runLater(() -> {
            activeController.askSurrender();
        });
    }

    public void waitPlayer(){
        Platform.runLater(() -> {
            activeController.waitPlayer();
        });
    }

    public void askSkip(String text, Consumer<Boolean> callback) {
        activeController.askSkip(text, callback);
    }

    public void assembly() {
        Platform.runLater(() -> {
            ((AssemblyController) controllers.get("assembly")).assembly();
        });
    }

    public void pickedTile() {
        Platform.runLater(() -> {
            ((AssemblyController) controllers.get("assembly")).pickedTile();
        });
    }

    public void pickReservedCard() {
        Platform.runLater(() -> {
            ((AssemblyController) controllers.get("assembly")).pickedReservedCard();
        });
    }

    public void crewManagement(CrewManagement data) {
        activeController = controllers.get("crewManagement");
        sceneManager.switchTo("crewManagement");
        Platform.runLater(() -> {
            ((CrewManagementController) controllers.get("crewManagement")).showShip(data);
        });
    }

    public void batteriesManagement(BatteriesManagement data) {
        activeController = controllers.get("batteriesManagement");
        sceneManager.switchTo("batteriesManagement");
        Platform.runLater(() -> {
            ((BatteriesManagementController) controllers.get("batteriesManagement")).showShip(data);
        });
    }

    public void removeMVGoods(RemoveMostValuable data) {
        activeController = controllers.get("removeMVGoods");
        sceneManager.switchTo("removeMVGoods");
        Platform.runLater(() -> {
            ((RemoveMVGoodsController) controllers.get("RemoveMVGoodsController")).startRemoveMVGoods(data);
        });
    }
}