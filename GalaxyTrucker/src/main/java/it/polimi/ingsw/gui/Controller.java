package it.polimi.ingsw.gui;

import it.polimi.ingsw.Rmi.ClientRmi;

import java.rmi.RemoteException;

public abstract class Controller {
    protected SceneManager sceneManager;
    protected ClientRmi clientRmi;
    protected MainApp mainApp;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setClientRmi(ClientRmi clientRmi) {this.clientRmi = clientRmi;}

    public void setMainApp(MainApp mainApp) {this.mainApp = mainApp;}

    public void onLoad() throws RemoteException {}

}
