package it.polimi.ingsw.gui;

import javafx.fxml.FXML;

public class LobbyController extends Controller {

    @FXML
    private void handleNext() {
        sceneManager.switchTo("assembly");
    }
}
