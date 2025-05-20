package it.polimi.ingsw.gui;

import it.polimi.ingsw.Rmi.ClientRmi;

public abstract class Controller {
    protected SceneManager sceneManager;
    protected ClientRmi clientRmi;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setClientRmi(ClientRmi clientRmi) {this.clientRmi = clientRmi;}
}
