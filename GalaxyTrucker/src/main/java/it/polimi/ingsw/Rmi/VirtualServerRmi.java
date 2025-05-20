package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.Server.VirtualServer;
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.model.game.SpaceShipPlanceException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface VirtualServerRmi extends Remote, VirtualServer {

    void connect(VirtualViewRmi client) throws RemoteException;

    // metodi controller:


    void addNickname(VirtualViewRmi client, String nickname) throws RemoteException, LobbyExceptions;

    @Override
    void createLobby(VirtualViewRmi client,int number) throws RemoteException, LobbyExceptions;

    void pickTile(VirtualViewRmi clientRmi, int input) throws RemoteException;

    void drawCard() throws RemoteException;

    void checkStorage(VirtualViewRmi clientRmi) throws Exception;

    void endCrafting(VirtualViewRmi clientRmi) throws Exception;

    void addGood(VirtualViewRmi clientRmi, int cargoIndex, int goodIndex, int rewardIndex) throws RemoteException;

    void swapGoods(VirtualViewRmi clientRmi, int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) throws RemoteException;

    void removeGood(VirtualViewRmi clientRmi, int cargoIndex, int goodIndex) throws RemoteException;

    void acceptCard(VirtualViewRmi client) throws RemoteException;

    //void printSpaceship(VirtualViewRmi clientRmi) throws RemoteException;

    void addTile(VirtualViewRmi clientRmi, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException;

    void charge(VirtualViewRmi clientRmi, int i) throws RemoteException;

    void putTileBack(VirtualViewRmi client) throws RemoteException;

    void choosePlanets(VirtualViewRmi clientRmi, int i)throws RemoteException;

    void manageCard() throws RemoteException;

    void addReserveSpot(VirtualViewRmi clientRmi) throws RemoteException ;

    void endCargoManagement(VirtualViewRmi clientRmi) throws RemoteException ;

    void chargeCannons(VirtualViewRmi clientRmi, ArrayList<Integer> chosenIndices) throws RemoteException;

    void rotateClockwise(VirtualViewRmi clientRmi) throws RemoteException;

    void removeAdjust(VirtualViewRmi clientRmi, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException;

    void selectShipPart(VirtualViewRmi clientRmi, int part) throws RemoteException;

    void playerHit(VirtualViewRmi clientRmi) throws RemoteException;

    void playerProtected(VirtualViewRmi clientRmi) throws RemoteException;

    boolean addAlienCabin(VirtualViewRmi clientRmi, int cabinId, String alienColor) throws RemoteException;

    void handleEndChooseAliens(VirtualViewRmi clientRmi) throws RemoteException;

    boolean removeFigure(VirtualViewRmi clientRmi, int cabinId, String part) throws RemoteException;

    void surrend(VirtualViewRmi clientRmi) throws RemoteException;

    void handleSurrenderEnded(VirtualViewRmi clientRmi) throws RemoteException;

    boolean removeBatteries(VirtualViewRmi clientRmi, int powerCenterId, int batteries)throws RemoteException;

    void endManagement(VirtualViewRmi clientRmi)throws RemoteException;

    boolean removeMVGood(ClientRmi clientRmi, int cargoIndex, int goodIndex) throws RemoteException;
}
