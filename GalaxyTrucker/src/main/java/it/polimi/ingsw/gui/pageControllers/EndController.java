package it.polimi.ingsw.gui.pageControllers;

import it.polimi.ingsw.gui.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EndController extends Controller {

    @FXML private Label endTextLabel;

    @FXML
    private void handleNext() {
        sceneManager.switchTo("home");
    }

    public void setText(String text) {
        endTextLabel.setText(text);
    }
}
