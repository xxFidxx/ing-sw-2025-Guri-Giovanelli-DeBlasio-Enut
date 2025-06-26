package it.polimi.ingsw.Rmi;


import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.ClientListenerRmi;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.SpaceShipPlanceException;
import it.polimi.ingsw.view.VirtualView;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


// Interfaccia che definisce i metodi del server che devono essere resi accessibili ai client per poter interagire con controller
public class ServerRmi extends UnicastRemoteObject implements VirtualServerRmi {
    final Controller controller;
    final List<VirtualViewRmi> clients;
    final List<VirtualViewRmi> realClients;
    final List<String> nicknames;
    final Map<VirtualViewRmi, ClientListenerRmi> clientListeners;
    final Map<VirtualViewRmi, ClientListenerRmi> realClientListeners;
    final Map<String,VirtualViewRmi> clientbyNickname;
    final Map<VirtualViewRmi,String> nicknamebyClient;
    final Map<String, Event> lastEventSent;
    final ArrayList<String> disconnectedPlayersNicks;
    private final Object lock = new Object();
    int lobbySize;
    boolean gameStarted;
    boolean gameEnded;

    // per fare più partite in contemporanea dovrei fare una mappatura Map<Controller, String> dove a ogni controller leghi una lobby o un game
    // bisogna gestire bene cosa succede in caso di disconnessione durante le chiamate ai metodi


    ServerRmi() throws RemoteException {
        super();
        this.clientListeners = new ConcurrentHashMap<>();
        this.realClientListeners = new ConcurrentHashMap<>();
        this.nicknames = new CopyOnWriteArrayList<>();
        this.nicknamebyClient = new ConcurrentHashMap<>();
        this.controller = new Controller();
        this.clients = new ArrayList<>();
        this.realClients = new CopyOnWriteArrayList<>();
        this.clientbyNickname = new ConcurrentHashMap<>();
        this.lastEventSent = new ConcurrentHashMap<>();
        this.disconnectedPlayersNicks = new ArrayList<>();
        this.lobbySize = 0;
        this.gameEnded = false;
        this.gameStarted = false;

        checkPings();
    }

