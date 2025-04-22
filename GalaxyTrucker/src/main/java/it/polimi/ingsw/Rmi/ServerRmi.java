package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.LobbyExceptions;

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
    final Map<VirtualViewRmi, String> clientbyNickname;

    // per fare più partite in contemporanea dovrei fare una mappatura Map<Controller, String> dove a ogni controller leghi una lobby o un game

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


    public void addNickname(String Nickname) throws RemoteException, LobbyExceptions {
        synchronized(controller) {
            controller.addNickname(Nickname);
            notifyAllClients();
        }
    }

    public void createLobby(int number) throws RemoteException, LobbyExceptions {
        controller.createLobby(number);
    }

    public void notifyAllClients() {
        synchronized (clients){
            for (VirtualViewRmi client : clients) {
                try {
                    client.showUpdate(11);
                } catch (RemoteException e) {
                    System.out.println("This error occured: " + e);
                }
            }
        }
    }
}
