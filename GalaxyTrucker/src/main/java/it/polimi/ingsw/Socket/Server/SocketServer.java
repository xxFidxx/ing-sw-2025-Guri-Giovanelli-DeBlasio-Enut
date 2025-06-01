package it.polimi.ingsw.Socket.Server;
import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.Socket.Client.ClientHandler;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.data.ServerResponse;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.SpaceShipPlanceException;


import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


public class SocketServer implements VirtualServerSocket {
    private final int port;
    private final Controller controller;
    private final ExecutorService clientPool;
    final List<ObjectOutputStream > clients;
    final Map<ObjectOutputStream , ClientListenerSocket> clientListeners;
    final Map<String,ObjectOutputStream > clientbyNickname;
    final Map<ObjectOutputStream ,String> nicknamebyClient;


    // IT IS VERY IMPORTANT TO ALWAYS SEND A RESPONSE AFTER EVERY METHOD INVOKED BY THE CLIENT, EVEN IF THESE ARE VOIDS BECAUSE THEY NEED TO KNOW IF THERER WAS NO EXCEPTION!

    public SocketServer(int port) {
        this.port = port;
        this.controller = new Controller();
        this.clientPool = Executors.newCachedThreadPool();
        this.clientListeners = new ConcurrentHashMap<>();
        this.nicknamebyClient = new ConcurrentHashMap<>();
        this.clients = new ArrayList<>();
        this.clientbyNickname = new ConcurrentHashMap<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("SocketServer listening on port: " + port);
            while (true) {
                try{
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(" New client connected: " + clientSocket);
                    clientPool.execute(new ClientHandler(clientSocket, this));
                }catch(Exception e){
                    System.out.println(" New client disconnected: " + e.getMessage());
                }
            }
        }catch(Exception e){
            System.out.println("SocketServer exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 1234;
        new SocketServer(port).start();
    }

    public void addClient(ObjectOutputStream out) {
        synchronized (clients) {
            clients.add(out);
        }
        ClientListenerSocket listener = new ClientListenerSocket(this,out);
        clientListeners.put(out, listener);
        controller.addEventListener(listener);
        System.out.println("Client connected");
    }

    public void notifyClient(ObjectOutputStream out, Event event) {
        try {
            out.writeObject(event);
            out.flush();
        } catch (IOException e) {
            System.out.println("Client disconnected: " + nicknamebyClient.get(out));
            removeClient(out);
        }
    }

    public void removeClient(ObjectOutputStream out) {
        try {
            String nickname = nicknamebyClient.get(out);
            if (nickname != null) {
                clientbyNickname.remove(nickname);
            }

            ClientListenerSocket listener = clientListeners.remove(out);
            if (listener != null) {
                synchronized (controller) {
                    controller.removeEventListener(listener);
                }
            }
            out.close();

            System.out.println(" Client removed: " + nickname);
        } catch (IOException e) {
            System.out.println("Error while closing client stream: " + e.getMessage());
        }
    }



    public void addNickname(ObjectOutputStream client, String nickname) throws RemoteException, LobbyExceptions {
        ClientListenerSocket listener = clientListeners.get(client);
        synchronized(controller){
            controller.addNickname(listener,nickname);
        }
        System.out.println("Nickname added\n");
        clientbyNickname.put(nickname, client);
        nicknamebyClient.put(client,nickname);
    }


    public void createLobby(int number) throws RemoteException, LobbyExceptions {
        synchronized(controller){
            controller.createLobby(number);
        }
        System.out.println("Lobby created\n");
    }

    public void pickTile(ObjectOutputStream client, int id) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.pickTile(listener, id);
    }

    public void drawCard() throws RemoteException {
        controller.drawCard();
    }


    public void endCrafting(ObjectOutputStream client) throws Exception {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.playerIsDoneCrafting(listener);
    }


    public void checkStorage(ObjectOutputStream client) throws RemoteException, CargoManagementException {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.checkStorage(listener);
    }

