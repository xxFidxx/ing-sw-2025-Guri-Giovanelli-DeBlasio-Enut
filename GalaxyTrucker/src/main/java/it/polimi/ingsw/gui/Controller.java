package it.polimi.ingsw.gui;

import it.polimi.ingsw.Rmi.ClientRmi;
import javafx.application.Platform;

import java.rmi.RemoteException;
import java.util.function.Consumer;

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

    public void waitPlayer(){
        ShowTextUtils.showTextVolatile("Information", "Please wait until the other players complete their actions");
    }

    public void askSurrender(){
        System.out.println("[DEBUG] Controller.askSurrender() chiamato");
        Platform.runLater(()-> {
            ShowTextUtils.askYesNo("SURRENDER?", "Press yes if you want to surrender, else press no", (Boolean surrender) -> {
                        if (surrender) {
                            try {
                                clientRmi.server.surrender(clientRmi);
                            } catch (RemoteException e) {
                                ShowTextUtils.showTextVolatile("Error", e.getMessage());
                            }
                        } else {
                            try {
                                clientRmi.server.handleSurrenderEnded(clientRmi);
                            } catch (RemoteException e) {
                                ShowTextUtils.showTextVolatile("Error", e.getMessage());
                            }
                        }
                    }
            );
        });
    }

    public void askSkip(String text, Consumer<Boolean> callback) {
        ShowTextUtils.askYesNo("SKIP?", text, callback);
    }
}
