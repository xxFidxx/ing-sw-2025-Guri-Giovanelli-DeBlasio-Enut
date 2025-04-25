package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.data.LobbyNicks;
import it.polimi.ingsw.controller.network.data.PickableTiles;
import it.polimi.ingsw.controller.network.data.Tile;
import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.game.Game;

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
    final Map<String,VirtualViewRmi> clientbyNickname;
    GameState currentGameState = GameState.IDLE;
    private volatile boolean isLobbyCreated = false;
    final private Object isLobbyCreatedLock;


    // per fare più partite in contemporanea dovrei fare una mappatura Map<Controller, String> dove a ogni controller leghi una lobby o un game
    // bisogna gestire bene cosa succede in caso di disconnessione durante le chiamate ai metodi
    // si può fare un eventcrafter invece di crare a mano gli eventi ogni volta che li mandi


    ServerRmi() throws RemoteException {
        super();
        this.isLobbyCreatedLock = new Object();
        this.controller = new Controller();
        this.clients = new ArrayList<>();
        this.clientbyNickname = new HashMap<>();
        this.isLobbyCreated = false;
    }

    public static void main(String[] args) throws RemoteException {
        final String serverName = "ServerRmi";

        VirtualServerRmi server = new ServerRmi();

        Registry registry = LocateRegistry.createRegistry(1234);

        registry.rebind(serverName, server);

        System.out.println("Server bound");
    }

    @Override// bisogna gestire concorrenza
    public void connect(VirtualViewRmi client) throws RemoteException {
        synchronized (clients) {
            clients.add(client);
        }
        System.out.println("Client connected");

        synchronized (isLobbyCreatedLock) {
            if(isLobbyCreated)
                notifyClient(client,new Event(this, GameState.LOBBY_PHASE, null));
            else
                notifyClient(client,new Event(this, GameState.IDLE, null)); // vuol dire che sei tu che devi creare la lobby
        }

    }


    public void addNickname(VirtualViewRmi client, String Nickname) throws RemoteException, LobbyExceptions {
        ArrayList<String> nicks;
        boolean isLobbyFull;
        synchronized(controller){
            nicks = controller.addNickname(Nickname);
            isLobbyFull=controller.isLobbyFull();
        }
        System.out.println("Nickname added\n");
        clientbyNickname.put(Nickname, client);

        notifyClient(client,new Event(this, GameState.WAIT_LOBBY, new LobbyNicks(nicks)));
        if(isLobbyFull)
            game_init(nicks);
    }

    public void createLobby(VirtualViewRmi client,int number) throws RemoteException, LobbyExceptions {
        synchronized(controller){
            controller.createLobby(number);
        }
        System.out.println("Lobby created\n");

        synchronized (isLobbyCreatedLock) {
            isLobbyCreated = true;
        }

        // if lobby is already created, it throws exception to the caller and doesn't update anything
        currentGameState = GameState.LOBBY_PHASE;
        notifyClient(client,new Event(this, GameState.WAIT_LOBBY, null));
    }


    // ancora da sincronizzare con la lobby che deve rimuovere pure dal suo
    public void removeClient(VirtualViewRmi client){
        synchronized(clients){
            clients.remove(client);
            synchronized(controller){}
        }
        synchronized (clientbyNickname){
            clientbyNickname.values().removeIf(v -> v.equals(client));
        }
    }

    private void notifyClient(VirtualViewRmi client, Event event) {
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

    private void notifyAllClients(Event event) {
        List<VirtualViewRmi> clientsCopy;
        synchronized (clients) {
            clientsCopy = new ArrayList<>(clients);
        }

        for (VirtualViewRmi client : clientsCopy) {
            notifyClient(client, event);
        }
    }

    public void game_init(ArrayList<String> nicks) throws RemoteException{
        notifyAllClients(new Event(this, GameState.GAME_INIT,null));
        currentGameState = GameState.ASSEMBLY;
        controller.setGame(new Game(nicks));
        ArrayList<String> assemblingTiles = controller.getGame().getAssemblingTiles();
        notifyAllClients(new Event(this, GameState.ASSEMBLY, new PickableTiles(assemblingTiles)));
    }

    public void pickTile(VirtualViewRmi client, int index) throws RemoteException {
    }
}
