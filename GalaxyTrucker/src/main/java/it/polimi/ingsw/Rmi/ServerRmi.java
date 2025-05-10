package it.polimi.ingsw.Rmi;


import it.polimi.ingsw.controller.ClientListener;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.SpaceShipPlanceException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Interfaccia che definisce i metodi del server che devono essere resi accessibili ai client per poter interagire con controller
public class ServerRmi extends UnicastRemoteObject implements VirtualServerRmi {
    final Controller controller;
    final List<VirtualViewRmi> clients;
    final Map<VirtualViewRmi, ClientListener> clientListeners;
    final Map<String,VirtualViewRmi> clientbyNickname;
    final Map<VirtualViewRmi,String> nicknamebyClient;

    // per fare più partite in contemporanea dovrei fare una mappatura Map<Controller, String> dove a ogni controller leghi una lobby o un game
    // bisogna gestire bene cosa succede in caso di disconnessione durante le chiamate ai metodi
    // si può fare un eventcrafter invece di crare a mano gli eventi ogni volta che li mandi


    ServerRmi() throws RemoteException {
        super();
        this.clientListeners = new HashMap<>();
        this.nicknamebyClient = new HashMap<>();
        this.controller = new Controller();
        this.clients = new ArrayList<>();
        this.clientbyNickname = new HashMap<>();
    }

    public static void main(String[] args) throws RemoteException {
        final String serverName = "ServerRmi";

        VirtualServerRmi server = new ServerRmi();

        Registry registry = LocateRegistry.createRegistry(1234);

        registry.rebind(serverName, server);

        System.out.println("Server bound");
    }

    @Override
    public void connect(VirtualViewRmi client) throws RemoteException {
        synchronized (clients) {
            clients.add(client);
        }
        ClientListener clientListener = new ClientListener(client, this);
        clientListeners.put(client, clientListener);
        synchronized(controller){
            controller.addEventListener(clientListener);
        }
        System.out.println("Client connected");
    }


    public void addNickname(VirtualViewRmi client, String Nickname) throws RemoteException, LobbyExceptions {
        ClientListener clientListener = clientListeners.get(client);
        synchronized(controller){
            controller.addNickname(clientListener,Nickname);
        }
        System.out.println("Nickname added\n");
        clientbyNickname.put(Nickname, client);
        nicknamebyClient.put(client,Nickname);
    }

    public void createLobby(VirtualViewRmi client,int number) throws RemoteException, LobbyExceptions {
        synchronized(controller){
            controller.createLobby(number);
        }
        System.out.println("Lobby created\n");
    }


    public void removeClient(VirtualViewRmi client){
        synchronized(clients){
            clients.remove(client);
            //synchronized(controller){}
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

    public void pickTile(VirtualViewRmi client, int id) throws RemoteException {
        ClientListener listener = clientListeners.get(client);
        controller.pickTile(listener, id);
    }

    public void drawCard(VirtualViewRmi client) throws RemoteException {
        ClientListener listener = clientListeners.get(client);
        controller.drawCard(listener);
    }

    @Override
    public void endCrafting(VirtualViewRmi client) throws Exception {
        ClientListener listener = clientListeners.get(client);
        controller.playerIsDoneCrafting(listener);
    }

    @Override
    public void checkStorage(VirtualViewRmi client) throws CargoManagementException {
        ClientListener listener = clientListeners.get(client);
        controller.checkStorage(listener);
    }

    @Override
    public void addGood(VirtualViewRmi client,int cargoIndex, int goodIndex, int rewardIndex) {
        ClientListener listener = clientListeners.get(client);
        controller.addGood(listener,cargoIndex,goodIndex,rewardIndex);
    }

    @Override
    public void swapGoods(VirtualViewRmi client,int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) {
        ClientListener listener = clientListeners.get(client);
        controller.swapGoods(listener,cargoIndex1,cargoIndex2,goodIndex1,goodIndex2);
    }

    @Override
    public void removeGood(VirtualViewRmi client,int cargoIndex, int goodIndex) {
        ClientListener listener = clientListeners.get(client);
        controller.removeGood(listener, cargoIndex, goodIndex);
    }

    @Override
    public void acceptCard(VirtualViewRmi client) throws RemoteException {
        ClientListener listener = clientListeners.get(client);
        controller.acceptCard(listener);
    }

    @Override
    public void rejectCard() throws RemoteException{
        controller.rejectCard();
    }

//    @Override
//    public void printSpaceship(VirtualViewRmi client) {
//        ClientListener listener = clientListeners.get(client);
//        controller.printSpaceship(listener);
//    }


    @Override
    public void addTile(VirtualViewRmi client, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException {
        ClientListener listener = clientListeners.get(client);
        controller.addTile(listener,xIndex,yIndex);
    }

    @Override
    public void charge(VirtualViewRmi client, int i) throws RemoteException {
        ClientListener listener = clientListeners.get(client);
        controller.charge(listener, i);
    }

    @Override
    public void putTileBack(VirtualViewRmi client) throws RemoteException {
        ClientListener listener = clientListeners.get(client);
        controller.putTileBack(listener);
    }
}
