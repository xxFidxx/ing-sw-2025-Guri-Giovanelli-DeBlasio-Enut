package it.polimi.ingsw.gui.pageControllers;

import it.polimi.ingsw.gui.Controller;
import javafx.fxml.FXML;

public class EndController extends Controller {

    @FXML
    private void handleNext() {
        sceneManager.switchTo("home");
    }
}
