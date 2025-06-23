package it.polimi.ingsw.Rmi;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.ControllerExceptions;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.data.*;
import it.polimi.ingsw.gui.MainApp;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.Cabin;
import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.componentTiles.DoubleCannon;
import it.polimi.ingsw.model.componentTiles.PowerCenter;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.SpaceShipPlanceException;
import it.polimi.ingsw.model.resources.GoodsContainer;
import it.polimi.ingsw.model.resources.Planet;
import it.polimi.ingsw.model.resources.TileSymbols;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientRmi extends UnicastRemoteObject implements VirtualViewRmi {
    public final VirtualServerRmi server;
    private volatile GameState currentState;
    private final LinkedBlockingQueue<Event> eventQueue;
    private final Scanner scan = new Scanner(System.in);
    private final Object StateLock = new Object();
    private Event currentEvent;
    private MainApp mainApp;

    public ClientRmi(VirtualServerRmi server) throws RemoteException{
        super();
        this.server = server;
        this.currentEvent = null;
        eventQueue = new LinkedBlockingQueue<>();
        currentState = GameState.IDLE;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public static void main(String[] args) throws Exception {
        final String serverName = "ServerRmi";

        // qua c'è da metterci come primo argomento identificativo registro, visto che voglio testare sulla mia macchina
        // ora l'ip è quello della macchina locale: 127.0.0.1 indirizzo local host
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1234);
        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);

        new ClientRmi(server).run(0);
    }


    public void run(int mode) throws Exception {
        server.connect(this);

        Thread eventThread;

        if (mode == 0) {
            eventThread = new Thread(this::handleEvents);
            eventThread.start();
            runCli();
        }
        else {
            eventThread = new Thread(this::handleEventsGUI);
            eventThread.start();
        }
    }

        // DA METTERE TIMER DOPO CHE IL PRIMO HA FINITO DI ASSEMBLARE LA NAVE!!!!
    // !!!!
    // !!!!!
    ////!!!!!!!!!!
    private void runCli() throws Exception {
        while (true) {
            if (scan.hasNextLine()) {
                String input = scan.nextLine().trim(); // trim to remove white spaces around the string
                synchronized (StateLock) {
                    handleInput(input);
                }
            }
        }
    }

    // sono da capire dove gestire gli errori di parsing in ingresso
    private void handleInput(String input) throws Exception {
        switch (currentState) {
            case IDLE -> {
                if (input.equals("0")) {
                    boolean inputValid = false;
                    while(!inputValid){
                        try {
                            System.out.print("Enter lobby size [2-4]: ");
                            int size = Integer.parseInt(scan.nextLine());
                            try {
                                server.createLobby(size);
                            } catch (Exception e) {
                                System.out.print("Error " + e.getMessage() + "\n");
                            }
                            inputValid = true;

                        } catch (NumberFormatException e) {
                            System.out.println("Error " + e.getMessage() + " please type a number");
                        }
                    }
                } else {
                    System.out.print("Not accepted input, please try again:\n");
                }
            }
            case LOBBY_PHASE -> {
                try {
                    server.addNickname(this, input);
                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                }
            }
            case WAIT_LOBBY -> System.out.print("Waiting for other players to join...");
            case GAME_INIT -> System.out.print("--- GAME STARTED ---\n You will now craft your spaceship!");

            case ASSEMBLY -> {
                    if(input.equals("-1")){
                        try {
                            server.endCrafting(this);
                        } catch (Exception e) {
                            System.out.println("Error " + e.getMessage());
                        }
                    }else if(input.equals("-2")){
                        try {
                            server.showDecks(this);
                        }catch (Exception e) {
                            System.out.println("Error " + e.getMessage());
                        }
                    }else{
                        try {
                            int index = Integer.parseInt(input);
                            // da aggiungere il check sul limite delle cards normali, per ora siamo fermi a 56 cards
                            if(index <= 1001){
                                server.pickTile(this, Integer.parseInt(input));
                            }else{
                                System.out.println("Outbound index, please retry");
                                System.out.println("Enter the index of the tile you want to pick, type -1 to end crafting or -2 to watch decks:");
                            }
                        }catch (NumberFormatException e){
                            System.out.println("Invalid input, ensure to write only numbers and not letters or special chars \n");
                        } catch (Exception e){
                            System.out.print("Error " + e.getMessage() + "\n");
                        }
                }

            }
            case PICKED_TILE -> {
                switch (input) {
                    case "0" -> {
                        boolean inputValid = false;
                        while (!inputValid) {
                            System.out.println("Type -1 to exit crafting or Insert coordinates: x y (es. 1 2): ");
                            System.out.print("> ");
                            String inputLine = scan.nextLine();

                            try {
                                String[] parts = inputLine.split(" ");
                                if(Integer.parseInt(parts[0]) == -1){
                                    inputValid = true;
                                    server.endCrafting(this);
                                }
                                if (parts.length == 2) {
                                    int xIndex = Integer.parseInt(parts[0]);
                                    int yIndex = Integer.parseInt(parts[1]);

                                    try {
                                        server.addTile(this, xIndex, yIndex);
                                        inputValid = true;
                                    }catch(SpaceShipPlanceException e) {
                                        System.out.println(e.getMessage() + ",please try again with valid coordinates");
                                    }
                                }else
                                    System.out.println("Wrong input. You need 2 numbers divided by a space \n");
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input, ensure to write only numbers and not letters or special chars \n");
                            } catch (SpaceShipPlanceException e) {
                                System.out.println("SpaceShipPlanceException error " + e.getMessage());
                            } catch (RemoteException e) {
                            System.out.println("RemoteException error " + e.getMessage());
                            }


                        }
                    }
                    case "1" -> server.rotateClockwise(this);
                    case "2" -> server.putTileBack(this);
                    case "3" -> server.addReserveSpot(this);
                    default -> System.out.print("Not accepted input, please try again:\n");
                }
            }

            case PICK_RESERVED_CARD ->{
                switch (input) {
                    case "0" -> {
                        boolean inputValid = false;
                        while (!inputValid) {
                            System.out.println("Type -1 to exit crafting or Insert coordinates: x y (es. 1 2): ");
                            System.out.print("> ");
                            String inputLine = scan.nextLine();

                            try {
                                String[] parts = inputLine.split(" ");
                                if(Integer.parseInt(parts[0]) == -1){
                                    inputValid = true;
                                    server.endCrafting(this);
                                }
                                if (parts.length == 2) {
                                    int xIndex = Integer.parseInt(parts[0]);
                                    int yIndex = Integer.parseInt(parts[1]);

                                    try {
                                        server.addTile(this, xIndex, yIndex);
                                        inputValid = true;
                                    }catch(SpaceShipPlanceException e) {
                                        System.out.println(e.getMessage() + ",please try again with valid coordinates");
                                    }
                                }else
                                    System.out.println("Wrong input. You need 2 numbers divided by a space \n");
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input, ensure to write only numbers and not letters or special chars \n");
                            } catch (SpaceShipPlanceException e) {
                                System.out.println("SpaceShipPlanceException error " + e.getMessage());
                            } catch (RemoteException e) {
                                System.out.println("RemoteException error " + e.getMessage());
                            }
                        }
                    }
                    case "1" -> server.rotateClockwise(this);
                    case "2" -> server.addReserveSpot(this);
                    default -> System.out.print("Not accepted input, please try again:\n");
                }
            }

            case SHOW_DECKS -> {
                if(input.equals("0")){
                    boolean inputValid = false;
                    while (!inputValid) {
                        System.out.println("Please select the deck you want to watch or type -1 to go back");
                        System.out.print("> ");
                        input = scan.nextLine();
                            try {
                                int nDeck = Integer.parseInt(input);
                                if(nDeck == -1){
                                    server.endShowCards(this,-1);
                                    inputValid = true;
                                }else if(nDeck > 0 && nDeck < 4){
                                    if(!server.showCardsbyDeck(this, nDeck))
                                        System.out.println("Another player is looking at this deck, please retry");
                                    else
                                        inputValid = true;
                                }else
                                    System.out.println("Outbound deck index!");

                            } catch (Exception e) {
                                System.out.print("Error " + e.getMessage() + "\n");
                            }
                        }
                }else{
                    System.out.print("Not accepted input, please try again:\n");
                }
            }

            case SHOW_CARDS ->{
                if (input.equals("0")) {
                    DataContainer data = currentEvent.getData();
                    int nDeck = ((AdventureCardsData) data).getnDeck();
                    server.endShowCards(this, nDeck);
                } else {
                    System.out.print("Not accepted input, please try again:\n");
                }
            }

            case ADJUST_SHIP -> {
                if (input.equals("0")) {
                    boolean inputValid = false;
                    while (!inputValid) {
                        System.out.println("Type -1 to exit or insert coordinates of the tile you want to remove: x y (es. 1 2): ");
                        System.out.print("> ");
                        String inputLine = scan.nextLine();

                        try {
                            String[] parts = inputLine.split(" ");
                            if (Integer.parseInt(parts[0]) == -1) {
                                inputValid = true;
                                server.endCrafting(this);
                            }
                            if (parts.length == 2) {
                                int xIndex = Integer.parseInt(parts[0]);
                                int yIndex = Integer.parseInt(parts[1]);

                                try {
                                    server.removeAdjust(this, xIndex, yIndex);
                                    inputValid = true;
                                } catch (SpaceShipPlanceException e) {
                                    System.out.println(e.getMessage() + ",please try again with valid coordinates");
                                }
                            } else
                                System.out.println("Wrong input. You need 2 numbers divided by a space \n");
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input, ensure to write only numbers and not letters or special chars \n");
                        } catch (SpaceShipPlanceException e) {
                            System.out.println("SpaceShipPlanceException error " + e.getMessage());
                        } catch (RemoteException e) {
                            System.out.println("RemoteException error " + e.getMessage());
                        }
                    }
                } else {
                    System.out.print("Not accepted input, please try again:\n");
                }

            }case SELECT_SHIP -> {
                    boolean inputValid = false;
                    while(!inputValid){
                        try {
                            int part = Integer.parseInt(input);
                            try {
                                server.selectShipPart(this, part);
                                inputValid = true;
                            } catch (Exception e) {
                                System.out.print("Error " + e.getMessage() + "\n");
                            }
                        } catch (NumberFormatException e) {
                            System.out.print("Error " + e.getMessage() + " please type a number \n");
                        }
                    }
            }
            case SHOW_SHIP -> System.out.println("Not accepted input, please try again:");
            case ROBBED_TILE -> System.out.println("Someone faster picked your card! Please try again");
            case VOID_RESERVED_SPOT -> System.out.print("This reserve spot is empty!");
            case FULL_RESERVE_SPOT -> System.out.print("Your reserve spot is full!");
            case DRAW_CARD ->
                    System.out.println("If you are the leader you will have to choose what to do, else just wait the players ahead are done!");
            case WAIT_PLAYER -> System.out.println("Wait for the choice of the current player");

            case WAIT_PLAYER_LEADER -> {
                if(input.equals("1")){
                    if(server.startTimer())
                        System.out.println("Timer started");
                    else
                        System.out.println("First timer isn't done yet");
                }else
                    System.out.println("Wait for other players are done crafting or start the timer by pressing 1");
            }

            case TIMER_DONE -> System.out.println("TIMER IS DONE!");


            case CHOOSE_ALIEN -> {
                switch (input) {
                    case "0" -> server.handleEndChooseAliens(this);
                    case "1"-> {
                        boolean exit = false;
                        while (!exit) {
                                System.out.println("Type -1 to exit or insert the id of the cabin you want to choose and then the color( b for brown and p for purple) of the alien you want to place in (ex. 1 b ): ");
                                System.out.print("> ");
                                String line = scan.nextLine();
                                try {
                                    String[] parts = line.split(" ");
                                    if(Integer.parseInt(parts[0]) == -1){
                                        server.handleEndChooseAliens(this);
                                        exit = true;
                                    }
                                    if (parts.length == 2) {
                                        int cabinId = Integer.parseInt(parts[0]);
                                        String alienColor = parts[1];
                                        if(!Objects.equals(alienColor, "b") && !Objects.equals(alienColor, "p"))
                                            System.out.println("You type a different letter from b or p, please try again");
                                        try {
                                            if(server.addAlienCabin(this, cabinId, alienColor))
                                                System.out.println("Successfully exchanged in cabin " + cabinId +  " 2 astronauts for a " + alienColor + " alien");
                                            else
                                                System.out.println("Your provided cabin id '" + cabinId +  "' or your provided alien color '" + alienColor + "' were incorrect, please try again");
                                        } catch (RemoteException e) {
                                            System.out.println(e.getMessage());
                                        }
                                    } else
                                        System.out.println("Wrong input. You need a number and the letter b or v divided by a space \n");
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input, ensure to write only numbers in the right spot and not letters or special chars \n");
                                } catch (RemoteException e) {
                                    System.out.println("RemoteException error " + e.getMessage());
                                }
                                if(!exit)
                                    System.out.println("Invalid input, please try again");
                            }
                        }
                    default -> System.out.print("Not accepted input, please try again:\n");
                    }
                }

            case CREW_MANAGEMENT ->{
                if (input.equals("0")) {
                    DataContainer data = currentEvent.getData();
                    int lostCrew = ((CrewManagement) data).getLostCrew();
                    while (lostCrew != 0) {
                        System.out.println("Please write the cabinId you want to remove the crew member from");
                        System.out.println("You have to remove " + "crew: " + lostCrew );
                        System.out.print("> ");
                        String line = scan.nextLine();
                        boolean removed = false;
                        try {
                            String[] parts = line.split(" ");
                            if (parts.length == 1) {
                                int cabinId = Integer.parseInt(parts[0]);
                                        if (server.removeFigure(this, cabinId)){
                                            lostCrew--;
                                            removed = true;
                                        } else {
                                            System.out.println("You have to put a cabinId containing at least one crew member");
                                        }
                            } else {
                                System.out.println("Wrong input. You need to put a number\n");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input, ensure to write only a number");
                        } catch (RemoteException e) {
                            System.out.println("Error " + e.getMessage());
                        }
                        if (removed)
                            System.out.println("Successfully removed");
                    }
                    server.endCrewManagement(this);
                } else {
                    System.out.print("Not accepted input, please try again:\n");
                }
            }

            case EPIDEMIC_MANAGEMENT ->{
                if (input.equals("0")) {
                    while (!server.isEpidemicDone(this)) {
                        System.out.println("Please write the cabinId you want to remove the crew member from");
                        System.out.print("> ");
                        String line = scan.nextLine();
                        boolean removed = false;
                        try {
                            String[] parts = line.split(" ");
                            if (parts.length == 1) {
                                int cabinId = Integer.parseInt(parts[0]);
                                if (server.removeFigureEpidemic(this, cabinId)){
                                    removed = true;
                                } else {
                                    System.out.println("You have to put a cabinId containing at least one crew member");
                                }
                            } else {
                                System.out.println("Wrong input. You need to put a number\n");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input, ensure to write only a number");
                        } catch (RemoteException e) {
                            System.out.println("Error " + e.getMessage());
                        }
                        if (removed)
                            System.out.println("Successfully removed");
                    }
                    server.endCrewManagement(this);
                } else {
                    System.out.print("Not accepted input, please try again:\n");
                }
            }

            case BATTERIES_MANAGEMENT,REMOVE_EXTRA_BATTERIES ->{
                if (input.equals("0")) {
                    DataContainer data = currentEvent.getData();
                    int nBatteries = ((BatteriesManagement) data).getNBatteries();
                    while ((nBatteries > 0)) {
                        System.out.println("Ex input: 14 2 -> remove 2 batteries from powerCenter number 14");
                        System.out.println("You have to remove " + "Batteries: " + nBatteries);
                        System.out.print("> ");
                        String line = scan.nextLine();
                        boolean removed = false;
                        try {
                            String[] parts = line.split(" ");
                            if (parts.length == 2) {
                                int powerCenterId = Integer.parseInt(parts[0]);
                                int batteries = Integer.parseInt(parts[1]);
                                    if (batteries > 0 && batteries <= 3 ) {
                                        if (server.removeBatteries(this, powerCenterId, batteries)) {
                                            nBatteries-= batteries;
                                            removed = true;
                                        } else {
                                            System.out.println("You have to put a PowerCenter containing a battery");
                                        }
                                    } else {
                                        System.out.println("You have to choose 1 to 3 batteries to remove, please retry");
                                    }
                            } else {
                                System.out.println("Wrong input. You need to put a PowerCenterId and a nBatteries divided by a space\n");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input, ensure to write only numbers in the right spot and not letters or special chars");
                        } catch (RemoteException e) {
                            System.out.println("Error " + e.getMessage());
                        }
                        if (removed)
                            System.out.println("Successfully removed");
                    }
                    if(currentEvent.getState() == GameState.BATTERIES_MANAGEMENT)
                        server.endManagement(this);
                    else
                        server.endMVGoodsManagement(this);
                } else {
                    System.out.print("Not accepted input, please try again:\n");
                }
            }

            case CARGO_MANAGEMENT -> System.out.print("If you have at least 1 cargo holds block you will manage your goods, else you will just skip this phase\n");

            case CARGO_VIEW -> {
                switch (input) {
                    case "0" -> {
                        boolean inputValid = false;
                        while (!inputValid) {
                            System.out.println("Insert: cargoIndex goodIndex rewardIndex (es. 0 1 2): or -1 to end cargo management");
                            System.out.print("> ");
                            String inputLine = scan.nextLine();
                            if(inputLine.equals("-1")){
                                server.endCargoManagement(this);
                                System.out.print("Cargo management ended:\n");
                            }else{
                                try {
                                    String[] parts = inputLine.split(" ");
                                    if (parts.length == 3) {
                                        int cargoIndex = Integer.parseInt(parts[0]);
                                        int goodIndex = Integer.parseInt(parts[1]);
                                        int rewardIndex = Integer.parseInt(parts[2]);
                                        if(cargoIndex != 0){
                                            server.addGood(this, cargoIndex, goodIndex, rewardIndex);
                                            inputValid = true;
                                        }else{
                                            System.out.println("You can't put a good in reward cargo");
                                        }
                                    }else
                                        System.out.println("Wrong input. You need 3 numbers divided by a space \n");
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input, ensure to write only numbers and not letters or special chars \n");
                                }catch (CargoManagementException e){
                                    System.out.println("CargoManagementException " + e.getMessage());
                                }catch (Exception e) {
                                    System.out.println("Error " + e.getMessage());
                                }
                            }

                        }
                    }
                    case "1" -> {
                        boolean inputValid = false;
                        while(!inputValid){
                            try {
                                System.out.println("Insert: cargoIndex1, cargoIndex2, goodIndex1, goodIndex2 (es. 0 1 2 1): or -1 to end cargo management");
                                System.out.print("> ");
                                String inputLine = scan.nextLine();
                                if(inputLine.equals("-1")){
                                    server.endCargoManagement(this);
                                    System.out.print("Cargo management ended:\n");
                                }else {
                                    String[] parts = inputLine.split(" ");
                                    if (parts.length == 4) {

                                        int cargoIndex1 = Integer.parseInt(parts[0]);
                                        int cargoIndex2 = Integer.parseInt(parts[1]);
                                        int goodIndex1 = Integer.parseInt(parts[2]);
                                        int goodIndex2 = Integer.parseInt(parts[3]);

                                        server.swapGoods(this, cargoIndex1, cargoIndex2, goodIndex1, goodIndex2);
                                        inputValid = true;
                                    } else
                                        System.out.println("Wrong input. You need 4 numbers divided by a space\n");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input, ensure to write only numbers and not letters or special chars \n");
                            }catch (CargoManagementException e){
                                System.out.println("CargoManagementException " + e.getMessage());
                            }catch (Exception e) {
                                System.out.println("Error " + e.getMessage());
                            }
                        }
                    }
                    case "2" -> {
                        boolean inputValid = false;
                        while(!inputValid){
                            System.out.println("Insert: cargoIndex goodIndex (es. 0 1): or -1 to end cargo management");
                            System.out.print("> ");
                            String inputLine = scan.nextLine();
                            if(inputLine.equals("-1")){
                                server.endCargoManagement(this);
                                System.out.print("Cargo management ended:\n");
                            }else{
                                try {
                                    String[] parts = inputLine.split(" ");
                                    if (parts.length != 2) {
                                        System.out.println("Wrong input. You need 2 numbers divided by a space \n");
                                    }else{
                                        int cargoIndex = Integer.parseInt(parts[0]);
                                        int goodIndex = Integer.parseInt(parts[1]);
                                        if(cargoIndex != 0){
                                            server.removeGood(this, cargoIndex, goodIndex);
                                            inputValid = true;
                                        }else{
                                            System.out.println("You can't put a good in reward cargo");
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input, ensure to write only numbers and not letters or special chars \n");
                                }catch (CargoManagementException e){
                                    System.out.println("CargoManagementException " + e.getMessage());
                                }catch (Exception e) {
                                    System.out.println("Error " + e.getMessage());
                                }
                            }

                        }
                    }

                    case "3" -> {
                        server.endCargoManagement(this);
                        System.out.print("Cargo management ended:\n");
                    }
                    default -> System.out.print("Not accepted input, please try again:\n");
                }
            }
            case ASK_SHIELD, ASK_CANNON -> {
                switch (input) {
                    case "0" -> server.playerHit(this);
                    case "1" -> server.playerProtected(this);
                    default -> System.out.print("Not accepted input, please try again:\n");
                }
            }
            case CHOOSE_PLAYER -> {
                switch (input) {
                    case "0" -> server.acceptCard(this);
                    case "1" -> server.manageCard();
                    default -> System.out.print("Not accepted input, please try again:\n");
                }
            }

            case CHOOSE_ENGINE -> {
                switch (input) {
                    case "0" -> server.fromChargeToManage(this);
                    case "1"->{
                        boolean inputValid = false;
                        while (!inputValid) {
                            System.out.print("Insert the number of double engines to charge: ");
                            try {
                                int numDE = Integer.parseInt(scan.nextLine());
                                server.chargeEngines(this, numDE);
                                inputValid = true;
                            } catch (ControllerExceptions e) {
                                System.out.println(e.getMessage());
                            } catch (NumberFormatException e) {
                                System.out.print("Error " + e.getMessage() + " please type a number \n");
                            } catch (Exception e) {
                                System.out.println("Error " + e.getMessage());
                            }
                        }
                    }
                    default -> System.out.print("Not accepted input, please try again:\n");
                }
            }

            case CHOOSE_CANNON -> {
                switch (input) {
                    case "0" -> server.fromChargeToManage(this);
                    case "1"-> {
                        ArrayList<Integer> chosenIndices = new ArrayList<>();
                        boolean inputValid = false;
                        while (!inputValid) {
                            System.out.println("Insert the index of double cannons to charge: ");
                            System.out.print("> ");
                            String line = scan.nextLine();
                            String[] parts = line.trim().split(" ");
                            inputValid = true;
                            for (String part : parts) {
                                try {
                                    int index = Integer.parseInt(part);
                                    chosenIndices.add(index);
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid number: " + part);
                                    inputValid = false;
                                    break;
                                }
                            }
                            if(inputValid){
                                try{
                                    server.chargeCannons(this, chosenIndices);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                    }
                    default -> System.out.print("Not accepted input, please try again:\n");
                }
            }
            case CHOOSE_PLANETS -> {
                switch (input) {
                    case "0" -> server.handlePlanets(this);
                    case "1"->{
                        DataContainer data = currentEvent.getData();
                        ArrayList<Planet> planets = ((PlanetsBlock) data).getPlanets();
                        int size = planets.size();
                        boolean inputValid = false;
                        while (!inputValid) {
                            try {
                                System.out.print("Insert planet index (from 0 to " + (size - 1) + "): ");
                                int numP = Integer.parseInt(scan.nextLine());
                                    server.choosePlanets(this, numP);
                                    inputValid = true;

                            } catch (ControllerExceptions e) {
                                System.out.println(e.getMessage());
                            } catch (NumberFormatException e) {
                            System.out.print("Error " + e.getMessage() + " please type a number \n");
                            } catch (CargoManagementException e) {
                                System.out.println(e.getMessage());
                                inputValid = true;
                            }catch (Exception e) {
                                System.out.println("Error " + e.getMessage());
                            }
                        }
                        server.manageCard();
                    }
                    default ->{
                        System.out.println("Not accepted input, please try again");
                        System.out.print("> ");
                    }
                }
            }case ASK_SURRENDER ->{
                switch (input) {
                    case "-1" ->{
                        server.surrender(this);
                        System.out.println("You surrendered, you will now be in spectator mode");
                    }
                    case "0"-> server.handleSurrenderEnded(this);
                    default -> System.out.print("Not accepted input, please try again:\n");
                }
            }

            case REMOVE_MV_GOODS ->{
                if (input.equals("0")) {
                    DataContainer data = currentEvent.getData();
                    int nGoods = ((RemoveMostValuable) data).getNGoods();
                    int nBatteries = ((RemoveMostValuable) data).getBatteriesToRemove();
                    while ((nGoods > 0)) {
                            System.out.println("You must remove " + nGoods + " goods");
                            System.out.println("Insert: cargoIndex goodIndex (es. 0 1): ");
                            System.out.print("> ");
                            String inputLine = scan.nextLine();
                            boolean removed = false;
                            try {
                                String[] parts = inputLine.split(" ");
                                if (parts.length != 2) {
                                    System.out.println("Wrong input. You need 2 numbers divided by a space");
                                }else{
                                    int cargoIndex = Integer.parseInt(parts[0]);
                                    int goodIndex = Integer.parseInt(parts[1]);
                                    if(server.removeMVGood(this, cargoIndex, goodIndex)){
                                        nGoods--;
                                        removed = true;
                                    }else
                                        System.out.println("This was not one of the most valuable goods you have, please select one among them!");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input, ensure to write only numbers and not letters or special chars");
                            } catch (Exception e) {
                                System.out.println("Error " + e.getMessage());
                            }
                        if (removed)
                            System.out.println("Successfully removed");
                    }
                    server.fromMvGoodstoBatteries(this, nBatteries);
                } else {
                    System.out.print("Not accepted input, please try again:\n");
                }
            }

            case SKIPPED_CARD -> System.out.println("Skipped the card because you are alone");
            case DIED -> System.out.println("You are on spectator mode because you died");
        }
        System.out.print("\n> ");
    }

    private void handleStateGUI() throws RemoteException {
        System.out.print("\n");
        switch(currentState){
            case IDLE -> System.out.println("Type 0 to create a lobby");
            case LOBBY_PHASE -> mainApp.lobbyPhase();
            case WAIT_LOBBY -> System.out.println("Waiting for other players to join...");
            case GAME_INIT -> mainApp.gameInit();
            case ASSEMBLY -> System.out.println("List of available tiles: ");
            case CRAFTING_ENDED -> System.out.println("CRAFTING PHASE ENDED");
            case PICKED_TILE -> System.out.println("This is the tile you picked: press 0 to place it in you spaceship plance, 1 to reserve it, 2 to put it back, 3 to draw a card, 4 to end the crafting, 5 to rotate it clockwise\n");
            case ROBBED_TILE -> System.out.println("Someone faster picked your card! Please try again");
            case ADJUST_SHIP -> System.out.println("Type 0 to remove a tile");
            case SELECT_SHIP -> System.out.println("Type the number corresponding to ship part you want to keep");
            case SHOW_SHIP -> System.out.println("Here is your spaceship");
            case BYTILE_SHIP -> System.out.println("Here is your spaceship with ids of interested tiles");
            case CHOOSE_ALIEN -> System.out.println("Press 0 to exit exchange mode, press 1 to enter");
            case TURN_START -> System.out.println("Here is the flight plance");
            case DRAW_CARD -> System.out.println("This is the drawn card:");
            case FAILED_CARD -> System.out.println("You haven't met the requirements to activate this card:");
            case CARGO_MANAGEMENT -> {
                try{
                    server.checkStorage(this);
                } catch (CargoManagementException e){
                    System.out.println(e.getMessage());
                } catch (Exception e){
                    System.out.println("Error " + e.getMessage());
                }
            }
            case CREW_MANAGEMENT -> {
                System.out.println("Here are your cabins, you will have to choose which crew to remove from which cabin");
                System.out.println("Press 0 to continue");
            }
            case BATTERIES_MANAGEMENT -> {
                System.out.println("Here are your PowerCenter, you will have to choose which one to remove batteries");
                System.out.println("Press 0 to continue");
            }
            case REMOVE_MV_GOODS -> {
                System.out.println("Here are your goods, you will have to remove the most valuable ones");
                System.out.println("Press 0 to continue");
            }
            case CARGO_VIEW -> System.out.println("Choose what to do: press 0 to add a good from the reward, 1 to swap goods, 2 to delete a good, 3 to end Cargo Management");
            case CHOOSE_PLAYER -> System.out.println("Type 0 to activate the card, 1 to reject the card");
            case WAIT_PLAYER -> System.out.println("Wait for the choice of the current player");
            case LEAST_CREW -> System.out.print("You have the least crew");
            case LEAST_ENGINE -> System.out.println("You have the least engine strenght");
            case MOVE_PLAYER -> System.out.println("You have the least crew");
            case LOST_CREW -> System.out.println("You have the least engine strength");
            case END_CARD -> System.out.println("End card");
            case SHOW_PLAYER -> System.out.println("Now your updated attributes are:");
            case CHOOSE_BATTERY -> System.out.println("Type 0 to skip your turn or 1 to charge your double engines ");
            case CHOOSE_PLANETS -> System.out.println("Type 0 to skip your turn or 1 to land on one of the planets");
            case CHOOSE_CANNON -> System.out.println("Type 0 to not use double cannons or 1 to use them");
            case ASK_SHIELD -> System.out.println("Type 0 to not use your shield or 1 to use it");
            case ASK_CANNON -> System.out.println("Type 0 to not use your double cannon or 1 to use it");
            case ASK_SURRENDER -> System.out.println("Type -1 to surrender or 0 to continue the game");
            case NOT_MIN_EQUIP -> System.out.println("You are not the player with minimum equipment");
            case NOT_MIN_ENGINE -> System.out.println("You are not the player with minimum engine strength");
            case NOT_MIN_FIRE -> System.out.println("You are not the player with minimum fire strength");
            case ENEMY_LOST -> System.out.println("You have been defeated by the enemies");
            case ENEMY_WIN -> System.out.println("You defeated the enemies");
            case ENEMY_DRAW -> System.out.println("You have the same power of enemies");
            case NO_DOUBLE_CANNON -> {
                System.out.println("You don't have any double cannon");
                server.fromChargeToManage(this);
            }
            case DIED -> System.out.println("You are on spectator mode because you died");
            case END_GAME -> System.out.println("Game has ended, below are the stats:");
            case NO_EXPOSED_CONNECTORS -> System.out.println("You don't have exposed connectors");
            case NO_HIT -> System.out.println("You have not been hit");
            case SHOT_HIT -> System.out.println("The shot hit your spaceship!");
            case SINGLE_CANNON_PROTECTION -> System.out.println("You have been protected by a single cannon");
        }
        System.out.print("> ");
    }

    private void handleState() throws RemoteException {
        System.out.print("\n");
        switch(currentState){
            case IDLE -> System.out.println("Type 0 to create a lobby");
            case LOBBY_PHASE -> System.out.println("Lobby available\nEnter nickname: ");
            case WAIT_LOBBY -> System.out.println("Waiting for other players to join...");
            case GAME_INIT -> System.out.println("--- GAME STARTED ---\n You will now craft your spaceship!\n" + TileSymbols.symbolExplanation);
            case ASSEMBLY -> System.out.println("List of available tiles: ");
            case CRAFTING_ENDED -> System.out.println("CRAFTING PHASE ENDED");
            case SHOW_DECKS -> System.out.println("Here are the decks you can watch:\n[Deck 1] [Deck 2] [Deck 3]\nPlease type 0 to continue");
            case SHOW_CARDS -> System.out.println("Here are the cards contained in selected deck");
            case PICKED_TILE -> System.out.println("This is the tile you picked: press 0 to place it in you spaceship plance, 1 rotate it clockwise, 2 to put it back, 3 to reserve it");
            case PICK_RESERVED_CARD -> System.out.println("This is the tile you picked from the reserve spot: press 0 to place it in you spaceship plance, 1 rotate it clockwise, 2 to put it back");
            case ROBBED_TILE -> System.out.println("Someone faster picked your card! Please try again");
            case VOID_RESERVED_SPOT -> System.out.print("This reserve spot is empty!");
            case FULL_RESERVE_SPOT -> System.out.print("Your reserve spot is full!");
            case ADJUST_SHIP -> System.out.println("Type 0 to remove a tile, type 1 to force draw card phase");
            case SELECT_SHIP -> System.out.println("Type the number corresponding to ship part you want to keep");
            case SHOW_SHIP -> System.out.println("Here is your spaceship");
            case BYTILE_SHIP -> System.out.println("Here is your spaceship with ids of interested tiles");
            case CHOOSE_ALIEN -> System.out.println("Press 0 to exit exchange mode, press 1 to enter");
            case TURN_START -> System.out.println("Here is the flight plance");
            case DRAW_CARD -> System.out.println("This is the drawn card:");
            case FAILED_CARD -> System.out.println("You haven't met the requirements to activate this card:");
            case CARGO_MANAGEMENT -> {
                try{
                    server.checkStorage(this);
                } catch (CargoManagementException e){
                    System.out.println(e.getMessage());
                } catch (Exception e){
                    System.out.println("Error " + e.getMessage());
                }
            }
            case CREW_MANAGEMENT -> {
                System.out.println("Here are your cabins, you will have to choose which crew to remove from which cabin");
                System.out.println("Press 0 to continue");
            }

            case EPIDEMIC_MANAGEMENT -> {
                System.out.println("Here are your interconnected cabins with at least one crew member, you will have to remove a crew member from each of the shown cabins");
                System.out.println("Press 0 to continue");
            }

            case BATTERIES_MANAGEMENT,REMOVE_EXTRA_BATTERIES -> {
                System.out.println("Here are your PowerCenter, you will have to choose which one to remove batteries");
                System.out.println("Press 0 to continue");
            }

            case REMOVE_MV_GOODS -> {
                System.out.println("Here are your goods, you will have to remove the most valuable ones");
                System.out.println("Press 0 to continue");
            }
            case CARGO_VIEW -> System.out.println("Choose what to do: press 0 to add a good from the reward, 1 to swap goods, 2 to delete a good, 3 to end Cargo Management");
            case CHOOSE_PLAYER -> System.out.println("Type 0 to activate the card, 1 to reject the card");
            case WAIT_PLAYER -> System.out.println("Wait for the choice of the current player");
            case WAIT_PLAYER_LEADER -> System.out.println("Wait for other players are done crafting or start the timer by pressing 1");
            case TIMER_DONE -> System.out.println("TIMER IS DONE!");
            case LEAST_CREW -> System.out.println("You have the least crew");
            case LEAST_ENGINE -> System.out.println("You have the least engine strenght");
            case LEAST_FIRE -> System.out.println("You have the least fire strenght");
            // case MOVE_PLAYER -> System.out.println("You have the least crew");
            // case LOST_CREW -> System.out.println("You have the least engine strength");
            case END_CARD -> System.out.println("End card");
            case SHOW_PLAYER -> System.out.println("Now your updated attributes are:");
            case CHOOSE_ENGINE -> System.out.println("Type 0 to skip your turn or 1 to charge your double engines ");
            case CHOOSE_PLANETS -> System.out.println("Type 0 to skip your turn or 1 to land on one of the planets");
            case CHOOSE_CANNON -> System.out.println("Type 0 to skip your turn or 1 to charge your double cannons");
            case ASK_SHIELD -> System.out.println("Type 0 to not use your shield or 1 to use it");
            case ASK_CANNON -> System.out.println("Type 0 to not use your double cannon or 1 to use it");
            case ASK_SURRENDER -> System.out.println("Type -1 to surrender or 0 to continue the game");
            case NOT_MIN_EQUIP -> System.out.println("You are not the player with minimum equipment");
            case NOT_MIN_ENGINE -> System.out.println("You are not the player with minimum engine strength");
            case NOT_MIN_FIRE -> System.out.println("You are not the player with minimum fire strength");
            case ENEMY_LOST -> System.out.println("You have been defeated by the enemies");
            case ENEMY_WIN -> System.out.println("You defeated the enemies");
            case ENEMY_DRAW -> System.out.println("You have the same power of enemies");
            case NO_DOUBLE_CANNON -> {
                System.out.println("You don't have any double cannon");
                server.fromChargeToManage(this);
            }
            case NO_DOUBLE_ENGINE -> {
                System.out.println("You don't have any double engine");
                server.fromChargeToManage(this);
            }
            case DIED -> System.out.println("You are on spectator mode because you died");
            case SKIPPED_CARD -> System.out.println("Skipped the card because you are alone");
            case END_GAME -> System.out.println("Game has ended, below are the stats:");
            case NO_EXPOSED_CONNECTORS -> System.out.println("You don't have exposed connectors");
            case NO_HIT -> System.out.println("You have not been hit");
            case SHOT_HIT -> System.out.println("The shot hit your spaceship!");
            case SINGLE_CANNON_PROTECTION -> System.out.println("You have been protected by a single cannon");
            case SAME_EQUIP -> System.out.println("No one was penalized because there are at least two players with the same equipment");
            case SAME_FIRE -> System.out.println("No one was penalized because there are at least two players with the same fire strength");
            case SAME_ENGINE -> System.out.println("No one was penalized because there are at least two players with the same engine strength");
            default -> System.out.println();
        }
        System.out.print("> ");
    }

    @Override
    public void showUpdate(Event event) throws RemoteException, InterruptedException {
        eventQueue.put(event);
    }

    private void handleEvents(){
        while (true) {
            try {
                currentEvent = eventQueue.take();
                    synchronized (StateLock) {
                        currentState = currentEvent.getState();
                        System.out.println("\n--- Game State Updated ---");
                        handleState();
                    }
                showData(currentEvent.getData());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("\n> Event thread interrupted");
                return;
            } catch (RemoteException e) {
                System.out.println("\n> You have been disconnected");
            }
        }
    }

    private void handleEventsGUI(){
        while (true) {
            try {
                currentEvent = eventQueue.take();
                synchronized (StateLock) {
                    currentState = currentEvent.getState();
                    System.out.println("\n--- Game State Updated ---");
                    handleStateGUI();
                }
                showData(currentEvent.getData());
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
            case PickableTiles pt -> printPickableTiles(pt.getTilesId(), pt.getReservedTiles());
            case PickedTile ptl -> System.out.println(ptl.getDescription());
            case Card c -> System.out.println("Card: " + c.getName() + ", level: " + c.getLevel());
            case AdventureCardsData adC -> printCards(adC.getAdventureCards());
            case Cargos c -> printCargos(c.getCargos());
            case CrewManagement cM-> printConnectedCabin(cM.getCabins());
            case BoardView b -> System.out.println(Arrays.toString(b.getBoard()));
            case PlayerColor pc -> System.out.println("Your color is " + pc.getColor());
            case PlayerInfo pi -> System.out.println("Nickname: " + pi.getNickname() + ", Position: " + pi.getPosition() + ", Credits: " + pi.getCredits() + ", Astronauts: " + pi.getNumAstronauts() + ", Aliens: " + pi.getNumAliens() + "\n");
            case DataString ds -> System.out.println(ds.getText());
            case DoubleEngineNumber den -> System.out.println("You have " + den.getPower() + " engine strength and " + den.getNum() + " double engines");
            case DoubleCannonNumber den -> System.out.println("You have " + den.getNum() + " double cannons");
            case PlanetsBlock pb -> printPlanets(pb.getPlanets());
            case EnemyStrenght es -> System.out.println("Enemy has " + es.getEnemyStrenght() + " fire strength, " + "You have " + es.getPlayerStrenght() + " fire strength without double cannons \n" );
            case DoubleCannonList dcl -> printDoubleCannons(dcl.getDoubleCannons());
            case ListCabinAliens lca -> printCabinAliens(lca.getCabinAliens());
            case EpidemicManagement em -> printConnectedCabin(em.getCabins());
            case ForwardDays fd -> System.out.println("You move forward " + fd.getFd() + " positions");
            case LostDays ld -> System.out.println("You lose " + ld.getLd() + " flight days");
            case LostCrew lc -> System.out.println("You lose " + lc.getLc() + " crew members");
            case BatteriesManagement batteriesManagement -> printPowerCenters(batteriesManagement.getPowerCenters());
            case RemoveMostValuable removeMostValuable -> printCargosRemove(removeMostValuable.getCargos());
            case SmallCannonDirPos sdr -> System.out.println("You are about to be hit by a small cannon shot from " + sdr.getDirection() + " direction in " + sdr.getPosition() + " position");
            case BigMeteorDirPos bdr -> System.out.println("You are about to be hit by a big meteor shot from " + bdr.getDirection() + " direction in " + bdr.getPosition() + " position");
            case SmallMeteorDirPos sdr -> System.out.println("You are about to be hit by a small meteor shot from " + sdr.getDirection() + " direction in " + sdr.getPosition() + " position");
            case BigCannonDirPos bdr -> System.out.println("You are about to be hit by a big cannon shot from " + bdr.getDirection() + " direction in " + bdr.getPosition() + " position");
            default -> {}
        }
        System.out.print("> ");
    }

    private void printConnectedCabin(ArrayList<Cabin> cabins) {
        for(Cabin c : cabins){
            System.out.println(c);
        }
    }

    private void printCards(ArrayList<Card> adventureCards) {
        System.out.println();
        for(Card c: adventureCards){
            System.out.println("Card: " + c.getName() + ", level: " + c.getLevel());
        }
        System.out.println();
        System.out.println("Type 0 to return to pickable tiles");
    }


    public void printCargosRemove(ArrayList<GoodsContainer> cargos) {
        System.out.println("--------------------------------------------------");

        for (int i = 0; i < cargos.size(); i++) {
            GoodsContainer container = cargos.get(i);
            GoodsBlock[] blocks = container.getGoods();

                String header = String.format("%sCargo %d id %d:",
                        container.isSpecial() ? "Special " : "", i, container.getId());
                System.out.print(header);

            for (GoodsBlock block : blocks) {
                String blockValue = (block != null) ?
                        String.valueOf(block.getValue()) : " ";
                System.out.printf(" [%2s]", blockValue);
            }
            System.out.println();
        }
        System.out.println("--------------------------------------------------");
        System.out.println();
    }

    private void printPowerCenters(ArrayList<PowerCenter> powerCenters) {
        for(PowerCenter pc : powerCenters){
            System.out.println(pc);
        }
    }

    private void printCabinAliens(ArrayList<CabinAliens> cabinAliens) {
        for(CabinAliens cabinAlien : cabinAliens) {
            Cabin c = cabinAlien.getCabin();
            System.out.println(c);
            System.out.println("brown: " + cabinAlien.isBrown() + " purple: " + cabinAlien.isPurple());
        }
        System.out.print("> ");
    }

    private void printPlanets(ArrayList<Planet> planets) {
        System.out.print("Here are the planets you can choose from:\n");
        for (int i = 0; i < planets.size(); i++) {
            if(planets.get(i).isBusy()){
                System.out.println("Busy planet : " + i  );
            }else{
                System.out.println("Planet : " + i  );
                GoodsBlock[] blocks = planets.get(i).getReward();
                for (GoodsBlock block : blocks) {
                    System.out.printf(" [" + block.getValue() + "] ");
                }
                System.out.print("\n");}
        }
        System.out.print("\n");
    }

    private void printCargos(ArrayList<GoodsContainer> cargos) {
        System.out.println("Here are the cargos you can choose from: (cargo number 0 is the reward one)");
        System.out.println("--------------------------------------------------");

        for (int i = 0; i < cargos.size(); i++) {
            GoodsContainer container = cargos.get(i);
            GoodsBlock[] blocks = container.getGoods();

            String header;
            if(i==0){
                header = String.format("%sReward Cargo %d:",
                        container.isSpecial() ? "Special " : "", i);
            }else {
                header = String.format("%sCargo %d id %d:",
                        container.isSpecial() ? "Special " : "", i, container.getId());
            }
            System.out.print(header);
            for (GoodsBlock block : blocks) {
                String blockValue = (block != null) ?
                        String.valueOf(block.getValue()) : " ";
                System.out.printf(" [%2s]", blockValue);
            }

            System.out.println();
        }
        System.out.println("--------------------------------------------------");
        System.out.println();
    }

    private void printDoubleCannons(ArrayList<DoubleCannon> doubleCannons) {
        System.out.print("Here are the double cannons you can choose from:\n");
        for (int i = 0; i < doubleCannons.size(); i++) {
            float power = doubleCannons.get(i).getPower();
            System.out.println("DoubleCannon: " + i + ", power: " + power);
            System.out.print("\n");
        }
    }



    public void printPickableTiles(Integer[] tiles, ArrayList<PickedTile> reservedTiles){
        System.out.println();
        for (Integer tileId : tiles) {
            if (tileId != null)
                System.out.println("[" + "Tile" + tileId + "]");
        }

        int i = 1000;
        for(PickedTile tile : reservedTiles){
            System.out.println("[" + "ReservedTileId " + i + "]     " + tile.getDescription());
            i++;
        }

        System.out.println("Enter the index of the tile you want to pick, type -1 to end crafting or -2 to watch decks:");
    }

    public void printLobbyNicks(ArrayList<String> nicks){
        System.out.println("\nLobby: ");
        for(String nick : nicks){
            System.out.printf("[%s] ",nick);
        }
        System.out.println("\n");
    }

    public GameState getCurrentState() {
        return currentState;
    }
}
