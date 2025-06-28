package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.Server.VirtualServer;
import it.polimi.ingsw.controller.ControllerExceptions; // Eccezioni di controller
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.SpaceShipPlanceException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface VirtualServerRmi extends Remote, VirtualServer {

    void connect(VirtualViewRmi client) throws RemoteException;

    void addNickname(VirtualViewRmi client, String nickname) throws RemoteException, LobbyExceptions, InterruptedException;

    void createLobby(int number) throws RemoteException, LobbyExceptions;

    void pickTile(VirtualViewRmi clientRmi, int input) throws RemoteException;

    void checkStorage(VirtualViewRmi clientRmi) throws RemoteException, CargoManagementException;

    void endCrafting(VirtualViewRmi clientRmi) throws Exception;

    void addGood(VirtualViewRmi clientRmi, int cargoIndex, int goodIndex, int rewardIndex) throws RemoteException, CargoManagementException;

    void swapGoods(VirtualViewRmi clientRmi, int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) throws RemoteException, CargoManagementException;

    void removeGood(VirtualViewRmi clientRmi, int cargoIndex, int goodIndex) throws RemoteException, CargoManagementException;

    void acceptCard(VirtualViewRmi client) throws RemoteException;

    void addTile(VirtualViewRmi clientRmi, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException;

    void chargeEngines(VirtualViewRmi clientRmi, int i) throws RemoteException, ControllerExceptions; // AGGIUNTO

    void putTileBack(VirtualViewRmi client) throws RemoteException;

    void choosePlanets(VirtualViewRmi clientRmi, int i) throws RemoteException, CargoManagementException, ControllerExceptions; // AGGIUNTO

    void manageCard() throws RemoteException;

    void handlePlanets(VirtualViewRmi clientRmi) throws RemoteException, CargoManagementException;

    void fromChargeToManage(VirtualViewRmi clientRmi) throws RemoteException;

    void addReserveSpot(VirtualViewRmi clientRmi) throws RemoteException;

    void endCargoManagement(VirtualViewRmi clientRmi) throws RemoteException;

    void chargeCannons(VirtualViewRmi clientRmi, ArrayList<Integer> chosenIndices) throws RemoteException;

    void rotateClockwise(VirtualViewRmi clientRmi) throws RemoteException;

    void removeAdjust(VirtualViewRmi clientRmi, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException;

    void selectShipPart(VirtualViewRmi clientRmi, int part) throws RemoteException;

    void playerHit(VirtualViewRmi clientRmi) throws RemoteException;

    void playerProtected(VirtualViewRmi clientRmi) throws RemoteException, ControllerExceptions;

    boolean addAlienCabin(VirtualViewRmi clientRmi, int cabinId, String alienColor) throws RemoteException;

    void handleEndChooseAliens(VirtualViewRmi clientRmi) throws RemoteException;

    boolean removeFigure(VirtualViewRmi clientRmi, int cabinId) throws RemoteException;

    void surrender(VirtualViewRmi clientRmi) throws RemoteException;

    void handleSurrenderEnded(VirtualViewRmi clientRmi) throws RemoteException;

    boolean removeBatteries(VirtualViewRmi clientRmi, int powerCenterId, int batteries) throws RemoteException;

    void endManagement(VirtualViewRmi clientRmi) throws RemoteException;

    void endCrewManagement(VirtualViewRmi clientRmi) throws RemoteException;

    void endMVGoodsManagement(VirtualViewRmi clientRmi) throws RemoteException;

    boolean removeMVGood(VirtualViewRmi clientRmi, int cargoIndex, int goodIndex) throws RemoteException;

    void showDecks(VirtualViewRmi clientRmi) throws RemoteException;

    boolean showCardsbyDeck(VirtualViewRmi clientRmi, int nDeck) throws RemoteException;

    void endShowCards(VirtualViewRmi clientRmi, int i) throws RemoteException;

    void removeFigureEpidemic(VirtualViewRmi clientRmi) throws RemoteException;

    boolean isEpidemicDone(VirtualViewRmi clientRmi) throws RemoteException;

    void fromMvGoodstoBatteries(VirtualViewRmi clientRmi, int nBatteries) throws RemoteException;

    boolean startTimer() throws RemoteException;

    int[] guiBoardInfo() throws RemoteException;

    HashMap<String, Integer> playerColors() throws RemoteException;
}
