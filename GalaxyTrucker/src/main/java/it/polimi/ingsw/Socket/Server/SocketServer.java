package it.polimi.ingsw.Socket.Server;
import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.Socket.Client.ClientHandler;
import it.polimi.ingsw.controller.ClientListener;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.data.ServerResponse;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.SpaceShipPlanceException;


import java.io.*;
import java.net.*;
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
        try {
            String hotspotIp = "127.0.0.1";
            InetAddress bindAddr = InetAddress.getByName(hotspotIp);

            try (ServerSocket serverSocket = new ServerSocket(port, 50, bindAddr)) {
                System.out.println("Server listening on " + hotspotIp + ":" + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket);
                    clientPool.execute(new ClientHandler(clientSocket, this));
                }

            }
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
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



    public void addNickname(ObjectOutputStream client, String nickname) throws  LobbyExceptions {
        ClientListenerSocket listener = clientListeners.get(client);
        synchronized(controller){
            controller.addNickname(listener,nickname);
        }
        System.out.println("Nickname added\n");
        clientbyNickname.put(nickname, client);
        nicknamebyClient.put(client,nickname);
    }



    @Override
    public void handleCommand(String command, Object[] parameters, ObjectOutputStream out) {
        try {
            boolean result = true;

            switch (command) {
                // Metodi boolean
                case "addAlienCabin":
                    result = controller.addAlienCabin(clientListeners.get(out), (int) parameters[0], (String) parameters[1]);
                    break;

                case "removeFigure":
                    result = controller.removeFigure(clientListeners.get(out), (int) parameters[0]);
                    break;

                case "removeBatteries":
                    result = controller.removeBatteries(clientListeners.get(out), (int) parameters[0], (int) parameters[1]);
                    break;

                case "removeMVGood":
                    result = controller.removeMVGood(clientListeners.get(out), (int) parameters[0], (int) parameters[1]);
                    break;

                case "pickTile":
                    controller.pickTile(clientListeners.get(out), (int) parameters[0]);
                    break;

                case "drawCard":
                    controller.drawCard();
                    break;

                case "endCrafting":
                    controller.playerIsDoneCrafting(clientListeners.get(out));
                    break;

                case "checkStorage":
                    controller.checkStorage(clientListeners.get(out));
                    break;

                case "checkStorageOk":
                    controller.checkStorageOk(clientListeners.get(out));
                    break;

                case "addGood":
                    controller.addGood(clientListeners.get(out), (int) parameters[0], (int) parameters[1], (int) parameters[2]);
                    break;

                case "swapGoods":
                    controller.swapGoods(clientListeners.get(out), (int) parameters[0], (int) parameters[1], (int) parameters[2], (int) parameters[3]);
                    break;

                case "removeGood":
                    controller.removeGood(clientListeners.get(out), (int) parameters[0], (int) parameters[1]);
                    break;

                case "acceptCard":
                    controller.acceptCard(clientListeners.get(out));
                    break;

                case "addTile":
                    controller.addTile(clientListeners.get(out), (int) parameters[0], (int) parameters[1]);
                    break;

                case "chargeEngines":
                    controller.charge(clientListeners.get(out), (int) parameters[0]);
                    break;

                case "putTileBack":
                    controller.putTileBack(clientListeners.get(out));
                    break;

                case "choosePlanets":
                    controller.choosePlanets(clientListeners.get(out), (int) parameters[0]);
                    break;

                case "manageCard":
                    controller.manageCard();
                    break;

                case "addReserveSpot":
                    controller.addReserveSpot(clientListeners.get(out));
                    break;

                case "endCargoManagement":
                    controller.endCargoManagement(clientListeners.get(out));
                    break;

                case "chargeCannons":
                    controller.chargeCannons(clientListeners.get(out), (ArrayList<Integer>) parameters[0]);
                    break;

                case "rotateClockwise":
                    controller.rotateClockwise(clientListeners.get(out));
                    break;

                case "removeAdjust":
                    controller.removeAdjust(clientListeners.get(out), (int) parameters[0], (int) parameters[1]);
                    break;

                case "selectShipPart":
                    controller.selectShipPart(clientListeners.get(out), (int) parameters[0]);
                    break;

                case "playerHit":
                    controller.playerHit(clientListeners.get(out));
                    break;

                case "playerProtected":
                    controller.playerProtected(clientListeners.get(out));
                    break;

                case "handleEndChooseAliens":
                    controller.handleEndChooseAliens(clientListeners.get(out));
                    break;

                case "surrender":
                    controller.surrender(clientListeners.get(out));
                    break;

                case "handleSurrenderEnded":
                    controller.handleSurrenderEnded(clientListeners.get(out));
                    break;

                case "endManagement":
                    controller.endManagement(clientListeners.get(out));
                    break;

                case "createLobby":
                    synchronized (controller){
                        controller.createLobby((int) parameters[0]);
                    }
                    break;

                case "addNickname":
                    addNickname(out, (String) parameters[0]); // mantiene questo perché aggiorna anche mappe lato server
                    break;

                case "endMVGoodsManagement":
                    controller.endMVGoodsManagement(clientListeners.get(out));
                    break;

                case "fromChargeToManage":
                    controller.fromChargeToManage(clientListeners.get(out));
                    break;

                case "endShowCards":
                    controller.endShowCards(clientListeners.get(out), (int) parameters[0]);
                    break;

                case "showCardsbyDeck":
                    result = controller.showCardsbyDeck(clientListeners.get(out), (int) parameters[0]);
                    break;

                case "showDecks" :
                    controller.showDecks(clientListeners.get(out));
                    break;

                case "startTimer":
                    result = controller.startTimer();
                    break;

                case "removeFigureEpidemic":
                    result = controller.removeFigureEpidemic(clientListeners.get(out), (int) parameters[0]);
                    break;

                case "endCrewManagement":
                    controller.endCrewManagement(clientListeners.get(out));
                    break;

                case "handlePlanets":
                    controller.handlePlanets(clientListeners.get(out));
                    break;

                case "fromMvGoodstoBatteries":
                    controller.fromMvGoodstoBatteries(clientListeners.get(out), (int) parameters[0]);
                    break;

                default:
                    System.out.println("Comando non riconosciuto: '" + command + "' (lunghezza: " + command.length() + ")"); // Debug
                    notifyClient(out, new Event(GameState.SERVER_RESPONSE,
                            new ServerResponse(false, true, "Comando non riconosciuto: " + command)));
                    return;
            }

            notifyClient(out, new Event(GameState.SERVER_RESPONSE, new ServerResponse(result, false, null)));

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


