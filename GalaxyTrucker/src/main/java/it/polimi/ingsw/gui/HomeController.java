package it.polimi.ingsw.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;


public class HomeController extends Controller {

    @FXML
    private ChoiceBox<String> lobbySizeBox;

    public void initialize() {
        lobbySizeBox.getItems().addAll("1", "2", "3", "4");
    }

    @FXML
    private void handleHost() {
        sceneManager.switchTo("lobby");
    }

    @FXML
    private void handleJoin() {
        sceneManager.switchTo("lobby");
    }
}
