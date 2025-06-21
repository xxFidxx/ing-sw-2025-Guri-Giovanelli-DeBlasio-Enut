package it.polimi.ingsw.gui;

import it.polimi.ingsw.Rmi.ClientRmi;
import it.polimi.ingsw.Rmi.VirtualServerRmi;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class HomeController extends Controller {

    @FXML
    private void handleJoin() throws Exception {
        mainApp.startClient();
        sceneManager.switchTo("lobby");
        mainApp.getControllers().get("lobby").onLoad();
    }

}
