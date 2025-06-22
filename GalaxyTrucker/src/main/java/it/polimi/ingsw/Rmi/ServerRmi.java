package it.polimi.ingsw.Rmi;


import it.polimi.ingsw.controller.ClientListenerRmi;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.SpaceShipPlanceException;
import it.polimi.ingsw.view.VirtualView;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


// Interfaccia che definisce i metodi del server che devono essere resi accessibili ai client per poter interagire con controller
public class ServerRmi extends UnicastRemoteObject implements VirtualServerRmi {
    final Controller controller;
    final List<VirtualViewRmi> clients;
    final Map<VirtualViewRmi, ClientListenerRmi> clientListeners;
    final Map<String,VirtualViewRmi> clientbyNickname;
    final Map<VirtualViewRmi,String> nicknamebyClient;

    // per fare più partite in contemporanea dovrei fare una mappatura Map<Controller, String> dove a ogni controller leghi una lobby o un game
    // bisogna gestire bene cosa succede in caso di disconnessione durante le chiamate ai metodi


    ServerRmi() throws RemoteException {
        super();
        this.clientListeners = new ConcurrentHashMap<>();
        this.nicknamebyClient = new ConcurrentHashMap<>();
        this.controller = new Controller();
        this.clients = new ArrayList<>();
        this.clientbyNickname = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) throws RemoteException {
        final String serverName = "ServerRmi";

        try {

            String hotspotIp = "127.0.0.1";
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");


            Registry registry = LocateRegistry.createRegistry(1234);
            VirtualServerRmi server = new ServerRmi();
            registry.rebind(serverName, server);

            System.out.println("Server running on: " + hotspotIp + ":1234");
        } catch (Exception e) {
            System.err.println("Server failed: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void removeClient(VirtualViewRmi client){
        synchronized(clients){
            clients.remove(client);
        }
        synchronized (clientbyNickname){
            clientbyNickname.values().removeIf(v -> v.equals(client));
        }
    }

    public void notifyClient(VirtualViewRmi client, Event event) {
        try {
            client.showUpdate(event);
        } catch (RemoteException e) {
            System.out.println("Client disconnected: " + e);
            removeClient(client);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread Interrupt");
        }
    }

    @Override
    public void connect(VirtualView client) throws RemoteException {
        synchronized (clients) {
            clients.add((VirtualViewRmi) client);
        }
        ClientListenerRmi clientListenerRmi = new ClientListenerRmi((VirtualViewRmi) client, this);
        clientListeners.put((VirtualViewRmi) client, clientListenerRmi);
        synchronized(controller){
            controller.addEventListener(clientListenerRmi);
        }
        System.out.println("Client connected");
    }

    @Override
    public void addNickname(VirtualView client, String nickname) throws RemoteException, LobbyExceptions {
        ClientListenerRmi clientListenerRmi = clientListeners.get((VirtualViewRmi) client);
        synchronized(controller){
            controller.addNickname(clientListenerRmi,nickname);
        }
        System.out.println("Nickname added\n");
        clientbyNickname.put(nickname, (VirtualViewRmi) client);
        nicknamebyClient.put((VirtualViewRmi) client,nickname);
    }

    @Override
    public void createLobby(int number) throws RemoteException, LobbyExceptions {
        synchronized(controller){
            controller.createLobby(number);
        }
        System.out.println("Lobby created\n");
    }

    public void pickTile(VirtualViewRmi client, int id) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(client);
        controller.pickTile(listener, id);
    }

    @Override
    public void drawCard() throws RemoteException {
        controller.drawCard();
    }

    @Override
    public boolean startTimer() throws RemoteException {
       return controller.startTimer();
    }

    @Override
    public void endCrafting(VirtualViewRmi client) throws Exception {
        ClientListenerRmi listener = clientListeners.get(client);
        controller.playerIsDoneCrafting(listener);
    }

    @Override
    public void checkStorage(VirtualViewRmi client) throws RemoteException, CargoManagementException {
        ClientListenerRmi listener = clientListeners.get(client);
        controller.checkStorage(listener);
    }

    @Override
    public void addGood(VirtualViewRmi client,int cargoIndex, int goodIndex, int rewardIndex) throws RemoteException, CargoManagementException {
        ClientListenerRmi listener = clientListeners.get(client);
        controller.addGood(listener,cargoIndex,goodIndex,rewardIndex);
    }

    @Override
    public void swapGoods(VirtualViewRmi client,int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2)  throws RemoteException, CargoManagementException {
        ClientListenerRmi listener = clientListeners.get(client);
        controller.swapGoods(listener,cargoIndex1,cargoIndex2,goodIndex1,goodIndex2);
    }

    @Override
    public void removeGood(VirtualViewRmi client,int cargoIndex, int goodIndex)  throws RemoteException, CargoManagementException {
        ClientListenerRmi listener = clientListeners.get(client);
        controller.removeGood(listener, cargoIndex, goodIndex);
    }

    @Override
    public void acceptCard(VirtualViewRmi client) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(client);
        controller.acceptCard(listener);
    }


