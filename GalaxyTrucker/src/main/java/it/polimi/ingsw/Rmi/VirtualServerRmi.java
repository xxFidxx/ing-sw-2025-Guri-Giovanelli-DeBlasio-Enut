package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.Server.VirtualServer;
import it.polimi.ingsw.controller.LobbyExceptions;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends Remote, VirtualServer {

    void connect(VirtualViewRmi client) throws RemoteException;

    // metodi controller:


    void addNickname(VirtualViewRmi client, String nickname) throws RemoteException, LobbyExceptions;

    @Override
    void createLobby(VirtualViewRmi client,int number) throws RemoteException, LobbyExceptions;

    void pickTile(VirtualViewRmi clientRmi, int input) throws RemoteException;

    void drawCard(VirtualViewRmi clientRmi) throws RemoteException;
}

