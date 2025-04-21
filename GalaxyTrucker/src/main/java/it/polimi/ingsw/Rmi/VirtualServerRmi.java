package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.Server.VirtualServer;
import it.polimi.ingsw.controller.LobbyExceptions;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends Remote, VirtualServer {

    void connect(VirtualViewRmi client) throws RemoteException;

    // metodi controller:

    @Override
    void addNickname(String nickname) throws RemoteException, LobbyExceptions;

    @Override
    void createLobby(int number) throws RemoteException, LobbyExceptions;
}
