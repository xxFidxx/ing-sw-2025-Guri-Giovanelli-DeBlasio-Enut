package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.view.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualViewRmi extends Remote, VirtualView {
    @Override
    void showUpdate(Event event) throws RemoteException, InterruptedException;
}

