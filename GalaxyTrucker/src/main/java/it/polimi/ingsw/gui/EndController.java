package it.polimi.ingsw.gui;

import javafx.fxml.FXML;

public class EndController extends Controller {

    @FXML
    private void handleNext() {
        sceneManager.switchTo("home");
    }
}
