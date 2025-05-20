package it.polimi.ingsw.gui;

import javafx.fxml.FXML;

public class GameController extends Controller {

    @FXML
    private void handleNext() {
        sceneManager.switchTo("end");
    }
}
