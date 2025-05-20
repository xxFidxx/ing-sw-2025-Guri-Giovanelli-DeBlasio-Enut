package it.polimi.ingsw.gui;

public abstract class Controller {
    protected SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
