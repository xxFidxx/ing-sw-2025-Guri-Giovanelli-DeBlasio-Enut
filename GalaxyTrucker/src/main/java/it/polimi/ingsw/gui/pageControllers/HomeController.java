package it.polimi.ingsw.gui.pageControllers;

import it.polimi.ingsw.gui.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class HomeController extends Controller {

    @FXML private TextField serverIpField;

    @FXML
    private void handleJoin() throws Exception {
        mainApp.startClient(serverIpField.getText());
        sceneManager.switchTo("lobby");
        mainApp.getControllers().get("lobby").onLoad();
    }

}
