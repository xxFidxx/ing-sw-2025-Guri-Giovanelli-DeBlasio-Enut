package it.polimi.ingsw.gui;

import javafx.fxml.FXML;

public class AssemblyController extends Controller {

    @FXML
    private void handleNext() {
        sceneManager.switchTo("game");
    }
}
