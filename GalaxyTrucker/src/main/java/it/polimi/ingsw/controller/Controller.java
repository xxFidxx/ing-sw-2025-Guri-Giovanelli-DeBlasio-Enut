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
import it.polimi.ingsw.model.resources.*;
import it.polimi.ingsw.model.game.*;

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
    final Map <ClientListener, Boolean> isDone = new HashMap<>();
    private AdventureCard currentAdventureCard;
    private Player currentPlayer;
    private ArrayList<Player> players;
    private boolean cargoended;
    private boolean piratesended;
    private Projectile currentProjectile;
    private int currentDiceThrow;
    private ArrayList<Player> tmpPlayers;
    private List<AdventureCard> cards;
    private ArrayList<Player> defeatedPlayers;

    public Controller() {
        this.game = null;
        this.queue = new LinkedBlockingQueue<>();
        this.lobby = null;
        this.currentAdventureCard = null;
        this.currentPlayer = null;
        this.players = null;
        this.cargoended=false;
        this.piratesended=false;
        this.currentProjectile = null;
        this.defeatedPlayers = null;
        cards = null;
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

            case CHOOSE_ALIEN -> {
                event = new Event(this, state, (ListCabinAliens) data);
            }

            case SHOW_ENEMY -> {
                event = new Event(this, state, (EnemyStrenght) data);
            }
            case CHOOSE_CANNON -> {
                event = new Event(this, state, (DoubleCannonList) data);
            }
            case ADJUST_SHIP -> {
                event = new Event(this, state, (DataString) data);
            }

            case SELECT_SHIP -> {
                event = new Event(this, state, (DataString) data);
            }

            case END_GAME -> {
                event = new Event(this, state, (DataString) data);
            }

            case BYTILE_SHIP -> {
                event = new Event(this, state, (DataString) data);
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
        players= game.getPlayers();
        cards = game.getFlightplance().getDeck().getCards();


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

        synchronized (isDone) {
            for (ClientListener l : listeners) {
                isDone.put(l, false);
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
        String[] boardView = handleBoardView();
        notifyAllListeners(eventCrafter(GameState.TURN_START,boardView));


        if(!cards.isEmpty()) {
            currentAdventureCard = cards.getFirst();
            String cardName = currentAdventureCard.getName();
            int cardLevel = currentAdventureCard.getLevel();
            Card card = new Card(cardName, cardLevel);


            // aggiorniamo liste della nave prima di attivare la carta
            for (Player player : players) {
                player.getSpaceshipPlance().updateLists();
            }
            if (cardName != null) {
                notifyAllListeners(eventCrafter(GameState.DRAW_CARD, card));
                game.orderPlayers();
                players = game.getPlayers();
                tmpPlayers = new ArrayList<>(players);
                manageCard();
            }

        }
        else {
                notifyAllListeners(eventCrafter(GameState.END_GAME, new DataString(game.getEndStats())));
            }
    }

    public void manageCard(){
        switch(currentAdventureCard){
            case AbandonedShipCard asc -> {
                if (tmpPlayers.isEmpty()) {
                    resetShowAndDraw();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                if (currentAdventureCard.checkCondition(currentPlayer)) {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    tmpPlayers.remove(currentPlayer);
                    l.onEvent(eventCrafter(GameState.FAILED_CARD, null));
                    handleWaitersPlayer(l);
                } else {
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }

            case AbandonedStationCard asc -> {
                if (tmpPlayers.isEmpty()||cargoended) {
                    cargoended=false;
                    resetShowAndDraw();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                if (currentAdventureCard.checkCondition(currentPlayer)) {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    tmpPlayers.remove(currentPlayer);
                    l.onEvent(eventCrafter(GameState.FAILED_CARD, null));
                    handleWaitersPlayer(l);
                } else {
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }

            case OpenSpaceCard osc ->{
                if (tmpPlayers.isEmpty()) {
                    resetShowAndDraw();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                int numDE = 0;
                for(Engine e : currentPlayer.getSpaceshipPlance().getEngines()){
                    if(e instanceof DoubleEngine) {
                        numDE++;
                    }
                }
                if(numDE > 0) {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    tmpPlayers.remove(currentPlayer);
                    handleWaitersBattery(l, numDE);
                } else {
                    tmpPlayers.remove(currentPlayer);
                    fromChargeToManage();
                }
            }

            case SlaversCard sl -> {
                if (tmpPlayers.isEmpty()) {
                    resetShowAndDraw();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                ClientListener l = listenerbyPlayer.get(currentPlayer);
                tmpPlayers.remove(currentPlayer);
                handleWaitersEnemy(l);
            }

            case SmugglersCard sg -> {
                if (tmpPlayers.isEmpty()||cargoended) {
                    cargoended=false;
                    resetShowAndDraw();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                ClientListener l = listenerbyPlayer.get(currentPlayer);
                handleWaitersEnemy(l);
            }

            case PiratesCard pc -> {
                ArrayList<Projectile> shots = (ArrayList<Projectile>) List.of(((PiratesCard) currentAdventureCard).getShots());
                if (tmpPlayers.isEmpty()||piratesended) {
                    piratesended=false;
                    defeatedByPirates();//reset and show lo metto in questo metodo
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                ClientListener l = listenerbyPlayer.get(currentPlayer);
                tmpPlayers.remove(currentPlayer);
                handleWaitersEnemy(l);
            }

            case PlanetsCard pc -> {
                if (tmpPlayers.isEmpty()) {
                    resetShowAndDraw();
                    return;
                }
                game.orderPlayers();
                currentPlayer = tmpPlayers.getLast();
                PlanetsCard currentPlanetsCard = (PlanetsCard) currentAdventureCard;
                if (game.freePlanets(currentAdventureCard,currentPlanetsCard.getPlanets())) {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    tmpPlayers.remove(currentPlayer);
                    handleWaitersPlanets(l);
                } else {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }

            case MeteorSwarmCard msc -> {
                Projectile[] meteorArray = ((MeteorSwarmCard) currentAdventureCard).getMeteors();
                ArrayList<Projectile> meteors = new ArrayList<>(Arrays.asList(meteorArray));
                if(meteors.isEmpty()){
                    resetShowAndDraw();
                    return;
                }
                currentProjectile = meteors.getFirst();
                currentDiceThrow = game.throwDices();
                int size = players.size();
                Player first = players.get(0);
                activateMeteor(first);
                Player second = players.get(1);
                activateMeteor(second);
                if(size >= 3) {
                    Player third = players.get(2);
                    activateMeteor(third);
                    if(size == 4) {
                        Player fourth = players.get(3);
                        activateMeteor(fourth);
                    }
                }
                meteors.remove(currentProjectile);
            }

            case EpidemicCard ec -> {
                currentAdventureCard.activate();
                resetShowAndDraw();
            }

            case StardustCard sc -> {
                currentAdventureCard.activate();
                resetShowAndDraw();
            }

            default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
        }
    }


    public void activateMeteor(Player player) {
        switch(currentProjectile){
            case SmallMeteor sm -> {
                boolean check = currentProjectile.activate(player, currentDiceThrow);
                if(!check) {
                    Direction direction = currentProjectile.getDirection();
                    ArrayList<ShieldGenerator> shields = player.getSpaceshipPlance().getShields();
                    for (ShieldGenerator shield : shields) {
                        if(shield.checkProtection(direction)) {
                            ClientListener l = listenerbyPlayer.get(currentPlayer);
                            l.onEvent(eventCrafter(GameState.ASK_SHIELD, null));
                            return;
                        }
                    }
                    player.getSpaceshipPlance().takeHit(direction, currentDiceThrow);
                }
            }
            case BigMeteor bm -> {
                Direction direction = currentProjectile.getDirection();
                int result = player.getSpaceshipPlance().checkProtection(direction, currentDiceThrow);
                if(result == -1) {
                    manageCard();
                }
                else if(result == 0) {
                    player.getSpaceshipPlance().takeHit(direction, currentDiceThrow);
                }
                else if(result == 1) {
                    manageCard();
                }
                else{
                    ClientListener l = listenerbyPlayer.get(player);
                    l.onEvent(eventCrafter(GameState.ASK_CANNON, null));
                }
            }
            case SmallCannonShot scs -> {
                Direction direction = currentProjectile.getDirection();
                ArrayList<ShieldGenerator> shields = player.getSpaceshipPlance().getShields();
                for (ShieldGenerator shield : shields) {
                    if(shield.checkProtection(direction)) {
                        ClientListener l = listenerbyPlayer.get(currentPlayer);
                        l.onEvent(eventCrafter(GameState.ASK_SHIELD, null));
                        return;
                    }
                }
                player.getSpaceshipPlance().takeHit(direction, currentDiceThrow);
            }
            case BigCannonShot bcs ->{
                Direction direction = currentProjectile.getDirection();
                player.getSpaceshipPlance().takeHit(direction, currentDiceThrow);

            }
            default -> throw new IllegalStateException("Unexpected value: " + currentProjectile);
        }

    }

    public void resetShowAndDraw() {
        notifyAllListeners(eventCrafter(GameState.END_CARD, null));
        game.endTurn();
        for(ClientListener cl : listeners){
            isDone.put(cl, false);
            endCard(cl);
        }
        cards.remove(currentAdventureCard);
        drawCard();
    }

    public void activateAbandonedShipCard(ClientListener listener) throws LobbyExceptions {
        Player p = playerbyListener.get(listener);
        AbandonedShipCard currentAbandonedShipCard = (AbandonedShipCard) currentAdventureCard;
        currentAbandonedShipCard.setActivatedPlayer(p);
        currentAdventureCard.activate();
        resetShowAndDraw();
    }

    private void activateAbandonedStationCard(ClientListener listener) {
        Player p = playerbyListener.get(listener);
        AbandonedStationCard currentAbandonedStationCard = (AbandonedStationCard) currentAdventureCard;
        currentAbandonedStationCard.setActivatedPlayer(p);
        currentAdventureCard.activate();
        cargoended=true;
        listener.onEvent(eventCrafter(GameState.CARGO_MANAGEMENT, null));

    }




    public void handleWaitersPlayer(ClientListener listener){
        for(ClientListener l: listeners){
            if(l == listener) {
                l.onEvent(eventCrafter(GameState.CHOOSE_PLAYER, null));
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
                EnemyCard currentEnemyCard = (EnemyCard) currentAdventureCard;
                float playerFire = currentPlayer.getFireStrenght();
                EnemyStrenght es = new EnemyStrenght(currentEnemyCard.getCannonStrength(), playerFire);
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
                manageCard();
            }
            case SlaversCard sc -> {
                ((SlaversCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                currentAdventureCard.activate();

                if(((SlaversCard) currentCastedCard).getFightOutcome(currentPlayer) == 1){
                    resetShowAndDraw();
                }
                else
                    manageCard();
            }
            case SmugglersCard sc -> {
                ((SmugglersCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                currentAdventureCard.activate();
                if (((SmugglersCard)currentCastedCard).getFightOutcome(currentPlayer) == 1){
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    cargoended=true;
                    l.onEvent(eventCrafter(GameState.CARGO_MANAGEMENT, null));
                }
                else {
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }
            case PiratesCard pc -> {
                ((PiratesCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                currentAdventureCard.activate();
                if(((PiratesCard) currentCastedCard).getFightOutcome(currentPlayer) == -1){
                    defeatedPlayers.add(currentPlayer);
                    //defeatedByPirates(currentPlayer);
                }
                else if(((PiratesCard) currentCastedCard).getFightOutcome(currentPlayer) == 0) {
                    manageCard();
                }
                else{
                    piratesended = true;
                    manageCard();

                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + currentCastedCard);
        }
    }

    public void defeatedByPirates(){
        Projectile[] projectileArray = ((PiratesCard) currentAdventureCard).getShots();
        ArrayList<Projectile> shots = new ArrayList<>(Arrays.asList(projectileArray));
        if(shots.isEmpty()||defeatedPlayers.isEmpty()){
            defeatedPlayers.clear();
            resetShowAndDraw();
            return;
        }
        currentProjectile = shots.getFirst();
        currentDiceThrow = game.throwDices();
        int size = defeatedPlayers.size();
        Player first = defeatedPlayers.get(0);
        activateMeteor(first);
        Player second = defeatedPlayers.get(1);
        activateMeteor(second);
        if(size >= 3) {
            Player third = defeatedPlayers.get(2);
            activateMeteor(third);
            if (size == 4) {
                Player fourth = defeatedPlayers.get(3);
                activateMeteor(fourth);
            }
        }
        shots.remove(currentProjectile);
        defeatedByPirates();
    }


    public void playerIsDoneCrafting(ClientListener listener) throws Exception {
        if (isDone.get(listener))
            throw new Exception("The player was already done crafting!\n");

        int pos;
        synchronized (isDone) {
            isDone.replace(listener, true);
            synchronized (GameLock) {
                pos = isDone.keySet().size();

                for (Boolean done : isDone.values()) {
                    if (done) {
                        pos--;
                    }
                }
            }
        }

        int realpos;

        switch(pos){
            case 0 -> realpos = 0;
            case 1 -> realpos = 1;
            case 2 -> realpos = 3;
            case 3 -> realpos = 6;
            default -> realpos = 7;
        }


        Flightplance flightPlance = game.getFlightplance();
        Player p = playerbyListener.get(listener);
        flightPlance.getPlaceholderByPlayer(p).setPosizione(realpos);
        String playerColor = flightPlance.getPlaceholderByPlayer(p).getColor().name();
        listener.onEvent(eventCrafter(GameState.PLAYER_COLOR, playerColor));

        synchronized (isDone) {
            if (!isDone.containsValue(false))
                handleCraftingEnded();
            else
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
        }
    }

    private void handleCraftingEnded() {

        notifyAllListeners(eventCrafter(GameState.CRAFTING_ENDED, null));

        synchronized (isDone) {
            for (ClientListener l : listeners) {
                isDone.put(l, false);
            }
        }


        boolean allOk = true;
        for (Player p : players) {
            ClientListener l = listenerbyPlayer.get(p);
            if(!p.getSpaceshipPlance().checkCorrectness()){
                printSpaceshipAdjustment(l);
                allOk = false;
            }else{
                p.getSpaceshipPlance().updateLists();
                System.out.println("uscito da update!");
                isDone.put(l, true);
                printSpaceship(l);
            }
        }

        if(allOk){
            chooseAliens();
            }else{ // for each already done client I send state to wait for the ones who aren't done cause they have to adjust
            isDone.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .forEach(entry -> {
                        ClientListener l = entry.getKey();
                        l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
                    });
        }

    }



    private boolean handleAdjustmentEnded(){
        synchronized (isDone) {
            return !isDone.containsValue(false);
        }
    }

    private String[] handleBoardView() {

        String[] boardView = new String[18];
        Arrays.fill(boardView, "[]");


        // 3) Li “sparo” nella board in base alla loro posizione
        for(Player player: players){
            Placeholder p = player.getPlaceholder();

            int pos = (p.getPosizione()) % 18;
            if (pos < 0) {
                pos = pos + 18;
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

            goodsContainers.add(new GoodsContainer(playerReward, true,-1));

            for (CargoHolds cargo : playerCargos) {
                GoodsBlock[] goods = cargo.getGoods();
                goodsContainers.add(new GoodsContainer(goods, cargo.isSpecial(),cargo.getId()));
            }

            player.getSpaceshipPlance().setGoodsContainers(goodsContainers);
            printSpaceshipbyTile(listener,playerCargos.getFirst());
            listener.onEvent(new Event(this,GameState.CARGO_VIEW,new Cargos(goodsContainers)));
        }else {
            // qua ci sarebbe da gestire se siamo in planets quindi devi aspettare altri oppure in un reward generico quindi lui gestisce e finisce il turno per tutti...
           // separiamo i casi per ogni tipo di carta per vedere se termina subito o passa agli altri player
            try {
                throw new CargoManagementException("You got 0 storage space, you can't manage any good");
            }
            finally {
                endCargoManagement(listener);
            }
        }
    }

    private void  printSpaceshipbyTile(ClientListener listener, ComponentTile tile){
        Player player = playerbyListener.get(listener);
        String complete_ship = player.getSpaceshipPlance().reserveSpotToString() + "\n" + player.getSpaceshipPlance().tileGridToStringTile(tile);
        DataString ds = new DataString(complete_ship);
        listener.onEvent(eventCrafter(GameState.BYTILE_SHIP, ds));
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
        String complete_ship = player.getSpaceshipPlance().reserveSpotToString() + "\n" + player.getSpaceshipPlance().tileGridToStringAdjustments();
        DataString ds = new DataString(complete_ship);
        listener.onEvent(eventCrafter(GameState.SHOW_SHIP, ds));
    }

    public void printSpaceshipAdjustment(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        String complete_ship = player.getSpaceshipPlance().reserveSpotToString() + "\n" + player.getSpaceshipPlance().tileGridToStringAdjustments();
        DataString ds = new DataString(complete_ship);
        listener.onEvent(eventCrafter(GameState.ADJUST_SHIP, ds));
    }

    public void endCard(ClientListener listener) {
        Player player = playerbyListener.get(listener);
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
                throw new CargoManagementException("You got 0 storage space, you can't manage any good");
            } finally {
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
        tmpPlayers.remove(player);
        player.setReward(null);
        cargoended = true;
        manageCard();
    }

//    private void handleEndManagement(ClientListener listener) {
//        Player player = playerbyListener.get(listener);
//
//    }

    public void rotateClockwise(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        ComponentTile tile = player.getHandTile();
        tile.rotateClockwise();
        printSpaceship(listener);
        listener.onEvent(eventCrafter(GameState.PICKED_TILE, new PickedTile(tile.toString())));
    }

    public void removeAdjust(ClientListener listener, int xIndex, int yIndex) throws SpaceShipPlanceException {
        Player player = playerbyListener.get(listener);
        int stumps = player.getSpaceshipPlance().remove(xIndex, yIndex);

        // se non c'è più di un troncone, faccio un check di correttezza: se è ok, allora sono apposto altrimenti ritorno nello stato di ShipAdjustment
        if(stumps <= 1){
            if(player.getSpaceshipPlance().checkCorrectness()) {
                player.getSpaceshipPlance().updateLists();
                isDone.put(listener, true);
                printSpaceship(listener);
                if (handleAdjustmentEnded())
                    drawCard();
                else
                    listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
            }else
                printSpaceshipAdjustment(listener);
        }
        else{
            printSpaceshipParts(listener);
        }
    }

    private void printSpaceshipParts(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        String complete_ship = player.getSpaceshipPlance().reserveSpotToString() + "\n" + player.getSpaceshipPlance().tileGridToStringParts();
        DataString ds = new DataString(complete_ship);
        listener.onEvent(eventCrafter(GameState.SELECT_SHIP, ds));
    }

    public void selectShipPart(ClientListener listener, int part) {
        Player p = playerbyListener.get(listener);
        p.getSpaceshipPlance().selectPart(part);
        if(!p.getSpaceshipPlance().checkCorrectness()){
            printSpaceshipAdjustment(listener);
        }else{
            p.getSpaceshipPlance().updateLists();
            isDone.put(listener,true);
            printSpaceship(listener);
            if(handleAdjustmentEnded())
                drawCard();
            else
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
        }
    }

    private void chooseAliens(){

        System.out.println("Entrato in chooseAliens");

        synchronized (isDone) {
            for (ClientListener l : listeners) {
                isDone.put(l, false);
            }
        }

        for(ClientListener l: listeners){
            Player p = playerbyListener.get(l);
            ArrayList<Cabin> cabins = p.getSpaceshipPlance().getCabins();
            ArrayList <CabinAliens> cabinAliens = new ArrayList <>();
            boolean atLeastOneSupport = false;
            for(Cabin c: cabins){
                boolean brown = false;
                boolean purple = false;
                System.out.println(c);
                AlienColor[]  lifeSupportSystemColors = c.getLifeSupportSystemColor();
                System.out.println(Arrays.toString(lifeSupportSystemColors));
                if(Arrays.stream(lifeSupportSystemColors)
                        .anyMatch(s -> s == AlienColor.BROWN)){
                    brown = true;
                    atLeastOneSupport = true;
                }

                if(Arrays.stream(lifeSupportSystemColors)
                        .anyMatch(s -> s == AlienColor.PURPLE)){
                    purple = true;
                    atLeastOneSupport = true;
                }

                // if there is at least one support connected to this cabin I push it
                if(atLeastOneSupport)
                    cabinAliens.add(new CabinAliens(c,brown,purple));
            }


            if(!cabinAliens.isEmpty()){
                printSpaceshipbyTile(l,  cabinAliens.getFirst().getCabin());
                l.onEvent(eventCrafter(GameState.CHOOSE_ALIEN, new ListCabinAliens(cabinAliens)));
            }else{
                isDone.put(l, true);
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
            }
        }

        // if everyone went to waitPlayer, so isDone is all true
        if(!isDone.containsValue(false)){
            drawCard();
            System.out.println("Entrato in if(!isDone.containsValue(false)){");
        }

    }

    public void playerHit(ClientListener listener) {
        Player p = playerbyListener.get(listener);
        Direction direction = currentProjectile.getDirection();
        p.getSpaceshipPlance().takeHit(direction, currentDiceThrow);
        manageCard();
    }

    public void playerProtected(ClientListener listener) {
        Player p = playerbyListener.get(listener);
        // togliere una batteria dato che ha attivato lo scudo
        manageCard();
    }

    public boolean addAlienCabin(ClientListener listener, int cabinId, String alienColor) {
        Player p = playerbyListener.get(listener);
        ArrayList<Cabin> cabins = p.getSpaceshipPlance().getCabins();
        for(Cabin c: cabins){
            if(c.getId() == cabinId){
                AlienColor[] colors = c.getLifeSupportSystemColor();
                if(Objects.equals(alienColor, "b")){
                    if(colors[AlienColor.BROWN.ordinal()] != null){
                        Figure[] figures = c.getFigures();
                        figures[0] = new Alien(1, AlienColor.BROWN);
                        figures[1] = null;
                        return true;
                    }
                }

                if(Objects.equals(alienColor, "p")){
                    if(colors[AlienColor.PURPLE.ordinal()] != null){
                        Figure[] figures = c.getFigures();
                        figures[0] = new Alien(1, AlienColor.PURPLE);
                        figures[1] = null;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void handleEndChooseAliens(ClientListener listener) {
        isDone.put(listener, true);

        // player who didn't have cabins to put aliens in or finished they alien chosen have isDone = true
        if(!isDone.containsValue(false))
            drawCard();
        else
            listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
    }

    public boolean removeFigure(ClientListener listener,int cabinId, String figure){
        Player p = playerbyListener.get(listener);
        ArrayList<Cabin> cabins = p.getSpaceshipPlance().getCabins();
        for(Cabin c: cabins){
            if(c.getId() == cabinId){
                Figure[] figures = c.getFigures();
                switch (figure) {
                    case "as" -> {
                        if (figures[1] != null && figures[1] instanceof Astronaut) {
                            figures[1] = null;
                            return true;
                        } else if (figures[0] != null && figures[0] instanceof Astronaut) {
                            figures[0] = null;
                            return true;
                        } else
                            return false;
                    }
                    case "al" -> {
                        if (figures[0] != null && figures[0] instanceof Alien) {
                            figures[0] = null;
                            return true;
                        } else
                            return false;
                    }
                    case null, default -> {
                        return false;
                    }
                }
            }
        }
        return false;
    }

//    public void handleFiguresManagement(ClientListener listener) {
//        Player p = playerbyListener.get(listener);
//        ArrayList<Cabin> cabins = p.getSpaceshipPlance().getCabins();
//        for(Cabin c: cabins){
//            if(c.getId() == cabinId){
//                AlienColor[] colors = c.getLifeSupportSystemColor();
//                if(Objects.equals(alienColor, "b")){
//                    if(colors[AlienColor.BROWN.ordinal()] != null){
//                        Figure[] figures = c.getFigures();
//                        figures[0] = new Alien(1, AlienColor.BROWN);
//                        figures[1] = null;
//                        return true;
//                    }
//                }
//
//                if(Objects.equals(alienColor, "p")){
//                    if(colors[AlienColor.PURPLE.ordinal()] != null){
//                        Figure[] figures = c.getFigures();
//                        figures[0] = new Alien(1, AlienColor.BROWN);
//                        figures[1] = null;
//                        return true;
//                    }
//                }
//            }
//        }
//    }
}

