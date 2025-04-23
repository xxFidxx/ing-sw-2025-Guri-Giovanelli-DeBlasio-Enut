package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.controller.network.Event;

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


    // per fare più partite in contemporanea dovrei fare una mappatura Map<Controller, String> dove a ogni controller leghi una lobby o un game
    // bisogna gestire bene cosa succede in caso di disconnessione durante le chiamate ai metodi


    ServerRmi() throws RemoteException {
        super();
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

    @Override// bisogna gestire concorrenza
    public void connect(VirtualViewRmi client) throws RemoteException {
        synchronized (clients) {
            clients.add(client);
        }
        System.out.println("Client connected");
    }


    public void addNickname(VirtualViewRmi client, String Nickname) throws RemoteException, LobbyExceptions {
        synchronized(controller){
            controller.addNickname(Nickname);
            // se il nome è gia presente, verrà lanciata exception e quindi non avverrà mai l'associazione client-nick
            synchronized (clientbyNickname) {
                clientbyNickname.put(Nickname, client);
            }
        }
    }

    public void createLobby(int number) throws RemoteException, LobbyExceptions {
        synchronized(controller){
            controller.createLobby(number);
        }
        currentGameState = GameState.LOBBY_PHASE;
    }

    public void removeClient(VirtualViewRmi client){
        synchronized(clients){
            clients.remove(client);
        }
        synchronized (clientbyNickname){
            clientbyNickname.values().removeIf(v -> v.equals(client));
        }
    }

    public void notifyClient(VirtualViewRmi client,Event event){
                try {
                    client.showUpdate(event);
                } catch (RemoteException e) {
                    System.out.println("This error occurred: " + e);
                }
        }

    public void notifyAllClients(Event event){
        synchronized (clients){
            for (VirtualViewRmi client : clients) {
                try {
                    client.showUpdate(event);
                } catch (RemoteException e) {
                    System.out.println("This error occurred: " + e);
                }
            }
        }
    }
}