    public static void main(String[] args) throws RemoteException {
        final String serverName = "ServerRmi";

        try (Scanner scanner = new Scanner(System.in)){

            System.out.print("Enter host IP address (leave empty for localhost):");
            String hotspotIp = scanner.nextLine().trim();

            if (hotspotIp.isEmpty()) hotspotIp = "127.0.0.1";
            System.setProperty("java.rmi.server.hostname", hotspotIp);


            Registry registry = LocateRegistry.createRegistry(1234);
            VirtualServerRmi server = new ServerRmi();
            registry.rebind(serverName, server);

            System.out.println("Server running on: " + hotspotIp + ":1234");
        } catch (Exception e) {
            System.err.println("Server failed: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void checkPings(){
        new Thread(() -> {
            while (true) {
                for(VirtualViewRmi client : realClients){
                        try{
                            client.ping();
                        } catch (RemoteException e) {
                            System.out.println("Client " + client + " failed to ping");
                            try {
                                handleDisconnect(client);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

        private void handleDisconnect(VirtualViewRmi client) throws Exception {
            String disconnectedNick = nicknamebyClient.get(client);
            nicknamebyClient.remove(client);

            ClientListenerRmi listener = realClientListeners.get(client);
            if (listener != null) {
                controller.handleDisconnect(listener);
            }

            synchronized (lock) {
                clientbyNickname.remove(disconnectedNick);
                realClientListeners.remove(client);
                realClients.remove(client);
            }

            if(gameStarted)
                disconnectedPlayersNicks.add(disconnectedNick);

            checkPauseGame();
        }

    private void checkPauseGame() throws Exception {

        if(!gameStarted){
            return;
        }

        synchronized (realClients) {
            if (realClients.size() == 1) {
                VirtualView client = realClients.getFirst();
                client.showUpdate(new Event(GameState.PAUSED_GAME, null));


                new Thread(() -> {
                    synchronized (controller) {
                        controller.setPause(true);
                    }
                    controller.pause();
                }).start();
            }
        }
    }

    synchronized void handleReconnect(String nickname, VirtualViewRmi client) throws RemoteException, InterruptedException {

            synchronized (lock){
                clientbyNickname.put(nickname, client);
                disconnectedPlayersNicks.remove(nickname);
                realClientListeners.put(client, clientListeners.get(client));
                realClients.add(client);
                nicknamebyClient.put(client, nickname);
            }


            ClientListenerRmi listener = realClientListeners.get(client);

            if(listener != null){
                client.showUpdate(new Event(GameState.WAIT_RECONNECT, null));
            }

            synchronized (controller) {
                if(controller.getPause()){
                    controller.setPause(false);

                    for(String nick: nicknames){
                        if(!disconnectedPlayersNicks.contains(nick)){
                            VirtualViewRmi realClient = clientbyNickname.get(nick);
                            System.out.println("client " + nicknamebyClient.get(realClient));
                            Event last = lastEventSent.get(nick);
                            System.out.println("Last event is: " + (last == null ? "null" : last.getState()));
                            notifyClient(realClient,last);
                        }
                    }
                    controller.handleReconnectPause(listener, nickname);
                }
            }

            if(listener!=null){
                controller.handleReconnect(listener, nickname);
            }
        }



    public void notifyClient(VirtualViewRmi client, Event event) {
        try {
            client.showUpdate(event);
            if(realClients.contains(client)){
                String nickname = nicknamebyClient.get(client);
                System.out.println("Saving event for nickname: '" + nickname + "' with state: " + event.getState());
                lastEventSent.put(nickname, event);
            }
            if(event.getState() == GameState.END_GAME)
                gameEnded=true;
        } catch (RemoteException e) {
            System.out.println("Client disconnected: " + e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread Interrupt");
        }
    }

    @Override
    public void connect(VirtualViewRmi client) throws RemoteException {
        if(!gameEnded){
            synchronized (clients) {
                clients.add(client);
            }
            ClientListenerRmi clientListenerRmi = new ClientListenerRmi( client, this);
            clientListeners.put(client, clientListenerRmi);
            synchronized(controller){
                controller.addEventListener(clientListenerRmi);
            }
            System.out.println("Client connected");
        }
    }

    @Override
    public void addNickname(VirtualViewRmi client, String nickname) throws RemoteException, LobbyExceptions, InterruptedException {
        if(!gameEnded){
            if(!gameStarted){
                realClients.add(client);
                ClientListenerRmi clientListenerRmi = clientListeners.get(client);
                realClientListeners.put(client, clientListenerRmi);
                nicknames.add(nickname);
                clientbyNickname.put(nickname, client);
                nicknamebyClient.put(client,nickname);
                synchronized(controller){
                    gameStarted = controller.addNickname(clientListenerRmi,nickname);
                }
                System.out.println("Nickname added\n");
            }else{
                System.out.println("Nickname given: " + nickname);
                System.out.println("Nickname in use:");
                for(String str : disconnectedPlayersNicks){
                    System.out.println(str);
                }
                if(disconnectedPlayersNicks.contains(nickname)){
                    handleReconnect(nickname, client);
                }
                else
                    throw new LobbyExceptions("Sorry, your nickname doesn't match with any of disconnected players ones, you can't join");
            }
        }
    }


    @Override
    public void createLobby(int number) throws RemoteException, LobbyExceptions {
        synchronized(controller){
            controller.createLobby(number);
        }
        lobbySize = number;
        System.out.println("Lobby created\n");
    }

    public void pickTile(VirtualViewRmi client, int id) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(client);
        controller.pickTile(listener, id);
    }

    @Override
    public boolean startTimer() throws RemoteException {
       return controller.startTimer();
    }

    @Override
    public void endCrafting(VirtualViewRmi client) throws Exception {
        ClientListenerRmi listener = realClientListeners.get(client);
        controller.playerIsDoneCrafting(listener);
    }

    @Override
    public void checkStorage(VirtualViewRmi client) throws RemoteException, CargoManagementException {
        ClientListenerRmi listener = realClientListeners.get(client);
        controller.checkStorage(listener);
    }

    @Override
    public void addGood(VirtualViewRmi client,int cargoIndex, int goodIndex, int rewardIndex) throws RemoteException, CargoManagementException {
        ClientListenerRmi listener = realClientListeners.get(client);
        controller.addGood(listener,cargoIndex,goodIndex,rewardIndex);
    }

    @Override
    public void swapGoods(VirtualViewRmi client,int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2)  throws RemoteException, CargoManagementException {
        ClientListenerRmi listener = realClientListeners.get(client);
        controller.swapGoods(listener,cargoIndex1,cargoIndex2,goodIndex1,goodIndex2);
    }

    @Override
    public void removeGood(VirtualViewRmi client,int cargoIndex, int goodIndex)  throws RemoteException, CargoManagementException {
        ClientListenerRmi listener = realClientListeners.get(client);
        controller.removeGood(listener, cargoIndex, goodIndex);
    }

    @Override
    public void acceptCard(VirtualViewRmi client) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(client);
        controller.acceptCard(listener);
    }


    @Override
    public void addTile(VirtualViewRmi client, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException {
        ClientListenerRmi listener = realClientListeners.get(client);
        controller.addTile(listener,xIndex,yIndex);
    }

    @Override
    public void chargeEngines(VirtualViewRmi client, int i) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(client);
        controller.charge(listener, i);
    }

    @Override
    public void putTileBack(VirtualViewRmi client) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(client);
        controller.putTileBack(listener);
    }
    @Override
    public void choosePlanets(VirtualViewRmi clientRmi, int i)throws RemoteException{
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.choosePlanets(listener,i);
    }

    @Override
    public void manageCard() throws RemoteException {
        controller.manageCard();
    }

    @Override
    public void handlePlanets(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.handlePlanets(listener);
    }

    @Override
    public void fromChargeToManage(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.fromChargeToManage(listener);
    }

    @Override
    public void addReserveSpot(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.addReserveSpot(listener);
    }

    @Override
    public void endCargoManagement(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.endCargoManagement(listener);
    }

    @Override
    public void chargeCannons(VirtualViewRmi clientRmi, ArrayList<Integer> chosenIndices) throws RemoteException{
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.chargeCannons(listener, chosenIndices);
    }

    @Override
    public void rotateClockwise(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.rotateClockwise(listener);
    }

    @Override
    public void removeAdjust(VirtualViewRmi clientRmi, int xIndex, int yIndex) throws RemoteException, SpaceShipPlanceException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.removeAdjust(listener, xIndex, yIndex);
    }

    @Override
    public void selectShipPart(VirtualViewRmi clientRmi, int part) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.selectShipPart(listener, part);
    }

    @Override
    public void playerHit(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.playerHit(listener);
    }

    @Override
    public void playerProtected(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.playerProtected(listener);
    }

    @Override
    public boolean addAlienCabin(VirtualViewRmi clientRmi, int cabinId, String alienColor) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        return controller.addAlienCabin(listener,cabinId,alienColor);
    }

    @Override
    public void handleEndChooseAliens(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.handleEndChooseAliens(listener);
    }

    @Override
    public boolean removeFigure(VirtualViewRmi clientRmi, int cabinId) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        return controller.removeFigure(listener,cabinId);
    }

    public boolean removeFigureEpidemic(VirtualViewRmi clientRmi, int cabinId) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        return controller.removeFigureEpidemic(listener,cabinId);
    }

    public boolean isEpidemicDone(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        return controller.isEpidemicDone(listener);
    }

    @Override
    public void fromMvGoodstoBatteries(VirtualViewRmi clientRmi, int nBatteries) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.fromMvGoodstoBatteries(listener, nBatteries);
    }


    @Override
    public void surrender(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.surrender(listener);
    }

    @Override
    public void handleSurrenderEnded(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.handleSurrenderEnded(listener);
    }

    @Override
    public boolean removeBatteries(VirtualViewRmi clientRmi, int powerCenterId, int batteries) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        return controller.removeBatteries(listener,powerCenterId,batteries);
    }

