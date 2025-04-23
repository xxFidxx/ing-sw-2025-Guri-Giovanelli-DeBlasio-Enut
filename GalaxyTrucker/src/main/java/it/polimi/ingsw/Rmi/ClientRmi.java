package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.data.DataContainer;
import it.polimi.ingsw.controller.network.data.PickableTiles;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientRmi extends UnicastRemoteObject implements VirtualViewRmi{
    final VirtualServerRmi server;
    GameState currentState;
    private final LinkedBlockingQueue<Event> eventQueue;
    private final Scanner scan = new Scanner(System.in);

    public ClientRmi(VirtualServerRmi server) throws RemoteException{
        super();
        this.server = server;
        eventQueue = new LinkedBlockingQueue<>();
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
        new Thread(this::handleEvents).start();
        runCli();
    }

    private void runCli() throws RemoteException {
        while(true) {
            System.out.print("\n> ");
            if(scan.hasNextLine()){
                String input = scan.nextLine().trim();
                handleInput(input);
            }
        }
    }

    // sono da capire dove gestire gli errori di parsing in input
    private void handleInput(String input) throws RemoteException {
        switch(currentState){
            case LOBBY_PHASE ->{
                System.out.print("Type 0 to create a lobby or type 1 to join the lobby");
                if (input.equals("0")) {
                    System.out.print("Enter lobby size [2-4]: ");
                    int size = Integer.parseInt(scan.nextLine());
                    server.createLobby(size);
                }else if (input.equals("1")) {
                    System.out.print("Enter nickname: ");
                    String name = scan.nextLine();
                    server.addNickname(this, name);
                }
            }
            case ASSEMBLY -> {
                System.out.print("Enter the index of the tile you want to place: ");
                int tileIndex = Integer.parseInt(input);
            }
        }
    }

    @Override
    public void showUpdate(Event event) throws RemoteException, InterruptedException {
        eventQueue.put(event);
    }

    private void handleEvents() {
        while(true){
            try {
                Event event = eventQueue.take();
                currentState = event.getState();
                showData(event.getData());
                System.out.print("\n> ");
            }catch(InterruptedException e){
                return;
            }
        }
    }

    public void showData(DataContainer data){
        switch(data){
            case PickableTiles p -> printPickableTiles(p.getTiles());
            default -> {}
        }
    }


    public void printPickableTiles(ArrayList<String> tiles){
        System.out.println("\n List of tiles:\n");
        for (int i= 0; i<tiles.size(); i++) {
            System.out.println(i + ": [" + tiles.get(i) + "]\n");
        }
    }
}