    @Override
    public void addTile(VirtualViewRmi client, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException {
        ClientListenerRmi listener = clientListeners.get(client);
        controller.addTile(listener,xIndex,yIndex);
    }

    @Override
    public void chargeEngines(VirtualViewRmi client, int i) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(client);
        controller.charge(listener, i);
    }

    @Override
    public void putTileBack(VirtualViewRmi client) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(client);
        controller.putTileBack(listener);
    }
    @Override
    public void choosePlanets(VirtualViewRmi clientRmi, int i)throws RemoteException{
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.choosePlanets(listener,i);
    }

    @Override
    public void manageCard() throws RemoteException {
        controller.manageCard();
    }

    @Override
    public void handlePlanets(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.handlePlanets(listener);
    }

    @Override
    public void fromChargeToManage(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.fromChargeToManage(listener);
    }

    @Override
    public void addReserveSpot(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.addReserveSpot(listener);
    }

    @Override
    public void endCargoManagement(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.endCargoManagement(listener);
    }

    @Override
    public void chargeCannons(VirtualViewRmi clientRmi, ArrayList<Integer> chosenIndices) throws RemoteException{
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.chargeCannons(listener, chosenIndices);
    }

    @Override
    public void rotateClockwise(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.rotateClockwise(listener);
    }

    @Override
    public void removeAdjust(VirtualViewRmi clientRmi, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.removeAdjust(listener, xIndex, yIndex);
    }

    @Override
    public void selectShipPart(VirtualViewRmi clientRmi, int part) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.selectShipPart(listener, part);
    }

    @Override
    public void playerHit(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.playerHit(listener);
    }

    @Override
    public void playerProtected(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.playerProtected(listener);
    }

    @Override
    public boolean addAlienCabin(VirtualViewRmi clientRmi, int cabinId, String alienColor) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        return controller.addAlienCabin(listener,cabinId,alienColor);
    }

    @Override
    public void handleEndChooseAliens(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.handleEndChooseAliens(listener);
    }

    @Override
    public boolean removeFigure(VirtualViewRmi clientRmi, int cabinId) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        return controller.removeFigure(listener,cabinId);
    }

    public boolean removeFigureEpidemic(VirtualViewRmi clientRmi, int cabinId) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        return controller.removeFigureEpidemic(listener,cabinId);
    }

    public boolean isEpidemicDone(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        return controller.isEpidemicDone(listener);
    }

    @Override
    public void fromMvGoodstoBatteries(VirtualViewRmi clientRmi, int nBatteries) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.fromMvGoodstoBatteries(listener, nBatteries);
    }


    @Override
    public void surrender(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.surrender(listener);
    }

    @Override
    public void handleSurrenderEnded(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.handleSurrenderEnded(listener);
    }

    @Override
    public boolean removeBatteries(VirtualViewRmi clientRmi, int powerCenterId, int batteries) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        return controller.removeBatteries(listener,powerCenterId,batteries);
    }

    @Override
    public void endManagement(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.endManagement(listener);
    }

    @Override
    public void endCrewManagement(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.endCrewManagement(listener);
    }

    @Override
    public void endMVGoodsManagement(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.endMVGoodsManagement(listener);
    }

    @Override
    public boolean removeMVGood(VirtualViewRmi clientRmi, int cargoIndex, int goodIndex) throws RemoteException, CargoManagementException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        return controller.removeMVGood(listener,cargoIndex,goodIndex);
    }

    @Override
    public void showDecks(VirtualViewRmi clientRmi)throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.showDecks(listener);
    }

    @Override
    public boolean showCardsbyDeck(VirtualViewRmi clientRmi, int nDeck) throws RemoteException{
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        return controller.showCardsbyDeck(listener, nDeck);
    }

    @Override
    public void endShowCards(VirtualViewRmi clientRmi, int i) throws RemoteException {
        ClientListenerRmi listener = clientListeners.get(clientRmi);
        controller.endShowCards(listener, i);
    }

}

