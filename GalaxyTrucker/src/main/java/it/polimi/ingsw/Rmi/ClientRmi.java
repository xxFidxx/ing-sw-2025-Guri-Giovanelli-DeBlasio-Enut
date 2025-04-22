package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.controller.LobbyExceptions;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientRmi extends UnicastRemoteObject implements VirtualViewRmi{
    final VirtualServerRmi server;

    public ClientRmi(VirtualServerRmi server) throws RemoteException{
        super();
        this.server = server;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "ServerRmi";

        // qua c'è da metterci come primo argomento identificativo registro , visto che voglio testare sulla mia macchina
        // ora l'ip è quello della macchina locale: 127.0.0.1 indirizzo local host
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1234);

        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);

        new ClientRmi(server).run();

    }

    private void run() throws RemoteException {
        server.connect(this);
        runCli();
    }

    private void runCli() throws RemoteException {
        Scanner scan = new Scanner(System.in);
        while(true){
            System.out.print("> ");
            int command = scan.nextInt();

            switch (command) {
                case 0:
                    System.out.print("\n> Select lobby size: [2 to 4 players] ");
                    int lobbySize = scan.nextInt();
                    try {
                        server.createLobby(lobbySize);
                    } catch (LobbyExceptions e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 1: {
                    System.out.print("\n> Type your nickname ");
                    String name = scan.next();
                    try {
                        server.addNickname(name);
                    } catch (LobbyExceptions e) {
                        System.out.println("\nErrore: " + e.getMessage());
                    }
                    break;
                }

                default: {
                    System.out.println("\nInput non ancora configurato / errato");
                    break;
                }
            }
        }
    }

    @Override
    public void showUpdate(Integer number) throws RemoteException {
        System.out.println(number);
    }
}
