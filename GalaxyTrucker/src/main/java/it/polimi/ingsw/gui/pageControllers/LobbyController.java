package it.polimi.ingsw.gui.pageControllers;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.gui.Controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.rmi.RemoteException;

public class LobbyController extends Controller {

    @FXML private ChoiceBox<String> lobbySizeBox;
    @FXML private TextField nicknameField;
    @FXML private AnchorPane createLobbyPane;
    @FXML private AnchorPane nicknamePane;
    @FXML private AnchorPane lobbyPane;

    @Override
    public void onLoad() {
        if (clientRmi.getCurrentState() == GameState.IDLE) {
            lobbySizeBox.getItems().addAll("2", "3", "4");
            lobbySizeBox.setValue("2");

            createLobbyPane.setVisible(true);
            nicknamePane.setVisible(false);
            lobbyPane.setVisible(false);
        }
        else {
            createLobbyPane.setVisible(false);
            nicknamePane.setVisible(true);
            lobbyPane.setVisible(false);
        }
    }

    @FXML
    private void handleCreateLobby() throws RemoteException {
        clientRmi.server.createLobby(Integer.parseInt(lobbySizeBox.getValue()));

        createLobbyPane.setVisible(false);
        nicknamePane.setVisible(true);
    }

    @FXML
    private void handleAddNickname() throws RemoteException, InterruptedException {
        clientRmi.server.addNickname(clientRmi, nicknameField.getText());

        nicknamePane.setVisible(false);
        lobbyPane.setVisible(true);
    }

    @FXML
    private void handleNext() {
        sceneManager.switchTo("assembly");
    }

    public void lobbyPhase() {
        Platform.runLater(() -> {
            createLobbyPane.setVisible(false);
            nicknamePane.setVisible(true);
            lobbyPane.setVisible(false);
        });
    }
}
