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

    void checkStorage(VirtualViewRmi clientRmi) throws Exception;

    void endCrafting(VirtualViewRmi clientRmi) throws Exception;

    void addGood(VirtualViewRmi clientRmi, int cargoIndex, int goodIndex, int rewardIndex) throws RemoteException;

    void swapGoods(VirtualViewRmi clientRmi, int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) throws RemoteException;

    void removeGood(VirtualViewRmi clientRmi, int cargoIndex, int goodIndex) throws RemoteException;

    void acceptCard(VirtualViewRmi client) throws RemoteException;

    void rejectCard() throws RemoteException;

    //void printSpaceship(VirtualViewRmi clientRmi) throws RemoteException;

    void endCard(VirtualViewRmi clientRmi) throws RemoteException;
}
