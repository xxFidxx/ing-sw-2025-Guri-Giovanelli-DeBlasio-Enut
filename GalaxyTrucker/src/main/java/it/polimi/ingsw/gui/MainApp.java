package it.polimi.ingsw.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        SceneManager sceneManager = new SceneManager(primaryStage);

        // Home
        Button toLobby = new Button("to lobby");
        Label homeLabel = new Label("Home Scene");
        StackPane homeLayout = new StackPane();
        homeLayout.getChildren().addAll(homeLabel, toLobby);
        StackPane.setAlignment(homeLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(toLobby, Pos.CENTER);
        Scene homeScene = new Scene(homeLayout, 400, 300);

        // Lobby
        Button toAssembly = new Button("to assembly");
        Label lobbyLabel = new Label("Lobby Scene");
        StackPane lobbyLayout = new StackPane();
        lobbyLayout.getChildren().addAll(lobbyLabel, toAssembly);
        StackPane.setAlignment(lobbyLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(toAssembly, Pos.CENTER);
        Scene lobbyScene = new Scene(lobbyLayout, 400, 300);

        // Assembly
        Button toGame = new Button("to game");
        Label assemblyLabel = new Label("Assembly Scene");
        StackPane assemblyLayout = new StackPane();
        assemblyLayout.getChildren().addAll(assemblyLabel, toGame);
        StackPane.setAlignment(assemblyLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(toGame, Pos.CENTER);
        Scene assemblyScene = new Scene(assemblyLayout, 400, 300);

        // Game
        Button toEndScreen = new Button("to end screen");
        Label gameLabel = new Label("Game Scene");
        StackPane gameLayout = new StackPane();
        gameLayout.getChildren().addAll(gameLabel, toEndScreen);
        StackPane.setAlignment(gameLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(toEndScreen, Pos.CENTER);
        Scene gameScene = new Scene(gameLayout, 400, 300);

        // End Screen
        Button toHome = new Button("to home");
        Label endScreenLabel = new Label("End Screen Scene");
        StackPane endScreenLayout = new StackPane();
        endScreenLayout.getChildren().addAll(endScreenLabel, toHome);
        StackPane.setAlignment(endScreenLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(toHome, Pos.CENTER);
        Scene endScreenScene = new Scene(endScreenLayout, 400, 300);

        // Register scenes
        sceneManager.addScene("home", homeScene);
        sceneManager.addScene("lobby", lobbyScene);
        sceneManager.addScene("assembly", assemblyScene);
        sceneManager.addScene("game", gameScene);
        sceneManager.addScene("end", endScreenScene);

        // Set up button actions
        toLobby.setOnAction(e -> sceneManager.switchTo("lobby"));
        toAssembly.setOnAction(e -> sceneManager.switchTo("assembly"));
        toGame.setOnAction(e -> sceneManager.switchTo("game"));
        toEndScreen.setOnAction(e -> sceneManager.switchTo("end"));
        toHome.setOnAction(e -> sceneManager.switchTo("home"));

        // Start with home
        sceneManager.switchTo("home");

        primaryStage.setTitle("Scene Manager Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}