    public void checkStorageOk(ObjectOutputStream client) throws RemoteException, CargoManagementException {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.checkStorageOk(listener);
    }




    public void addGood(ObjectOutputStream client,int cargoIndex, int goodIndex, int rewardIndex) {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.addGood(listener,cargoIndex,goodIndex,rewardIndex);
    }


    public void swapGoods(ObjectOutputStream client,int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.swapGoods(listener,cargoIndex1,cargoIndex2,goodIndex1,goodIndex2);
    }


    public void removeGood(ObjectOutputStream client,int cargoIndex, int goodIndex) {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.removeGood(listener, cargoIndex, goodIndex);
    }


    public void acceptCard(ObjectOutputStream client) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.acceptCard(listener);
    }


    public void addTile(ObjectOutputStream client, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.addTile(listener,xIndex,yIndex);
    }


    public void charge(ObjectOutputStream client, int i) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.charge(listener, i);
    }


    public void putTileBack(ObjectOutputStream client) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(client);
        controller.putTileBack(listener);
    }


    public void choosePlanets(ObjectOutputStream clientRmi, int i)throws RemoteException{
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.choosePlanets(listener,i);
    }


    public void manageCard() throws RemoteException {
        controller.manageCard();
    }


    public void addReserveSpot(ObjectOutputStream clientRmi) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.addReserveSpot(listener);
    }


    public void endCargoManagement(ObjectOutputStream clientRmi) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.endCargoManagement(listener);
    }



    public void chargeCannons(ObjectOutputStream clientRmi, ArrayList<Integer> chosenIndices) throws RemoteException{
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.chargeCannons(listener, chosenIndices);
    }

    public void rotateClockwise(ObjectOutputStream clientRmi) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.rotateClockwise(listener);
    }


    public void removeAdjust(ObjectOutputStream clientRmi, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.removeAdjust(listener, xIndex, yIndex);
    }


    public void selectShipPart(ObjectOutputStream clientRmi, int part) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.selectShipPart(listener, part);
    }


    public void playerHit(ObjectOutputStream clientRmi) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.playerHit(listener);
    }


    public void playerProtected(ObjectOutputStream clientRmi) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.playerProtected(listener);
    }


    public boolean addAlienCabin(ObjectOutputStream clientRmi, int cabinId, String alienColor) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        return controller.addAlienCabin(listener,cabinId,alienColor);
    }


    public void handleEndChooseAliens(ObjectOutputStream clientRmi) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.handleEndChooseAliens(listener);
    }


    public boolean removeFigure(ObjectOutputStream clientRmi, int cabinId, String figure) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        return controller.removeFigure(listener,cabinId,figure);
    }


    public void surrender(ObjectOutputStream clientRmi) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.surrender(listener);
    }


    public void handleSurrenderEnded(ObjectOutputStream clientRmi) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.handleSurrenderEnded(listener);
    }

    public boolean removeBatteries(ObjectOutputStream clientRmi, int powerCenterId, int batteries) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        return controller.removeBatteries(listener,powerCenterId,batteries);
    }


    public void endManagement(ObjectOutputStream clientRmi) throws RemoteException {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        controller.endManagement(listener);
    }


    public boolean removeMVGood(ObjectOutputStream clientRmi, int cargoIndex, int goodIndex) {
        ClientListenerSocket listener = clientListeners.get(clientRmi);
        return controller.removeMVGood(listener,cargoIndex,goodIndex);
    }


    @Override
    public void handleCommand(String command, Object[] parameters, ObjectOutputStream out) {
        try {
            boolean result = true;

            switch (command) {
                // Metodi boolean
                case "addAlienCabin":
                    result = addAlienCabin(out, (int)parameters[0], (String)parameters[1]);
                    break;
                case "removeFigure":
                    result = removeFigure(out, (int)parameters[0], (String)parameters[1]);
                    break;
                case "removeBatteries":
                    result = removeBatteries(out, (int)parameters[0], (int)parameters[1]);
                    break;
                case "removeMVGood":
                    result = removeMVGood(out, (int)parameters[0], (int)parameters[1]);
                    break;

                // Metodi void (considerati sempre come success) a meno che ci sia un errore
                case "pickTile":
                    pickTile(out, (int)parameters[0]);
                    break;
                case "drawCard":
                    drawCard();
                    break;
                case "endCrafting":
                    endCrafting(out);
                    break;
                case "checkStorage":
                    checkStorage(out);
                    break;
                case "checkStorageOk":
                    checkStorageOk(out);
                    break;
                case "addGood":
                    addGood(out, (int)parameters[0], (int)parameters[1], (int)parameters[2]);
                    break;
                case "swapGoods":
                    swapGoods(out, (int)parameters[0], (int)parameters[1], (int)parameters[2], (int)parameters[3]);
                    break;
                case "removeGood":
                    removeGood(out, (int)parameters[0], (int)parameters[1]);
                    break;
                case "acceptCard":
                    acceptCard(out);
                    break;
                case "addTile":
                    addTile(out, (int)parameters[0], (int)parameters[1]);
                    break;
                case "charge":
                    charge(out, (int)parameters[0]);
                    break;
                case "putTileBack":
                    putTileBack(out);
                    break;
                case "choosePlanets":
                    choosePlanets(out, (int)parameters[0]);
                    break;
                case "manageCard":
                    manageCard();
                    break;
                case "addReserveSpot":
                    addReserveSpot(out);
                    break;
                case "endCargoManagement":
                    endCargoManagement(out);
                    break;
                case "chargeCannons":
                    chargeCannons(out, (ArrayList<Integer>)parameters[0]);
                    break;
                case "rotateClockwise":
                    rotateClockwise(out);
                    break;
                case "removeAdjust":
                    removeAdjust(out, (int)parameters[0], (int)parameters[1]);
                    break;
                case "selectShipPart":
                    selectShipPart(out, (int)parameters[0]);
                    break;
                case "playerHit":
                    playerHit(out);
                    break;
                case "playerProtected":
                    playerProtected(out);
                    break;
                case "handleEndChooseAliens":
                    handleEndChooseAliens(out);
                    break;
                case "surrender":
                    surrender(out);
                    break;
                case "handleSurrenderEnded":
                    handleSurrenderEnded(out);
                    break;
                case "endManagement":
                    endManagement(out);
                    break;
                case "createLobby":
                    createLobby((int)parameters[0]);
                    break;
                case "addNickname":
                    addNickname(out, (String)parameters[0]);
                    break;

                default:
                    System.out.println("Comando non riconosciuto: '" + command + "' (lunghezza: " + command.length() + ")"); // Debug
                    notifyClient(out, new Event(GameState.SERVER_RESPONSE,
                            new ServerResponse(false, true, "Comando non riconosciuto: " + command)));
                    return;
            }

            notifyClient(out, new Event(GameState.SERVER_RESPONSE,
                    new ServerResponse(result, false, null)));

        } catch (RemoteException e) {
            handleException(out, command, "Errore di comunicazione", e);
        } catch (LobbyExceptions e) {
            handleException(out, command, "Errore lobby", e);
        } catch (CargoManagementException e) {
            handleException(out, command, "Errore cargo", e);
        } catch (SpaceShipPlanceException e) {
            handleException(out, command, "Errore spaceship", e);
        } catch (Exception e) {
            handleException(out, command, "Errore generico del server", e);
        }
    }

    private void handleException(ObjectOutputStream out, String command, String context, Exception e) {
        String message = context + " per il comando " + command + ": " + e.getMessage();
        System.out.println(message);
        notifyClient(out, new Event(GameState.SERVER_RESPONSE,new ServerResponse(false, true, message)));
    }
}


