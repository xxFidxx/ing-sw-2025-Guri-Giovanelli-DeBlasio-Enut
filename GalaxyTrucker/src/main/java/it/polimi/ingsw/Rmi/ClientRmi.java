package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.LobbyExceptions;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.data.DataContainer;
import it.polimi.ingsw.controller.network.data.LobbyNicks;
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
    private final VirtualServerRmi server;
    private volatile GameState currentState;
    private final LinkedBlockingQueue<Event> eventQueue;
    private final Scanner scan = new Scanner(System.in);
    private final Object StateLock = new Object();


    public ClientRmi(VirtualServerRmi server) throws RemoteException{
        super();
        this.server = server;
        eventQueue = new LinkedBlockingQueue<>();
        currentState = GameState.IDLE;
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
        Thread eventThread = new Thread(this::handleEvents);
        eventThread.start();
        runCli();
    }

    private void runCli() throws RemoteException {
        while (true) {
            if (scan.hasNextLine()) {
                String input = scan.nextLine().trim(); // trim to remove white spaces around the string
                synchronized (StateLock) {
                    handleInput(input);
                }
            }
        }
    }

    // sono da capire dove gestire gli errori di parsing in input
    private void handleInput(String input) throws RemoteException {
        switch(currentState){
            case IDLE ->{
                if (input.equals("0")) {
                    System.out.print("Enter lobby size [2-4]: ");
                    int size = Integer.parseInt(scan.nextLine());
                    try{
                        server.createLobby(this,size);
                    }catch (Exception e){
                        System.out.print("Lobby already exists, join it:\n");
                    }
                }else{
                    System.out.print("Not accepted input, please follow the instructions below:\n");
                }
            }
            case LOBBY_PHASE -> server.addNickname(this, input);
            case WAIT_LOBBY -> System.out.print("Waiting for other players to join...");
            case GAME_INIT -> System.out.print("--- GAME STARTED ---\n You will now craft your spaceship!");
            case ASSEMBLY -> {
                int tileIndex = Integer.parseInt(input);
            }
        }
        System.out.print("\n> ");
    }

    private void handleState() throws RemoteException {
        System.out.print("\n");
        switch(currentState){
            case IDLE -> System.out.print("Type 0 to create a lobby");
            case LOBBY_PHASE -> System.out.print("Lobby available\nEnter nickname: ");
            case WAIT_LOBBY -> System.out.print("Waiting for other players to join...");
            case GAME_INIT -> System.out.print("--- GAME STARTED ---\n You will now craft your spaceship!");
            case ASSEMBLY -> System.out.print("List of available tiles: ");
        }
        System.out.print("\n> ");
    }

    @Override
    public void showUpdate(Event event) throws RemoteException, InterruptedException {
        eventQueue.put(event);
    }

    private void handleEvents(){
        while (true) {
            try {
                Event event = eventQueue.take();
                    synchronized (StateLock) {
                        currentState = event.getState();
                        System.out.println("\n--- Game State Updated ---");
                        handleState();
                    }
                showData(event.getData());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("\n> Event thread interrupted");
                return;
            } catch (RemoteException e) {
                System.out.println("\n> You have been disconnected");
            }
        }
    }

    public void showData(DataContainer data){
        if(data == null)
            return;
        switch(data){
            case LobbyNicks ln ->  printLobbyNicks(ln.getNicks());
            case PickableTiles pt -> printPickableTiles(pt.getTiles());
            default -> {}
        }
    }


    public void printPickableTiles(ArrayList<String> tiles){
        System.out.println("\n");
        for (int i= 0; i<tiles.size(); i++) {
            System.out.println((i+1) + ": [" + tiles.get(i) + "]");
        }

        System.out.print("Enter the index of the tile you want to pick:\n");
    }

    public void printLobbyNicks(ArrayList<String> nicks){
        System.out.println("\nLobby: ");
        for(String nick : nicks){
            System.out.printf("[%s] ",nick);
        }
        System.out.println("\n");
    }
}
