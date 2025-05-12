package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.EventListenerInterface;
import it.polimi.ingsw.controller.network.Lobby;
import it.polimi.ingsw.controller.network.data.*;
import it.polimi.ingsw.model.adventureCards.*;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.resources.Planet;
import it.polimi.ingsw.model.resources.TileSymbols;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.resources.GoodsContainer;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Controller implements EventListenerInterface {
    private Game game;
    private Lobby lobby;
    private GameState currentGameState = GameState.IDLE;
    // queue of messageEvents, because you don't want the client to wait the computational time
    private final BlockingQueue<Event> queue;
    private final List<ClientListener> listeners = new ArrayList<>();
    private final Object LobbyLock = new Object();
    private final Object GameLock = new Object();
    final Map<ClientListener, Player> playerbyListener = new HashMap<>();
    final Map<Player,ClientListener> listenerbyPlayer = new HashMap<>();
    final Map <ClientListener, Boolean> isDonecrafting  = new HashMap<>();
    private AdventureCard currentAdventureCard;
    private Player currentPlayer;
    private ArrayList<Player> players;

    public Controller() {
        this.game = null;
        this.queue = new LinkedBlockingQueue<>();
        this.lobby = null;
        this.currentAdventureCard = null;
        this.currentPlayer = null;
        this.players = null;
    }

    public void addEventListener(ClientListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        handleOnConnectState(listener);
    }

    public void removeEventListener(ClientListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void notifyAllListeners(Event event) {
        List<ClientListener> listenersCopy;
        synchronized (listeners) {
            listenersCopy = new ArrayList<>(listeners);
        }

        for (ClientListener listener : listenersCopy) {
            listener.onEvent(event);
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void onEvent(Event event) {
        queue.add(event);
    }

    public void createLobby(int numPlayers) {
        if(lobby !=null)
            throw new LobbyExceptions("Lobby is already set");

        if (numPlayers < 2 || numPlayers > 4)
            throw new LobbyExceptions("Number of players must be between 2 and 4");

        lobby = new Lobby(numPlayers);

        currentGameState = GameState.LOBBY_PHASE;
        Event event= eventCrafter(currentGameState,null);
        notifyAllListeners(event);
    }

    public void addNickname(ClientListener listener, String nickname) throws LobbyExceptions {
        if(lobby == null)
            throw new LobbyExceptions("Not existing lobby");


        lobby.setPlayersNicknames(nickname);
        Event event = eventCrafter(GameState.WAIT_LOBBY,null);
        listener.onEvent(event);

        if(lobby.isFull())
            gameInit();

    }

    public void addTile(ClientListener listener, int xIndex, int yIndex) throws SpaceShipPlanceException {
        Player player = playerbyListener.get(listener);
        ComponentTile tile =player.getHandTile();
        try {
            player.getSpaceshipPlance().placeTileComponents(tile,xIndex,yIndex);
        } finally {
            printSpaceship(listener);
            listener.onEvent(eventCrafter(GameState.ASSEMBLY, null));
        }
    }

    public void pickTile(ClientListener listener, int tileId) throws LobbyExceptions {
        Player player = playerbyListener.get(listener);
        ComponentTile tile;

        if (tileId >= 1000) {
            tile = game.pickTileReserveSpot(player, tileId - 1000);
        } else {
            tile = game.pickTile(player,tileId);
        }

        if(tile != null){
            String tileName = tiletoString(tile);
            ConnectorType[] connectors = tile.getConnectors();
            printSpaceship(listener);
            listener.onEvent(eventCrafter(GameState.PICKED_TILE, new PickedTile(tile.toString())));
        }
        else{
            listener.onEvent(eventCrafter(GameState.ROBBED_TILE, null));
            listener.onEvent(eventCrafter(GameState.ASSEMBLY, null));
        }
    }

    public void handleOnConnectState(ClientListener listener){
        listener.onEvent(eventCrafter(currentGameState, null));
    }

    public Event eventCrafter(GameState state, Object data){
        Event event;
        switch(state){
            case WAIT_LOBBY ->{
                ArrayList<String> nicks;
                synchronized(LobbyLock){
                    nicks = lobby.getPlayersNicknames();
                }
                event = new Event(this, state,  new LobbyNicks(nicks));
            }
            case ASSEMBLY ->{
                Integer[] assemblingTiles;
                synchronized(GameLock){
                    assemblingTiles = game.getAssemblingTilesId();
                }
                event = new Event(this, state, new PickableTiles(assemblingTiles));
            }

            case SHOW_SHIP -> {
                event = new Event(this, state, (DataString) data);
            }

            case PICKED_TILE -> {
                event = new Event(this, state, (PickedTile)data);
            }
            case DRAW_CARD -> {
                event = new Event(this, state, (Card) data);
            }

            case PLAYER_COLOR -> event = new Event(this, state, new PlayerColor((String)data));

            case TURN_START -> {
                event = new Event(this, state, new BoardView((String[])data));
            }
            case SHOW_PLAYER -> {
                event = new Event(this, state, (PlayerInfo) data);
            }
            case CHOOSE_BATTERY -> {
                event = new Event(this, state, (DoubleEngineNumber) data);
            }
            case CHOOSE_PLANETS -> {
                event = new Event(this, state,new PlanetsBlock((ArrayList<Planet>) data));
            }
            case SHOW_ENEMY -> {
                event = new Event(this, state, (EnemyStrenght) data);
            }
            case CHOOSE_CANNON -> {
                event = new Event(this, state, (DoubleCannonNumber) data);
            }
            default ->event = new Event(this, state, null); // in cases where you don't have to send data, you just send the current state
        }
        return event;
    }

    public void gameInit() {

        currentGameState = GameState.GAME_INIT;
        notifyAllListeners(eventCrafter(currentGameState,null));

        currentGameState = GameState.ASSEMBLY;

        ArrayList<String> nicks = lobby.getPlayersNicknames();
        game = new Game(nicks);
        ArrayList<Player> players= game.getPlayers();


        synchronized(playerbyListener) {
            for (int i = 0; i < players.size(); i++) {
                playerbyListener.put(listeners.get(i), players.get(i));
            }
        }

        synchronized(listenerbyPlayer) {
            for (int i = 0; i < listeners.size(); i++) {
                listenerbyPlayer.put(players.get(i), listeners.get(i));
            }
        }

        synchronized (isDonecrafting) {
            for (ClientListener l : listeners) {
                isDonecrafting.put(l, false);
            }
        }

        Event event = eventCrafter(currentGameState,null);
        notifyAllListeners(event);
    }


    public char[][] tileCrafter(ComponentTile tile){
        char[][] lines = new char[3][3];

        // angoli sempre uguali
        lines[0][0] = '┌';
        lines[0][2] = '┐';
        lines[2][0] = '└';
        lines[2][2] = '┘';

        // centro
        char center = TileSymbols.ASCII_TILE_SYMBOLS.get(tiletoString(tile));

        // connettori
        ConnectorType[] connectors = tile.getConnectors();
        lines[0][1] = connectorToChar(connectors[0]);
        lines[1][0] = connectorToChar(connectors[1]);
        lines[1][2] = connectorToChar(connectors[2]);
        lines[2][1] = connectorToChar(connectors[3]);

        // scudo
        if (tile instanceof ShieldGenerator) {
            boolean[] protection = ((ShieldGenerator) tile).getProtection();
            if (protection[0] && protection[1]) {
                lines[0][2] = 'S';
            }
            else if (protection[1] && protection[2]) {
                lines[2][2] = 'S';
            }
            else if (protection[2] && protection[3]) {
                lines[2][0] = 'S';
            }
            else {
                lines[0][0] = 'S';
            }
        }

        return lines;
    }

    private char connectorToChar(ConnectorType ct) {
        switch (ct){
            case UNIVERSAL -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("universal");
            }
            case SINGLE -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("single");
            }
            case DOUBLE -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("double");
            }
            case SMOOTH -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("smooth");
            }
            case CANNON -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("cannon");
            }
            case ENGINE -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("engine");
            }
            default -> {
                return '?';
            }
        }
    }

    private String tiletoString(ComponentTile tile){
        if (tile != null) {
            switch (tile) {
                case DoubleCannon dc -> {
                    return "DoubleCannon";
                }

                case Cannon c-> {
                    return "Cannon";
                }

                case DoubleEngine de -> {
                    return "DoubleEngine";
                }
                case Engine e -> {
                    return "Engine";
                }
                case Cabin cab -> {
                    return "Cabin";
                }
                case CargoHolds ch -> {
                    return "CargoHolds";
                }

                case ShieldGenerator sg -> {
                    return "ShieldGenerator";
                }

                case LifeSupportSystem lfs -> {
                    return "LifeSupportSystem";
                }

                case PowerCenter pc -> {
                    return "PowerCenter";
                }

                case StructuralModule sm -> {
                    return "StructuralModule";
                }

                default -> {
                    return "not Catched in tiletoString";
                }
            }
        }
        return null;
    }

    public void drawCard() {
        List<AdventureCard> cards = game.getFlightPlance().getDeck().getCards();
        currentAdventureCard = cards.getFirst();
        String cardName = currentAdventureCard.getName();
        int cardLevel = currentAdventureCard.getLevel();
        Card card = new Card(cardName, cardLevel);
        players = new ArrayList<>(game.getPlayers());
        // aggiorniamo liste della nave prima di attivare la carta
        for(Player player: players){
            player.getSpaceshipPlance().updateLists();
        }
        if (cardName != null) {
            notifyAllListeners(eventCrafter(GameState.DRAW_CARD, card));
            manageCard();
        }
        else
            notifyAllListeners(eventCrafter(GameState.END_GAME, null));
        cards.remove(currentAdventureCard);
    }

    public void manageCard(){
        switch(currentAdventureCard){
            case AbandonedShipCard asc -> {
                if (players.isEmpty()) {
                    notifyAllListeners(eventCrafter(GameState.END_CARD, null));
                    game.endTurn();
                    ClientListener cl = listeners.getLast();
                    drawCard();
                    return;
                }
                currentPlayer = players.getFirst();
                if (game.choosePlayer(currentAdventureCard, currentPlayer)) {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    handleWaitersPlayer(l);
                } else {
                    players.remove(currentPlayer);
                    manageCard();
                }
            }

            case AbandonedStationCard asc -> {
                if (players.isEmpty()) {
                    notifyAllListeners(eventCrafter(GameState.END_CARD, null));
                    game.endTurn();
                    return;
                }
                currentPlayer = players.getFirst();
                if (game.choosePlayer(currentAdventureCard, currentPlayer)) {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    handleWaitersPlayer(l);
                } else {
                    players.remove(currentPlayer);
                    manageCard();
                }
            }

            case OpenSpaceCard osc ->{
                if (players.isEmpty()) {
                    notifyAllListeners(eventCrafter(GameState.END_CARD, null));
                    game.endTurn();
                    for(ClientListener cl : listeners){
                        endCard(cl);
                    }
                    drawCard();
                    return;
                }
                currentPlayer = players.getFirst();
                if(currentPlayer.hasResponded()){
                    fromChargeToManage();
                }
                int numDE = 0;
                for(Engine e : currentPlayer.getSpaceshipPlance().getEngines()){
                    if(e instanceof DoubleEngine) {
                        numDE++;
                    }
                }
                if(numDE > 0) {
                    currentPlayer.setResponded(true);
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    handleWaitersBattery(l, numDE);
                } else {
                    fromChargeToManage();
                }
            }

            case SlaversCard sl -> {
                if (players.isEmpty() || ((SlaversCard) currentAdventureCard).getFightOutcome(currentPlayer)==1) {
                    notifyAllListeners(eventCrafter(GameState.END_CARD, null));
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    endCard(l);
                    return;
                }
                currentPlayer = players.getFirst();
                ClientListener l = listenerbyPlayer.get(currentPlayer);
                handleWaitersEnemy(l);
            }

            case PlanetsCard pc -> {
                if (players.isEmpty()) {
                    notifyAllListeners(eventCrafter(GameState.END_CARD, null));
                    return;
                }
                currentPlayer = players.getFirst();
                game.orderPlayers();
                currentPlayer = players.getFirst();
                PlanetsCard currentPlanetsCard = (PlanetsCard) currentAdventureCard;
                if (game.freePlanets(currentAdventureCard,currentPlanetsCard.getPlanets())) {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    handleWaitersPlanets(l);
                } else {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
                    players.remove(currentPlayer);
                    manageCard();
                }
            }

            default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
        }
    }



    public void activateAbandonedShipCard(ClientListener listener) throws LobbyExceptions {
        Player p = playerbyListener.get(listener);
        AbandonedShipCard currentAbandonedShipCard = (AbandonedShipCard) currentAdventureCard;
        currentAbandonedShipCard.setActivatedPlayer(p);
        currentAdventureCard.activate();
        notifyAllListeners(eventCrafter(GameState.END_CARD, null));
        endCard(listener);
        drawCard();
    }
    private void activateAbandonedStationCard(ClientListener listener) {
        Player p = playerbyListener.get(listener);
        AbandonedStationCard currentAbandonedStationCard = (AbandonedStationCard) currentAdventureCard;
        currentAbandonedStationCard.setActivatedPlayer(p);
        currentAdventureCard.activate();
        listener.onEvent(eventCrafter(GameState.CARGO_MANAGEMENT, null));
        //endCard(listener);
        //drawCard(listener);
    }




    public void handleWaitersPlayer(ClientListener listener){
        for(ClientListener l: listeners){
            if(l == listener) {
                l.onEvent(eventCrafter(GameState.CHOOSE_PLAYER, null));
                Player p = playerbyListener.get(l);
                p.setResponded(true);
            }
            else {
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
            }
        }
    }

    public void handleWaitersBattery(ClientListener listener, int num){
        for(ClientListener l: listeners){
            if(l == listener) {
                DoubleEngineNumber den = new DoubleEngineNumber(num);
                l.onEvent(eventCrafter(GameState.CHOOSE_BATTERY, den));
            }
            else {
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
            }
        }
    }

    public void handleWaitersEnemy(ClientListener listener){
        for(ClientListener l: listeners) {
            if (l == listener) {
                SlaversCard currentSlaversCard = (SlaversCard) currentAdventureCard;
                float playerFire = currentPlayer.getFireStrenght();
                EnemyStrenght es = new EnemyStrenght(currentSlaversCard.getCannonStrength(), playerFire);
                l.onEvent(eventCrafter(GameState.SHOW_ENEMY, es));
                ArrayList<DoubleCannon> doubleCannons = new ArrayList<>();
                for (Cannon c : currentPlayer.getSpaceshipPlance().getCannons()) {
                    if (c instanceof DoubleCannon) {
                        doubleCannons.add((DoubleCannon) c);
                    }
                }
                DoubleCannonList dcl = new DoubleCannonList(doubleCannons);
                l.onEvent(eventCrafter(GameState.CHOOSE_CANNON, dcl));
            } else {
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
            }
        }
    }

    private void handleWaitersPlanets(ClientListener listener) {
        for(ClientListener l: listeners){
            if(l == listener) {
                PlanetsCard currentPlanetsCard = (PlanetsCard) currentAdventureCard;
                l.onEvent(eventCrafter(GameState.CHOOSE_PLANETS,currentPlanetsCard.getPlanets()));
            }else
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
        }

    }

    public void fromChargeToManage(){
        AdventureCard currentCastedCard = currentAdventureCard;
        switch(currentCastedCard) {
            case OpenSpaceCard osc -> {
                ((OpenSpaceCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                currentAdventureCard.activate();
                players.remove(currentPlayer);
                manageCard();
            }
            case SlaversCard sc -> {
                ((SlaversCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                currentAdventureCard.activate();
                players.remove(currentPlayer);
                manageCard();
            }
            default -> throw new IllegalStateException("Unexpected value: " + currentCastedCard);
        }
    }

    public void playerIsDoneCrafting(ClientListener listener) throws Exception {
        if (isDonecrafting.get(listener))
            throw new Exception("The player was already done crafting!\n");

        int pos;
        synchronized (isDonecrafting) {
            isDonecrafting.replace(listener, true);
            synchronized (GameLock) {
                pos = isDonecrafting.keySet().size();

                for (Boolean done : isDonecrafting.values()) {
                    if (done) {
                        pos--;
                    }
                }
            }
        }

        Flightplance flightPlance = game.getFlightPlance();
        Player p = playerbyListener.get(listener);
        flightPlance.getPlaceholderByPlayer(p).setPosizione(pos);
        String playerColor = flightPlance.getPlaceholderByPlayer(p).getColor().name();
        listener.onEvent(eventCrafter(GameState.PLAYER_COLOR, playerColor));
        game.orderPlayers();
        players = game.getPlayers();
        synchronized (isDonecrafting) {
            if (!isDonecrafting.containsValue(false))
                handleCraftingEnded();
            else
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
        }
    }

    public void handleCraftingEnded(){

        notifyAllListeners(eventCrafter(GameState.CRAFTING_ENDED, null));
        String[] boardView = handleBoardView();
        notifyAllListeners(eventCrafter(GameState.TURN_START, boardView));
        drawCard();
    }

    private String[] handleBoardView() {

        Flightplance flightPlance = game.getFlightPlance();
        Placeholder[] placeHolders = flightPlance.getSpots();

        String[] boardView = new String[18];
        Arrays.fill(boardView, "[]");


        // 3) Li “sparo” nella board in base alla loro posizione
        for (Placeholder p : placeHolders) {
            int pos = (p.getPosizione() % 18);
            if (pos < 0) {
                throw new IllegalStateException(
                        "Placeholder in posizione negativa: " + pos);
            }
            // prendo solo la prima lettera di ogni enum
            boardView[pos] = ("[" +p.getColor().name().charAt(0) + "]");

        }
        return boardView;
    }

    public void checkStorage(ClientListener listener) throws CargoManagementException {
        Player player = playerbyListener.get(listener);
        if(player.getSpaceshipPlance().checkStorage()){
            ArrayList<CargoHolds> playerCargos = player.getSpaceshipPlance().getCargoHolds();
            GoodsBlock[] playerReward = player.getReward();
            // Creazione della lista di GoodsContainer
            ArrayList<GoodsContainer> goodsContainers = new ArrayList<>();

            goodsContainers.add(new GoodsContainer(playerReward, false));

            for (CargoHolds cargo : playerCargos) {
                GoodsBlock[] goods = cargo.getGoods();
                goodsContainers.add(new GoodsContainer(goods, cargo.isSpecial()));
            }

            player.getSpaceshipPlance().setGoodsContainers(goodsContainers);
            listener.onEvent(new Event(this,GameState.CARGO_VIEW,new Cargos(goodsContainers)));
        }else {
            // qua ci sarebbe da gestire se siamo in planets quindi devi aspettare altri oppure in un reward generico quindi lui gestisce e finisce il turno per tutti...
            listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
            throw new CargoManagementException("You got 0 storage space, you can't manage any good");
        }
    }

    public void addGood(ClientListener listener,int cargoIndex, int goodIndex, int rewardIndex) {
        Player player = playerbyListener.get(listener);
        game.addGood(player,cargoIndex,goodIndex,rewardIndex);
        checkStorage(listener);
    }

    public void swapGoods(ClientListener listener,int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) {
        Player player = playerbyListener.get(listener);
        game.swapGoods(player,cargoIndex1,cargoIndex2,goodIndex1,goodIndex2);
        checkStorage(listener);
    }

    public void removeGood(ClientListener listener, int cargoIndex, int goodIndex) {
        Player player = playerbyListener.get(listener);
        game.removeGood(player,cargoIndex,goodIndex);
        checkStorage(listener);
    }

    public void acceptCard(ClientListener listener) {
        switch (currentAdventureCard) {
            case AbandonedShipCard asc -> activateAbandonedShipCard(listener);
            case AbandonedStationCard asc -> activateAbandonedStationCard(listener);
            default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
        }
    }

    public void printSpaceshipAll() {
        for (ClientListener c: listeners) {
            Player player = playerbyListener.get(c);
            DataString ds = new DataString(player.getSpaceshipPlance().tileGridToString());
            c.onEvent(eventCrafter(GameState.SHOW_SHIP, ds));
        }
    }

    public void printSpaceship(ClientListener listener) {
            Player player = playerbyListener.get(listener);
            String complete_ship = player.getSpaceshipPlance().reserveSpotToString() + "\n" + player.getSpaceshipPlance().tileGridToString();
            DataString ds = new DataString(complete_ship);
            listener.onEvent(eventCrafter(GameState.SHOW_SHIP, ds));
    }

    public void endCard(ClientListener listener) {
        game.endTurn();
        Player player = playerbyListener.get(listener);
        ArrayList<Player> players = game.getPlayers();
        for(Player p: players){
            if(p == player){
                String nick = p.getNickname();
                int pos = p.getPlaceholder().getPosizione();
                int cred = p.getCredits();
                int astr = p.getNumAstronauts();
                int al = p.getNumAliens();
                PlayerInfo pi = new PlayerInfo(nick, pos, cred, astr, al);
                listener.onEvent(eventCrafter(GameState.SHOW_PLAYER, pi));
            }
        }
    }

    public void charge(ClientListener listener, int i) throws ControllerExceptions {
        Player player = playerbyListener.get(listener);
        ArrayList<Engine> engines = player.getSpaceshipPlance().getEngines();
        ArrayList<DoubleEngine> doubleEngines = new ArrayList<>();
        for(Engine e: engines){
            if(e instanceof DoubleEngine) {
                doubleEngines.add((DoubleEngine) e);
            }
        }
        if(i < 0 || i > doubleEngines.size()) {
            throw new ControllerExceptions("You selected a wrong double engines number");
        }
        else {
            for(int j=0; j<i; j++){
                doubleEngines.get(j).setCharged(true);
            }
            fromChargeToManage();
        }
    }

    public void chargeCannons(ClientListener listener, ArrayList<Integer> chosenIndices) throws ControllerExceptions{
        Player player = playerbyListener.get(listener);
        ArrayList<Cannon> cannons = player.getSpaceshipPlance().getCannons();
        ArrayList<DoubleCannon> doubleCannons = new ArrayList<>();
        for(Cannon c: cannons){
            if(c instanceof DoubleCannon) {
                doubleCannons.add((DoubleCannon) c);
            }
        }
        for(Integer i: chosenIndices){
            if(i < 0 || i > doubleCannons.size()) {
                throw new ControllerExceptions("You selected a wrong chosen cannons number");
            }
            else {
                doubleCannons.get(i).setCharged(true);
            }
        }
        fromChargeToManage();
    }

    public void choosePlanets(ClientListener listener, int i) throws CargoManagementException {
        Player player = playerbyListener.get(listener);
        PlanetsCard currentPlanetsCard = (PlanetsCard) currentAdventureCard;
        ArrayList<Planet> planets = currentPlanetsCard.getPlanets();

        if(i < 0 || i > planets.size() - 1) {
            throw new ControllerExceptions("You selected a wrong planet number");
        }
        else if(planets.get(i).isBusy()) {
            throw new ControllerExceptions("The chosen planet is busy");
        }
        else if(!player.getSpaceshipPlance().checkStorage()) {
            try {
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
                throw new CargoManagementException("You got 0 storage space, you can't manage any good");
            } finally {
                players.remove(player);
                manageCard();
            }
        }

        // Se tutto va bene
        Planet planet = planets.get(i);
        planet.setBusy(true);
        currentPlanetsCard.setActivatedPlayer(player);
        currentPlanetsCard.setChosenPlanet(planet);
        currentPlanetsCard.activate();
        listener.onEvent(eventCrafter(GameState.CARGO_MANAGEMENT, null));
    }

    public void putTileBack(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        ComponentTile tile = player.getHandTile();
        ComponentTile[] tiles = game.getAssemblingTiles();
        tiles[tile.getId()] = tile;
        player.setHandTile(null);
        listener.onEvent(eventCrafter(GameState.ASSEMBLY, null));
    }


    public void addReserveSpot(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        ComponentTile tile = player.getHandTile();

        if (player.getSpaceshipPlance().getReserveSpot().size() >= 2) {
            putTileBack(listener);
        }
        else {
            player.getSpaceshipPlance().addReserveSpot(tile);
            player.setHandTile(null);
            printSpaceship(listener);
            listener.onEvent(eventCrafter(GameState.ASSEMBLY, null));
        }

    }

    public void endCargoManagement(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        players.remove(player);
        player.setReward(null);
        manageCard();
    }

    public void rotateClockwise(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        ComponentTile tile = player.getHandTile();
        tile.rotateClockwise();
        printSpaceship(listener);
        listener.onEvent(eventCrafter(GameState.PICKED_TILE, new PickedTile(tile.toString())));
    }

}

