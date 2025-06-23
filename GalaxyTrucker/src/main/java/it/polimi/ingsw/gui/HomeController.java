package it.polimi.ingsw.gui;

import com.sun.javafx.scene.control.InputField;
import it.polimi.ingsw.Rmi.ClientRmi;
import it.polimi.ingsw.Rmi.VirtualServerRmi;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class HomeController extends Controller {

    @FXML private TextField serverIpField;

    @FXML
    private void handleJoin() throws Exception {
        mainApp.startClient(serverIpField.getText());
        sceneManager.switchTo("lobby");
        mainApp.getControllers().get("lobby").onLoad();
    }

}
