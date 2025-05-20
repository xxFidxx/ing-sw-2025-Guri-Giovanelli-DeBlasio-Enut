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
    private ChoiceBox<String> lobbySizeBox;

    public void initialize() {
        lobbySizeBox.getItems().addAll("1", "2", "3", "4");
        lobbySizeBox.setValue("2");
    }

    @FXML
    private void handleHost() throws RemoteException {
        sceneManager.switchTo("lobby");
        int lobbySize = Integer.parseInt(lobbySizeBox.getValue());
        clientRmi.server.createLobby(clientRmi, lobbySize);
    }

    @FXML
    private void handleJoin() throws Exception {
        sceneManager.switchTo("lobby");
    }

    private void startClient() throws Exception {
        final String serverName = "ServerRmi";

        // qua c'è da metterci come primo argomento identificativo registro, visto che voglio testare sulla mia macchina
        // ora l'ip è quello della macchina locale: 127.0.0.1 indirizzo local host
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1234);
        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);

        new ClientRmi(server).run(1);
    }
}