    @Override
    public void endManagement(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.endManagement(listener);
    }

    @Override
    public void endCrewManagement(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.endCrewManagement(listener);
    }

    @Override
    public void endMVGoodsManagement(VirtualViewRmi clientRmi) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.endMVGoodsManagement(listener);
    }

    @Override
    public boolean removeMVGood(VirtualViewRmi clientRmi, int cargoIndex, int goodIndex) throws RemoteException, CargoManagementException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        return controller.removeMVGood(listener,cargoIndex,goodIndex);
    }

    @Override
    public void showDecks(VirtualViewRmi clientRmi)throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.showDecks(listener);
    }

    @Override
    public boolean showCardsbyDeck(VirtualViewRmi clientRmi, int nDeck) throws RemoteException{
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        return controller.showCardsbyDeck(listener, nDeck);
    }

    @Override
    public void endShowCards(VirtualViewRmi clientRmi, int i) throws RemoteException {
        ClientListenerRmi listener = realClientListeners.get(clientRmi);
        controller.endShowCards(listener, i);
    }

    public int[] guiBoardInfo() throws RemoteException {
        return controller.guiBoardInfo();
    }

    public void notifyLastClient(VirtualViewRmi client) {
        try {
            Event event = lastEventSent.get(nicknamebyClient.get(client));
            client.showUpdate(event);
            System.out.println("Saving event for nickname: '" + nicknamebyClient.get(client) + "' with state: " + event.getState());

        } catch (RemoteException e) {
            System.out.println("Client disconnected: " + e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread Interrupt");
        }
    }
}

