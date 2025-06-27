package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.Lobby;
import it.polimi.ingsw.controller.network.data.*;
import it.polimi.ingsw.model.adventureCards.*;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.Timer;
import it.polimi.ingsw.model.resources.*;
import it.polimi.ingsw.model.game.*;

import java.util.*;
import java.util.stream.Collectors;


public class Controller{
    private Game game;
    private Lobby lobby;
    private GameState currentGameState = GameState.IDLE;
    private final List<ClientListener> listeners = new ArrayList<>();
    private final HashMap<ClientListener,String> registredListeners = new HashMap<>();
    private final List<ClientListener> realListeners = new ArrayList<>();
    private final boolean[] busyDecks;
    private final Map<ClientListener, Player> playerbyListener = new HashMap<>();
    private final Map<Player, ClientListener> listenerbyPlayer = new HashMap<>();
    final Map <Player, Boolean> isDone = new HashMap<>();
    final Map <Player, Boolean> isDonePirates = new HashMap<>();
    private AdventureCard currentAdventureCard;
    private Player currentPlayer;
    private ArrayList<Player> players;
    final private ArrayList<Placeholder> placeholders;
    private ArrayList<Player> disconnectedPlayers;
    private ArrayList<Player> reconnectedPlayers;
    private ArrayList<Player> donecraftingPlayers;
    private boolean cargoended;
    private boolean piratesended;
    private boolean crewended;
    private Projectile currentProjectile;
    private int currentDiceThrow;
    private ArrayList<Player> tmpPlayers;
    private List<AdventureCard> cards;
    private ArrayList<Player> defeatedPlayers;
    private boolean combatZoneFlag;
    private boolean piratesFlag;
    private boolean smugglersFlag;
    private boolean enemyDefeated;
    private boolean afterShots;
    private String lastMethodCalled;
    private volatile boolean pause;

    public Controller() {
        this.game = null;
        this.lobby = null;
        this.currentAdventureCard = null;
        this.currentPlayer = null;
        this.players = null;
        this.placeholders = new ArrayList<>();
        this.disconnectedPlayers = new ArrayList<>();
        this.reconnectedPlayers = new ArrayList<>();
        this.donecraftingPlayers = new ArrayList<>();
        this.cargoended = false;
        this.piratesended = false;
        this.crewended = false;
        this.currentProjectile = null;
        this.defeatedPlayers = new ArrayList<>();
        this.cards = null;
        this.combatZoneFlag = false;
        this.piratesFlag = false;
        this.smugglersFlag = false;
        this.enemyDefeated = false;
        this.afterShots = false;
        this.busyDecks = new boolean[3];
        this.lastMethodCalled = null;
        this.pause = false;
    }

    // Setter e getter utili per il testing
    public void setPlayers(ArrayList<Player> players) { this.players = players;}
    public void setTmpPlayers(ArrayList<Player> tmpPlayers) { this.tmpPlayers = tmpPlayers;}
    public void setDefeatedPlayers(ArrayList<Player> defeatedPlayers) { this.defeatedPlayers = defeatedPlayers;}
    public void setDisconnectedPlayers(ArrayList<Player> disconnectedPlayers) { this.disconnectedPlayers = disconnectedPlayers;}
    public ArrayList<Player> getTmpPlayers() { return tmpPlayers; }
    public void addRealListener(ClientListener listener) { realListeners.add(listener);}
    public void addRealListeners(Collection<ClientListener> listeners) { this.realListeners.addAll(listeners);}
    public void addPlayerListenerPair(ClientListener listener, Player player) {
        playerbyListener.put(listener, player);
        listenerbyPlayer.put(player, listener);
    }
    public void setCards(List<AdventureCard> cards) { this.cards = new LinkedList<>(cards);}
    public GameState getCurrentGameState(){
        return currentGameState;
    }
    public AdventureCard getCurrentAdventureCard(){
        return currentAdventureCard;
    }
    public void setCurrentAdventureCard(AdventureCard currentAdventureCard) { this.currentAdventureCard = currentAdventureCard;}
    public void setEnemyDefeated(){ this.enemyDefeated=true;}
    public void setCargoended(){ this.cargoended=true;}
    public boolean getCargoended(){return this.cargoended;}
    public boolean getSmugglersFlag(){return this.smugglersFlag;}
    public void setPiratesended(){ this.piratesended=true;}
    public boolean getPiratesended(){return this.piratesended;}
    public boolean getPiratesFlag(){return this.piratesFlag;}



    public boolean getPause(){
        return pause;
    }

    public void setPause(boolean pause){
        this.pause = pause;
    }

    public void addEventListener(ClientListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }

        if(currentGameState==GameState.IDLE)
            listener.onEvent(eventCrafter(GameState.IDLE,null,null));
        else
            listener.onEvent(eventCrafter(GameState.LOBBY_PHASE,null,null));
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

    private void notifyAllRealListeners(Event event) {
        List<ClientListener> listenersCopy;
        synchronized (realListeners) {
            listenersCopy = new ArrayList<>(realListeners);
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


    public void createLobby(int numPlayers) {
        if (lobby != null) {
            throw new LobbyExceptions("Lobby is already set");
        }

        if (numPlayers < 2 || numPlayers > 4)
            throw new LobbyExceptions("Number of players must be between 2 and 4");

        lobby = new Lobby(numPlayers);
        currentGameState = GameState.LOBBY_PHASE;

        Event event = eventCrafter(GameState.LOBBY_PHASE, null, null);
        notifyAllListeners(event);
    }

    public boolean addNickname(ClientListener listener, String nickname) throws LobbyExceptions {
        if (lobby == null)
            throw new LobbyExceptions("Not existing lobby");

        if(GameState.LOBBY_PHASE.equals(currentGameState)){
            lobby.setPlayersNicknames(nickname);
            registredListeners.put(listener,nickname);
            realListeners.add(listener);

            for (ClientListener l : registredListeners.keySet()) {
                l.onEvent(eventCrafter(GameState.WAIT_LOBBY, null, null));
            }

            if (lobby.isFull()) {
                gameInit();
                return true;
            }
            return false;
        }
        return true;
    }

    public void addTile(ClientListener listener, int xIndex, int yIndex) throws SpaceShipPlanceException {
        Player player = playerbyListener.get(listener);
        ComponentTile tile = player.getHandTile();
        try {
            player.getSpaceshipPlance().placeTileComponents(tile, xIndex, yIndex);
        } finally {
            printSpaceship(listener);
            listener.onEvent(eventCrafter(GameState.ASSEMBLY, null, player));
        }
    }

    public void pickTile(ClientListener listener, int tileId) throws LobbyExceptions {
        Player player = playerbyListener.get(listener);
        ComponentTile tile = null;

        if (tileId >= 1000) {
            if(tileId == 1000)
                tile = game.pickTileReserveSpot(player, 0);
            else if(tileId == 1001)
                tile = game.pickTileReserveSpot(player, 1);

            if(tile!=null)
                listener.onEvent(eventCrafter(GameState.PICK_RESERVED_CARD, tile, player));
            else{
                listener.onEvent(eventCrafter(GameState.VOID_RESERVED_SPOT, null, null));
                listener.onEvent(eventCrafter(GameState.ASSEMBLY, null, player));
            }
        } else {
            tile = game.pickTile(player, tileId);
            if (tile != null) {
                printSpaceship(listener);
                listener.onEvent(eventCrafter(GameState.PICKED_TILE, tile, null));
            } else {
                listener.onEvent(eventCrafter(GameState.ROBBED_TILE, null, null));
                listener.onEvent(eventCrafter(GameState.ASSEMBLY, null, player));
            }
        }
    }

    public void handleOnConnectState(ClientListener listener) {
        listener.onEvent(eventCrafter(GameState.LOBBY_PHASE, null, null));
    }

    public Event eventCrafter(GameState state, Object data, Player player) {
        Event event;
        switch (state) {
            case WAIT_LOBBY -> {
                ArrayList<String> nicks;
                    nicks = lobby.getPlayersNicknames();
                event = new Event(state, new LobbyNicks(nicks));
            }
            case ASSEMBLY -> {
                Integer[] assemblingTilesIds;
                    assemblingTilesIds = game.getTilesId();

                ArrayList<ComponentTile> reservedTiles = player.getSpaceshipPlance().getReserveSpot();

                ArrayList <PickedTile> reserveTiles = new ArrayList<>();

                for(ComponentTile componentTile: reservedTiles)
                    reserveTiles.add(new PickedTile(componentTile.toString(), componentTile.getRotation()));

                event = new Event(state, new PickableTiles(assemblingTilesIds, reserveTiles));
            }

            case SHOW_SHIP -> {
                event = new Event(state, (DataString) data);
            }

            case PICKED_TILE,PICK_RESERVED_CARD -> {
                ComponentTile tile = (ComponentTile) data;
                event = new Event(state, new PickedTile(tile.toString(), tile.getRotation()));
            }

            case DRAW_CARD -> {
                String cardName = currentAdventureCard.getName();
                int cardLevel = currentAdventureCard.getLevel();
                switch(currentAdventureCard){
                    case AbandonedShipCard asc -> {
                        int ld = ((AbandonedShipCard) currentAdventureCard).getLostDays();
                        int lc = ((AbandonedShipCard) currentAdventureCard).getLostCrew();
                        int cr = ((AbandonedShipCard) currentAdventureCard).getReward();
                        AbShipCard ashipc = new AbShipCard(cardName, cardLevel, ld, lc, cr);
                        event = new Event(state, ashipc);
                    }
                    case AbandonedStationCard asc -> {
                        int ld = ((AbandonedStationCard) currentAdventureCard).getLostDays();
                        int rc = ((AbandonedStationCard) currentAdventureCard).getRequiredCrew();
                        GoodsBlock[] rw = ((AbandonedStationCard) currentAdventureCard).getReward();
                        for(GoodsBlock block: rw){
                            System.out.printf(" [" + block.getValue() + "] ");
                        }
                        AbStationCard astationc = new AbStationCard(cardName, cardLevel, ld, rc, rw);
                        event = new Event(state, astationc);
                    }
                    case CombatZoneCard czc -> {
                        int ld = ((CombatZoneCard) currentAdventureCard).getLostDays();
                        if(((CombatZoneCard) currentAdventureCard).getType() == CombatZoneType.LOSTCREW){
                            int lc = ((CombatZoneCard) currentAdventureCard).getLostOther();
                            CZCCrew czcg = new CZCCrew(cardName, cardLevel, ld, lc);
                            event = new Event(state, czcg);
                        } else {
                            int lg = ((CombatZoneCard) currentAdventureCard).getLostOther();
                            CZCGoods czcg = new CZCGoods(cardName, cardLevel, ld, lg);
                            event = new Event(state, czcg);
                        }
                    }
                    case PiratesCard pc -> {
                        int ld = ((PiratesCard) currentAdventureCard).getLostDays();
                        int cr = ((PiratesCard) currentAdventureCard).getReward();
                        Pirates pcdata = new Pirates(cardName, cardLevel, ld, cr);
                        event = new Event(state, pcdata);
                    }
                    case PlanetsCard pc -> {
                        int ld = ((PlanetsCard) currentAdventureCard).getLostDays();
                        Planets pl = new Planets(cardName, cardLevel, ld);
                        event = new Event(state, pl);
                    }
                    case SlaversCard sc -> {
                        int ld = ((SlaversCard) currentAdventureCard).getLostDays();
                        int lc = ((SlaversCard) currentAdventureCard).getLostCrew();
                        int cr = ((SlaversCard) currentAdventureCard).getReward();
                        Slavers scdata = new Slavers(cardName, cardLevel, ld, lc, cr);
                        event = new Event(state, scdata);
                    }
                    case SmugglersCard sm -> {
                        int ld = ((SmugglersCard) currentAdventureCard).getLostDays();
                        int lg = ((SmugglersCard) currentAdventureCard).getLossMalus();
                        GoodsBlock[] rw = ((SmugglersCard) currentAdventureCard).getReward();
                        Smugglers smdata = new Smugglers(cardName, cardLevel, ld, lg, rw);
                        event = new Event(state, smdata);
                    }
                    default -> {
                        Card card = new Card(cardName, cardLevel);
                        event = new Event(state, card);
                    }
                }
            }

            case SHOW_CARDS ->{
                int nDeck = (int) data;
                ArrayList<AdventureCard> advCardsToShow = new ArrayList<>();

                switch(nDeck) {
                    case 1 -> {
                        advCardsToShow = new ArrayList<>(cards.subList(0, 3));
                    }
                    case 2 -> {
                        advCardsToShow = new ArrayList<>(cards.subList(4, 7));
                    }
                    case 3 -> {
                        advCardsToShow = new ArrayList<>(cards.subList(8, 11));
                    }
                }

                ArrayList<Card> cardsToShow = new ArrayList<>();
                for(AdventureCard card : advCardsToShow) {
                    cardsToShow.add(new Card(card.getName(), card.getLevel()));
                }

                event = new Event(state, new AdventureCardsData(cardsToShow, nDeck));
            }

            case PLAYER_COLOR -> event = new Event(state, new PlayerColor((String) data));

            case TURN_START -> {
                String[] boardView = handleBoardView();
                event = new Event(state, new BoardView((boardView)));
            }

            case SHOW_PLAYER -> {
                String nick = player.getNickname();
                int pos = player.getPlaceholder().getPosizione();
                int cred = player.getCredits();
                int astr = player.getSpaceshipPlance().getnAstronauts();
                int al = player.getSpaceshipPlance().getBrownAliens() + player.getSpaceshipPlance().getPurpleAliens();
                event = new Event(state, new PlayerInfo(nick, pos, cred, astr, al));
            }

            case CHOOSE_ENGINE -> {
                int es = player.getEngineStrenght();
                int numDE = 0;
                for (Engine e : player.getSpaceshipPlance().getEngines()) {
                    if (e instanceof DoubleEngine) {
                        numDE++;
                    }
                }
                if(numDE > 0)
                    event = new Event(state, new DoubleEngineNumber(es, numDE));
                else
                    event = eventCrafter(GameState.NO_DOUBLE_ENGINE, null, null);
            }

            case CHOOSE_PLANETS -> {
                event = new Event(state, new PlanetsBlock((ArrayList<Planet>) data));
            }

            case CHOOSE_ALIEN -> {
                ArrayList<CabinAliens> cabinAliens = (ArrayList<CabinAliens>) data;
                event = new Event(state, new ListCabinAliens(cabinAliens));
            }

            case SHOW_ENEMY -> {
                EnemyCard currentEnemyCard = (EnemyCard) currentAdventureCard;
                float playerFire = player.getFireStrenght();
                event = new Event(state, new EnemyStrenght(currentEnemyCard.getCannonStrength(), playerFire));
            }

            case CHOOSE_CANNON -> {
                float fs = player.getFireStrenght();
                ArrayList<DoubleCannon> doubleCannons = new ArrayList<>();
                for (Cannon c : player.getSpaceshipPlance().getCannons()) {
                    if (c instanceof DoubleCannon) {
                        doubleCannons.add((DoubleCannon) c);
                    }
                }

                if (!doubleCannons.isEmpty()) {
                    System.out.println("handleWaitersEnemy: mando in CHOOSE_CANNON");
                    event = new Event(state, new DoubleCannonList(doubleCannons));
                } else {
                    System.out.println("handleWaitersEnemy: mando in NO_DOUBLE_CANNON");
                    event = eventCrafter(GameState.NO_DOUBLE_CANNON, null, null);
                }

            }
            case ADJUST_SHIP -> {
                event = new Event(state, (DataString) data);
            }

            case SELECT_SHIP -> {
                event = new Event(state, (DataString) data);
            }

            case END_GAME -> {
                event = new Event(state, new DataString(game.getEndStats()));
            }

            case BYTILE_SHIP -> {
                event = new Event(state, (DataString) data);
            }

            case MOVE_PLAYER -> {
                event = new Event(state, new LostDays((int) data));
            }

            case MOVE_FORWARD -> {
                event = new Event(state, new ForwardDays((int) data));
            }

            case LOST_CREW -> {
                event = new Event(state, new LostCrew((int) data));
            }

            case GET_CREDITS -> {
                event = new Event(state, new Credits((int) data));
            }

            case CREW_MANAGEMENT -> {
                lastMethodCalled = "crewManagement";
                System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);

                int astr = player.getSpaceshipPlance().getnAstronauts();
                int al = player.getSpaceshipPlance().getBrownAliens() + player.getSpaceshipPlance().getPurpleAliens();
                ArrayList<Cabin> cabins = player.getSpaceshipPlance().getCabins();
                int playersCrew = astr + al;
                int lostCrew = (int) data;

                if (playersCrew < lostCrew) {
                    lostCrew = playersCrew;
                }
                if (!cabins.isEmpty()) {
                    printSpaceshipbyTile(listenerbyPlayer.get(player), cabins.getFirst());
                }
                event = new Event(state, new CrewManagement(cabins, lostCrew));
            }

            case EPIDEMIC_MANAGEMENT -> {
                lastMethodCalled = "epidemicManagement";
                System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);

                ArrayList<Cabin> cabins = player.getSpaceshipPlance().getInterconnectedCabins();
                event = new Event(state, new EpidemicManagement(cabins));
            }

            case BATTERIES_MANAGEMENT,REMOVE_EXTRA_BATTERIES -> {
                ArrayList<PowerCenter> pc = player.getSpaceshipPlance().getPowerCenters();

                if(!pc.isEmpty())
                printSpaceshipbyTile(listenerbyPlayer.get(player), pc.getFirst());

                event = new Event(state, new BatteriesManagement((int) data, pc));
            }
            case REMOVE_MV_GOODS -> {
                ArrayList<GoodsContainer> goodsContainers = new ArrayList<>();
                ArrayList<CargoHolds> playerCargos = player.getSpaceshipPlance().getCargoHolds();

                for (CargoHolds cargo : playerCargos) {
                    GoodsBlock[] goods = cargo.getGoods();
                    goodsContainers.add(new GoodsContainer(goods, cargo.isSpecial(), cargo.getId()));
                }

                int playerGoods = player.getSpaceshipPlance().countGoods();
                int cardMalus = (int) data;
                int diff = playerGoods - cardMalus;
                System.out.println("REMOVE_MV_GOODS: diff: " + diff);

                int playerBatteries = player.getSpaceshipPlance().getnBatteries();
                int batteriesToRemove = 0;
                int cargosToRemove = cardMalus;

                if (diff < 0) {

                    if (playerBatteries > 0) {
                        int diffBatteries = playerBatteries + diff;
                        System.out.println("REMOVE_MV_GOODS: diff Batteries: " + diffBatteries);

                        if (diffBatteries >= 0) {
                            batteriesToRemove = -diff;
                        } else {
                            batteriesToRemove = playerBatteries;
                        }
                    }

                    if (playerGoods > 0) {
                        cargosToRemove = playerGoods;
                    } else {
                        cargosToRemove = 0;
                    }
                }

                if(!playerCargos.isEmpty())
                printSpaceshipbyTile(listenerbyPlayer.get(player), playerCargos.getFirst());

                event = new Event(state, new RemoveMostValuable(cargosToRemove, goodsContainers, batteriesToRemove));
            }
            case SCS_DIR_POS -> {
                event = new Event(state, new SmallCannonDirPos((Direction) data, currentDiceThrow));
            }

            case BMS_DIR_POS -> {
                event = new Event(state, new BigMeteorDirPos((Direction) data, currentDiceThrow));
            }

            case SMS_DIR_POS -> {
                event = new Event(state, new SmallMeteorDirPos((Direction) data, currentDiceThrow));
            }

            case BCS_DIR_POS -> {
                event = new Event(state, new BigCannonDirPos((Direction) data, currentDiceThrow));
            }

            default ->
                    event = new Event(state, null); // in cases where you don't have to send data, you just send the current state
        }

        return event;
    }

    public void gameInit() {

        currentGameState = GameState.GAME_INIT;
        notifyAllRealListeners(eventCrafter(currentGameState, null, null));

        currentGameState = GameState.ASSEMBLY;

        ArrayList<String> nicks = lobby.getPlayersNicknames();
        game = new Game(nicks);
        players = new ArrayList<>(game.getPlayers());
        cards = game.getFlightplance().getDeck().getCards();

        for (boolean b : busyDecks)
            b = false;


        synchronized (playerbyListener) {
            for (int i = 0; i < players.size(); i++) {
                playerbyListener.put(realListeners.get(i), players.get(i));
            }
        }

        synchronized (listenerbyPlayer) {
            for (int i = 0; i < realListeners.size(); i++) {
                listenerbyPlayer.put(players.get(i), realListeners.get(i));
            }
        }

        synchronized (isDone) {
            for (ClientListener l : realListeners) {
                Player p = playerbyListener.get(l);
                isDone.put(p, false);
            }
        }

        for(ClientListener listener: realListeners){
            listener.onEvent(eventCrafter(currentGameState, null, playerbyListener.get(listener)));
        }

        game.getTimer().start();

    }

    public void restoreReconnectedPlayers(){
        lastMethodCalled = null;
        System.out.println("lastMethodCalled = null;");
        currentGameState = GameState.TURN_START;
        List<Player> playersToRestore = new ArrayList<>(reconnectedPlayers);

        for (Player player : playersToRestore) {
            realListeners.add(listenerbyPlayer.get(player));
            isDone.put(player, false);
            isDonePirates.put(player, false);
        }

        boolean allOk = true;

        for(Player player: players){
            isDone.put(player,true);
        }

        for (Player p : playersToRestore) {
            ClientListener l = listenerbyPlayer.get(p);
            if (!p.getSpaceshipPlance().isCorrect() || !p.getSpaceshipPlance().checkCorrectness()) {
                printSpaceshipAdjustment(l);
                allOk = false;
            } else {
                players.add(p);
                isDone.put(p, true);
                reconnectedPlayers.remove(p);
            }
        }

        if (allOk) {
            resetIsDoneDraw();
        } else {
            for(Player p: players){
                    ClientListener l = listenerbyPlayer.get(p);
                    l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }
    }

    private void resetIsDoneDraw() {

        for(Player player: reconnectedPlayers){
            players.add(player);
        }

        reconnectedPlayers.clear();

        for(Player p: players){
            isDone.put(p, false);
        }
        drawCard();
    }


    public void drawCard() {


        notifyAllRealListeners(eventCrafter(GameState.TURN_START, null, null));

        if (!cards.isEmpty() || players.isEmpty()) {
//            Random random = new Random();
//            int randomNumber = random.nextInt(cards.size());
//            currentAdventureCard = cards.get(randomNumber);
            currentAdventureCard = cards.getFirst();
            String cardName = currentAdventureCard.getName();
            // int cardLevel = currentAdventureCard.getLevel();
            // Card card = new Card(cardName, cardLevel);


            // aggiorniamo liste della nave prima di attivare la carta
            for (Player player : players) {
                player.getSpaceshipPlance().updateLists();
            }
            if (cardName != null) {
                currentGameState = GameState.DRAW_CARD;
                notifyAllRealListeners(eventCrafter(GameState.DRAW_CARD, null, null));
                if(players.size() > 1 || !(currentAdventureCard instanceof CombatZoneCard)){
                    orderPlayers();
                    tmpPlayers = new ArrayList<>(players);
                    isDone.replaceAll((c, v) -> false);
                    manageCard();
                }else{
                    notifyAllRealListeners(eventCrafter(GameState.SKIPPED_CARD, null, null));
                    resetShowAndDraw();
                }
            }

        } else {
            notifyAllRealListeners(eventCrafter(GameState.END_GAME, null, null));
        }
    }

    public void manageCard() {
        // nelle carte dove si chiede di rimuovere alieni/batterie, voi fate finta che, chw l'abbiano già fatto e a fine turno chi deve rimuovere
        // invece glieli facciamo fisicamente rimuovere, dopo che tutti li avranno rimossi, allora vai in resetShowandDrawn
        switch (currentAdventureCard) {
            case AbandonedShipCard asc -> {
                if (tmpPlayers.isEmpty() || crewended) {
                    resetShowAndDraw();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();

                if(!disconnectedPlayers.contains(currentPlayer)){
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    if (currentAdventureCard.checkCondition(currentPlayer)) {
                        tmpPlayers.remove(currentPlayer);
                        handleWaitersPlayer(l);
                    } else {
                        l.onEvent(eventCrafter(GameState.FAILED_CARD, null, null));
                        tmpPlayers.remove(currentPlayer);
                        manageCard();
                    }
                }else{
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }

            case AbandonedStationCard asc -> {
                if (tmpPlayers.isEmpty() || cargoended) {
                    cargoended = false;
                    resetShowAndDraw();
                    return;
                }

                currentPlayer = tmpPlayers.getLast();
                if(!disconnectedPlayers.contains(currentPlayer)){
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    if (currentAdventureCard.checkCondition(currentPlayer)) {
                        tmpPlayers.remove(currentPlayer);
                        handleWaitersPlayer(l);
                    } else {
                        l.onEvent(eventCrafter(GameState.FAILED_CARD, null, null));
                        tmpPlayers.remove(currentPlayer);
                        manageCard();
                    }
                }else{
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }

            case OpenSpaceCard osc -> {
                if (tmpPlayers.isEmpty()) {
                    resetShowAndDraw();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                if(!disconnectedPlayers.contains(currentPlayer)){
                    int numE = 0;
                    int numDE = 0;
                    for (Engine e : currentPlayer.getSpaceshipPlance().getEngines()) {
                        numE++;
                        if (e instanceof DoubleEngine) {
                            numDE++;
                        }
                    }
                    if (numE == 0) {
                        handleEarlyEnd(currentPlayer);
                        manageCard();
                    } else {
                        ClientListener l = listenerbyPlayer.get(currentPlayer);
                        tmpPlayers.remove(currentPlayer);
                        if (numDE > 0) {
                            handleWaitersBattery(l, currentPlayer);
                        } else {
                            fromChargeToManage(l);
                        }
                    }
                }else{
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }

            case SlaversCard sl -> {
                if (tmpPlayers.isEmpty() || enemyDefeated) {
                    System.out.println("manageCard: vado in defeatedBySlavers");
                    defeatedBySlavers();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                if(!disconnectedPlayers.contains(currentPlayer)){
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    tmpPlayers.remove(currentPlayer);
                    handleWaitersEnemy(l);
                }else{
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }

            case SmugglersCard sg -> {
                if (tmpPlayers.isEmpty() || cargoended) {
                    System.out.println("manageCard: cargoended " + cargoended);
                    cargoended = false;
                    smugglersFlag = true;
                    System.out.println("manageCard: vado in defeatedBySmugglers");
                    defeatedBySmugglers();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                if(!disconnectedPlayers.contains(currentPlayer)){
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    tmpPlayers.remove(currentPlayer);
                    handleWaitersEnemy(l);
                }else{
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }

            case PiratesCard pc -> {
                if (tmpPlayers.isEmpty() || piratesended) {
                    piratesended = false;
                    piratesFlag = true;
                    if(!defeatedPlayers.isEmpty()){
                        for (Player p : defeatedPlayers) {
                            isDonePirates.put(p, false);
                        }
                    }
                    System.out.println("manageCard: vado in defeatedByPirates");
                    defeatedByPirates();//reset and show lo metto in questo metodo
                    return;
                }
                currentPlayer = tmpPlayers.getLast();

                if(!disconnectedPlayers.contains(currentPlayer)){
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    tmpPlayers.remove(currentPlayer);
                    handleWaitersEnemy(l);
                }else{
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }

            case PlanetsCard pc -> {
                if(!tmpPlayers.isEmpty()) {
                    currentPlayer = tmpPlayers.getLast();
                    if(!disconnectedPlayers.contains(currentPlayer)){
                        System.out.println("PlanetsCard, currentPlayer: " + currentPlayer);
                        PlanetsCard currentPlanetsCard = (PlanetsCard) currentAdventureCard;
                        if (game.freePlanets(currentAdventureCard, currentPlanetsCard.getPlanets())) {
                            System.out.println("PlanetsCard: mando in handleWaitersPlanets");
                            handleWaitersPlanets(currentPlayer);
                        } else {
                            ClientListener l = listenerbyPlayer.get(currentPlayer);
                            tmpPlayers.remove(currentPlayer);
                            handlePlanets(l);
                        }
                    }else{
                        tmpPlayers.remove(currentPlayer);
                        manageCard();
                    }
                }
            }

            case MeteorSwarmCard msc -> {
                Projectile[] meteorArray = ((MeteorSwarmCard) currentAdventureCard).getMeteors();
                int length = meteorArray.length;
                int i = 0;
                while (i < length && meteorArray[i] == null) {
                    i++;
                }
                if (i == length) {
                    resetShowAndDraw();
                    return;
                }
                currentProjectile = meteorArray[i];
                meteorArray[i] = null;
                currentDiceThrow = game.throwDices();
                int size = players.size();
                Player first = players.get(0);
                System.out.println("manageCard: attivo activateMeteor per il primo player ");
                activateMeteor(first);
                if(size >= 2) {
                    Player second = players.get(1);
                    System.out.println("manageCard: attivo activateMeteor per il secondo player ");
                    activateMeteor(second);
                    if (size >= 3) {
                        Player third = players.get(2);
                        activateMeteor(third);
                        if (size == 4) {
                            Player fourth = players.get(3);
                            activateMeteor(fourth);
                        }
                    }
                }
            }

            case EpidemicCard ec -> {
                for(Player p : players) {
                    ClientListener l = listenerbyPlayer.get(p);
                    l.onEvent(eventCrafter(GameState.EPIDEMIC_MANAGEMENT, null, p));
                }
            }

            case StardustCard sc -> {
                for(Player p : players) {
                    ClientListener l = listenerbyPlayer.get(p);
                    int ec = p.getSpaceshipPlance().countExposedConnectors();
                    l.onEvent(eventCrafter(GameState.MOVE_PLAYER, ec, null));
                    game.getFlightplance().move(-ec, p);
                }

                for(Player p : disconnectedPlayers) {
                    int ec = p.getSpaceshipPlance().countExposedConnectors();
                    game.getFlightplance().move(-ec, p);
                }
                resetShowAndDraw();
            }

            case CombatZoneCard czc -> {

                for(Player p : disconnectedPlayers) {
                    if(tmpPlayers.contains(p))
                        tmpPlayers.remove(p);
                }

                if(tmpPlayers.size() == 1){
                    resetShowAndDraw();
                    return;
                }

                if(((CombatZoneCard)currentAdventureCard).getType() == CombatZoneType.LOSTCREW){
                    int minEquip = tmpPlayers.stream().mapToInt(p -> p.getSpaceshipPlance().getCrew()).min().orElse(Integer.MAX_VALUE);
                    List<Player> minEquipPlayers = tmpPlayers.stream().filter(p -> p.getSpaceshipPlance().getCrew() == minEquip).collect(Collectors.toList());
                    if (minEquipPlayers.size() == 1) {
                        Player minEquipPlayer = minEquipPlayers.get(0);
                        int ld= ((CombatZoneCard) currentAdventureCard).getLostDays();
                        ClientListener l = listenerbyPlayer.get(minEquipPlayer);
                        handleMinEquip(l);
                        l.onEvent(eventCrafter(GameState.MOVE_PLAYER, ld, null));
                        game.getFlightplance().move(-ld, minEquipPlayer);
                    } else {
                        notifyAllRealListeners(eventCrafter(GameState.SAME_EQUIP, null, null));
                    }

                    for (Player player: players) {
                        ClientListener l= listenerbyPlayer.get(player);
                        System.out.println("listener.onEvent(eventCrafter(GameState.CHOOSE_ENGINE, null, player));");
                        l.onEvent(eventCrafter(GameState.CHOOSE_ENGINE, null, player));
                    }
                } else {
                    for (Player player: players) {
                        ClientListener l= listenerbyPlayer.get(player);
                        l.onEvent(eventCrafter(GameState.CHOOSE_CANNON, null, player));
                    }
                }

            }

            default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
        }
    }

    public void handlePlanets(ClientListener l) {
        if(l!=null) {
            lastMethodCalled = "handlePlanets";
            System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
            Player p = playerbyListener.get(l);
            isDone.put(p, true);
        }
        if(!isDone.containsValue(false)){
            resetShowAndDraw();
        } else {
            if(l!=null)
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));

            manageCard();
        }
    }

    public void handleEarlyEnd(Player player) {
        ClientListener listener = listenerbyPlayer.get(player);
        isDone.remove(player);
        System.out.println("handleEarlyEnd " + player);
        players.remove(player);
        tmpPlayers.remove(player);
        listener.onEvent(eventCrafter(GameState.DIED, null, null));
    }


    public void activateMeteor(Player player) {
        switch (currentProjectile) {
            case SmallMeteor sm -> {
                System.out.println("activateMeteor: Small Meteor");
                ClientListener l = listenerbyPlayer.get(player);
                Direction direction = currentProjectile.getDirection();
                l.onEvent(eventCrafter(GameState.SMS_DIR_POS, direction, null));
                boolean check = currentProjectile.activate(player, currentDiceThrow);
                System.out.println("Check " + check);
                if (!check) {
                    ArrayList<ShieldGenerator> shields = player.getSpaceshipPlance().getShields();
                    for (ShieldGenerator shield : shields) {
                        if (shield.checkProtection(direction)) {
                            lastMethodCalled = "checkProtection";
                            System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
                            l.onEvent(eventCrafter(GameState.ASK_SHIELD, null, null));
                            return;
                        }
                    }
                    playerHit(l);
                } else {
                    l.onEvent(eventCrafter(GameState.NO_EXPOSED_CONNECTORS, null, null));
                    waitForNextShotMeteor(l);
                }
            }
            case BigMeteor bm -> {
                System.out.println("activateMeteor: Big Meteor");
                Direction direction = currentProjectile.getDirection();
                ClientListener l = listenerbyPlayer.get(player);
                l.onEvent(eventCrafter(GameState.BMS_DIR_POS, direction, null));
                int result = player.getSpaceshipPlance().checkProtection(direction, currentDiceThrow);
                System.out.println("activateMeteor: result " + result);
                if (result == -1) {
                    l.onEvent(eventCrafter(GameState.NO_HIT, null, null));
                    waitForNextShotMeteor(l);
                } else if (result == 0) {
                    playerHit(l);
                } else if (result == 1) {
                    l.onEvent(eventCrafter(GameState.SINGLE_CANNON_PROTECTION, null, null));
                    waitForNextShotMeteor(l);
                } else {
                    lastMethodCalled = "checkProtection";
                    System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
                    l.onEvent(eventCrafter(GameState.ASK_CANNON, null, null));
                }
            }
            case SmallCannonShot scs -> {
                Direction direction = currentProjectile.getDirection();
                ClientListener l = listenerbyPlayer.get(player);
                l.onEvent(eventCrafter(GameState.SCS_DIR_POS, direction, null));
                ArrayList<ShieldGenerator> shields = player.getSpaceshipPlance().getShields();
                for (ShieldGenerator shield : shields) {
                    if (shield.checkProtection(direction)) {
                        lastMethodCalled = "checkProtection";
                        System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
                        l.onEvent(eventCrafter(GameState.ASK_SHIELD, null, null));
                        return;
                    }
                }
                playerHit(l);
            }
            case BigCannonShot bcs -> {
                Direction direction = currentProjectile.getDirection();
                ClientListener l = listenerbyPlayer.get(player);
                l.onEvent(eventCrafter(GameState.BCS_DIR_POS, direction, null));
                playerHit(l);
            }
            default -> throw new IllegalStateException("Unexpected value: " + currentProjectile);
        }

    }

    public void resetShowAndDraw() {
        System.out.println("resetShowAndDraw: mando in END_CARD");
        notifyAllRealListeners(eventCrafter(GameState.END_CARD, null, null));
        game.endTurn();
        isDone.replaceAll((c, v) -> false);
        isDonePirates.replaceAll((c, v) -> false);
        cargoended = false;
        combatZoneFlag = false;
        piratesFlag = false;
        smugglersFlag = false;
        enemyDefeated = false;
        piratesended = false;
        crewended = false;
        afterShots = false;
        lastMethodCalled = null;
        System.out.println("Stampa temporanea: lastMethodCalled NULL");
        endCard();
        for (Player player : players) {
            player.getSpaceshipPlance().updateLists();
        }
        cards.remove(currentAdventureCard);
        endTurn();
    }

    public void activateAbandonedShipCard(ClientListener listener) throws LobbyExceptions {
        Player p = playerbyListener.get(listener);
        AbandonedShipCard currentAbandonedShipCard = (AbandonedShipCard) currentAdventureCard;
        currentAbandonedShipCard.setActivatedPlayer(p);
        int ld = currentAbandonedShipCard.getLostDays();
        listener.onEvent(eventCrafter(GameState.MOVE_PLAYER, ld, null));
        int c = currentAbandonedShipCard.getReward();
        listener.onEvent(eventCrafter(GameState.GET_CREDITS, c, null));
        currentAdventureCard.activate();
        crewended=true;
        int lostCrew = ((AbandonedShipCard)currentAdventureCard).getLostCrew();
        listener.onEvent(eventCrafter(GameState.CREW_MANAGEMENT, lostCrew, p));
    }

    private void activateAbandonedStationCard(ClientListener listener) {
        Player p = playerbyListener.get(listener);
        AbandonedStationCard currentAbandonedStationCard = (AbandonedStationCard) currentAdventureCard;
        currentAbandonedStationCard.setActivatedPlayer(p);
        int ld = currentAbandonedStationCard.getLostDays();
        listener.onEvent(eventCrafter(GameState.MOVE_PLAYER, ld, null));
        currentAdventureCard.activate();
        cargoended = true;
        listener.onEvent(eventCrafter(GameState.CARGO_MANAGEMENT, null, null));
    }

    public void handleWaitersPlayer(ClientListener listener) {

        lastMethodCalled = "handleWaitersPlayer";
        System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);

        if(listener != null){
            for (Player player: players) {
                ClientListener l = listenerbyPlayer.get(player);
                if (l == listener) {
                    l.onEvent(eventCrafter(GameState.CHOOSE_PLAYER, null, null));
                } else {
                    l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                }
            }
        }

    }

    public void handleMinEquip(ClientListener listener) {
        for (Player player: players) {
           ClientListener l= listenerbyPlayer.get(player);
            if (l == listener) {
                l.onEvent(eventCrafter(GameState.LEAST_CREW, null, null));
            } else {
                l.onEvent(eventCrafter(GameState.NOT_MIN_EQUIP, null, null));
            }
        }
    }

    public void handleMinEngine(ClientListener listener) {
        for (Player player: players) {
           ClientListener l= listenerbyPlayer.get(player);
            if (l == listener) {
                System.out.println("mando in LEAST_ENGINE");
                l.onEvent(eventCrafter(GameState.LEAST_ENGINE, null, null));
            } else {
                System.out.println("mando in NOT_MIN_ENGINE");
                l.onEvent(eventCrafter(GameState.NOT_MIN_ENGINE, null, null));
            }
        }
    }

    public void handleMinFire(ClientListener listener) {
        for (Player player: players) {
           ClientListener l= listenerbyPlayer.get(player);
            if (l == listener) {
                System.out.println("mando in LEAST_FIRE");
                l.onEvent(eventCrafter(GameState.LEAST_FIRE, null, null));
            } else {
                System.out.println("mando in NOT_MIN_FIRE");
                l.onEvent(eventCrafter(GameState.NOT_MIN_FIRE, null, null));
            }
        }
    }

    public void handleWaitersBattery(ClientListener listener, Player player) {

        lastMethodCalled = "handleWaitersBattery";
        System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);

        if(listener != null){
            for (Player p: players) {
                ClientListener l= listenerbyPlayer.get(p);
                if (l == listener) {
                    listener.onEvent(eventCrafter(GameState.CHOOSE_ENGINE, null, player));
                } else {
                    l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                }
            }
        }

    }

    public void handleWaitersEnemy(ClientListener listener) {

        lastMethodCalled = "handleWaitersEnemy";
        System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);

        if(listener!=null){
            for (Player player: players) {
                ClientListener l= listenerbyPlayer.get(player);
                System.out.println("handleWaitersEnemy: Listener: " + l);
                if (l == listener) {
                    l.onEvent(eventCrafter(GameState.SHOW_ENEMY, null, currentPlayer));
                    l.onEvent(eventCrafter(GameState.CHOOSE_CANNON, null, currentPlayer));
                } else {
                    System.out.println("handleWaitersEnemy: mando in WAIT_PLAYER");
                    l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                }
            }
        }
    }

    public void handleWaitersPlanets(Player chosenPlayer) {

        lastMethodCalled = "handleWaitersPlanets";
        System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);

        if(!(disconnectedPlayers.contains(chosenPlayer))){
            for (Player player: tmpPlayers) {
                ClientListener l = listenerbyPlayer.get(player);
                if (player == chosenPlayer) {
                    PlanetsCard currentPlanetsCard = (PlanetsCard) currentAdventureCard;
                    l.onEvent(eventCrafter(GameState.CHOOSE_PLANETS, currentPlanetsCard.getPlanets(), null));
                } else {
                    l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                }
            }
        }
        tmpPlayers.remove(chosenPlayer);
    }

    public void fromChargeToManage(ClientListener listener) {
        AdventureCard currentCastedCard = currentAdventureCard;
        Player player = null;
        if(listener== null && !(currentCastedCard instanceof CombatZoneCard))
            manageCard();
        else{
            if(listener!=null) {
                lastMethodCalled = "fromChargeToManage";
                System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
                player = playerbyListener.get(listener);
                player.getSpaceshipPlance().updateLists();
            }

            switch (currentCastedCard) {
                case OpenSpaceCard osc -> {
                    // ((OpenSpaceCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                    // currentAdventureCard.activate();
                    int power = player.getEngineStrenght();
                    listener.onEvent(eventCrafter(GameState.MOVE_FORWARD, power, null));
                    game.getFlightplance().move(power, player);
                    manageCard();
                }
                case SlaversCard sc -> {
                    ((SlaversCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                    currentAdventureCard.activate();
                    int outcome = ((SlaversCard) currentCastedCard).getFightOutcome(currentPlayer);
                    if (outcome == 1) {
                        listener.onEvent(eventCrafter(GameState.ENEMY_WIN, null, null));
                        enemyDefeated = true;
                        int ld = ((SlaversCard) currentCastedCard).getLostDays();
                        listener.onEvent(eventCrafter(GameState.MOVE_PLAYER, ld, null));
                        int c = ((SlaversCard) currentCastedCard).getReward();
                        listener.onEvent(eventCrafter(GameState.GET_CREDITS, c, null));
                        manageCard();
                    } else if (outcome == -1) {
                        listener.onEvent(eventCrafter(GameState.ENEMY_LOST, null, null));
                        defeatedPlayers.add(currentPlayer);
                        manageCard();
                    } else {
                        listener.onEvent(eventCrafter(GameState.ENEMY_DRAW, null, null));
                        manageCard();
                    }
                }
                case SmugglersCard sc -> {
                    ((SmugglersCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                    currentAdventureCard.activate();
                    int outcome = ((SmugglersCard) currentCastedCard).getFightOutcome(currentPlayer);
                    if (outcome == 1) {
                        listener.onEvent(eventCrafter(GameState.ENEMY_WIN, null, null));
                        cargoended = true;
                        int ld = ((SmugglersCard) currentCastedCard).getLostDays();
                        listener.onEvent(eventCrafter(GameState.MOVE_PLAYER, ld, null));
                        listener.onEvent(eventCrafter(GameState.CARGO_MANAGEMENT, null, null));
                    } else if (outcome == -1) {
                        listener.onEvent(eventCrafter(GameState.ENEMY_LOST, null, null));
                        defeatedPlayers.add(currentPlayer);
                        manageCard();
                    } else {
                        listener.onEvent(eventCrafter(GameState.ENEMY_DRAW, null, null));
                        manageCard();
                    }
                }
                case PiratesCard pc -> {
                    ((PiratesCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                    currentAdventureCard.activate();
                    int outcome = ((PiratesCard) currentCastedCard).getFightOutcome(currentPlayer);
                    if (outcome == 1) {
                        listener.onEvent(eventCrafter(GameState.ENEMY_WIN, null, null));
                        piratesended = true;
                        int ld = ((PiratesCard) currentCastedCard).getLostDays();
                        listener.onEvent(eventCrafter(GameState.MOVE_PLAYER, ld, null));
                        int c = ((PiratesCard) currentCastedCard).getReward();
                        listener.onEvent(eventCrafter(GameState.GET_CREDITS, c, null));
                        manageCard();
                    } else if (((PiratesCard) currentCastedCard).getFightOutcome(currentPlayer) == -1) {
                        System.out.println("fromChargeToManage: vado in ENEMY_LOST");
                        listener.onEvent(eventCrafter(GameState.ENEMY_LOST, null, null));
                        defeatedPlayers.add(currentPlayer);
                        manageCard();
                        //defeatedByPirates(currentPlayer);
                    } else {
                        listener.onEvent(eventCrafter(GameState.ENEMY_DRAW, null, null));
                        manageCard();
                    }
                }
                case CombatZoneCard czc -> {

                    for(Player p : disconnectedPlayers) {
                        if(tmpPlayers.contains(p))
                            tmpPlayers.remove(p);
                    }

                    if(tmpPlayers.size() == 1){
                        resetShowAndDraw();
                        return;
                    }

                    if (((CombatZoneCard) currentAdventureCard).getType() == CombatZoneType.LOSTCREW) {

                        if (!combatZoneFlag) {

                            if(player!= null)
                            isDone.put(player,true);

                            if(!isDone.containsValue(false)){
                                System.out.println("fromChargeToManage: combatZoneFlag " + combatZoneFlag);
                                combatZoneFlag = true;
                                isDone.replaceAll((c, v) -> false);
                                System.out.println("fromChargeToManage: mi trovo il minEnginePlayer");
                                int minEngine = players.stream().mapToInt(Player::getEngineStrenght).min().orElse(Integer.MAX_VALUE);
                                List<Player> minEnginePlayers = players.stream().filter(p -> p.getEngineStrenght() == minEngine).collect(Collectors.toList());
                                if (minEnginePlayers.size() == 1) {
                                    Player minEnginePlayer = minEnginePlayers.get(0);
                                    int lostOther = ((CombatZoneCard) currentAdventureCard).getLostOther();
                                    ClientListener l = listenerbyPlayer.get(minEnginePlayer);
                                    handleMinEngine(l);
                                    l.onEvent(eventCrafter(GameState.LOST_CREW, lostOther,null));
                                    //minEnginePlayer.loseCrew(lostOther);
                                    l.onEvent(eventCrafter(GameState.CREW_MANAGEMENT, lostOther, minEnginePlayer));
                                    //sendToCrewManagement(minEnginePlayer);
                                } else {
                                    notifyAllRealListeners(eventCrafter(GameState.SAME_ENGINE, null, null));
                                    combatZoneCannons();
                                }
                                //combatZoneCannons();
                            } else {
                                if(listener != null)
                                    listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                            }
                        } else {

                            if(player!= null)
                            isDone.put(player, true);

                            if (!isDone.containsValue(false)) {
                                afterShots = true;
                                double minFire = players.stream().mapToDouble(Player::getFireStrenght).min().orElse(Integer.MAX_VALUE);
                                System.out.println("minFire: " + minFire);
                                List<Player> minFirePlayers = players.stream().filter(p -> p.getFireStrenght() == minFire).collect(Collectors.toList());
                                if (minFirePlayers.size() == 1) {
                                    Player minFirePlayer = minFirePlayers.get(0);
                                    ClientListener l = listenerbyPlayer.get(minFirePlayer);
                                    handleMinFire(l);
                                    combatZoneShots(minFirePlayer);
                                } else {
                                    notifyAllRealListeners(eventCrafter(GameState.SAME_FIRE, null, null));
                                    System.out.println("fromChargeToManage: vado in resetShowAndDraw");
                                    resetShowAndDraw();
                                }
                            } else {

                                if(listener != null)
                                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                            }
                        }
                    } else {
                        if (!combatZoneFlag) {

                            if(player!= null)
                            isDone.put(player, true);

                            if (!isDone.containsValue(false)) {
                                combatZoneFlag = true;
                                isDone.replaceAll((c, v) -> false);
                                double minFire = players.stream().mapToDouble(Player::getFireStrenght).min().orElse(Integer.MAX_VALUE);
                                List<Player> minFirePlayers = players.stream().filter(p -> p.getFireStrenght() == minFire).collect(Collectors.toList());
                                if (minFirePlayers.size() == 1) {
                                    Player minFirePlayer = minFirePlayers.get(0);
                                    int ld = ((CombatZoneCard) currentAdventureCard).getLostDays();
                                    ClientListener l = listenerbyPlayer.get(minFirePlayer);
                                    handleMinFire(l);
                                    l.onEvent(eventCrafter(GameState.MOVE_PLAYER, ld, null));
                                    game.getFlightplance().move(-ld, minFirePlayer);
                                } else {
                                    notifyAllRealListeners(eventCrafter(GameState.SAME_FIRE, null, null));
                                }
                                combatZoneEngine();

                            } else {
                                if(listener!= null)
                                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                            }
                        } else {
                            if(player != null)
                            isDone.put(player, true);
                            if (!isDone.containsValue(false)) {
                                isDone.replaceAll((c, v) -> false);
                                int minEngine = players.stream().mapToInt(Player::getEngineStrenght).min().orElse(Integer.MAX_VALUE);
                                List<Player> minEnginePlayers = players.stream().filter(p -> p.getEngineStrenght() == minEngine).collect(Collectors.toList());
                                if (minEnginePlayers.size() == 1) {
                                    Player minEnginePlayer = minEnginePlayers.get(0);
                                    ClientListener l = listenerbyPlayer.get(minEnginePlayer);
                                    handleMinEngine(l);
                                    sendToRemoveMVGoods(minEnginePlayer);
                                } else {
                                    notifyAllRealListeners(eventCrafter(GameState.SAME_ENGINE, null, null));
                                    combatZoneLastMinEquip();
                                }
                            } else {
                                if(listener!= null)
                                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                            }
                        }
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + currentCastedCard);
            }
        }
    }

    public void combatZoneLastMinEquip() {

        for(Player p : disconnectedPlayers) {
            if(tmpPlayers.contains(p))
                tmpPlayers.remove(p);
        }

        if(tmpPlayers.size() == 1){
            resetShowAndDraw();
            return;
        }

        int minEquip = tmpPlayers.stream().mapToInt(p -> p.getSpaceshipPlance().getCrew()).min().orElse(Integer.MAX_VALUE);
        List<Player> minEquipPlayers = tmpPlayers.stream().filter(p -> p.getSpaceshipPlance().getCrew() == minEquip).collect(Collectors.toList());
        if (minEquipPlayers.size() == 1) {
            Player minEquipPlayer = minEquipPlayers.get(0);
            ClientListener l = listenerbyPlayer.get(minEquipPlayer);
            handleMinEquip(l);
            combatZoneShots(minEquipPlayer);
        } else {
            notifyAllRealListeners(eventCrafter(GameState.SAME_EQUIP, null, null));
            resetShowAndDraw();
        }
    }

    public void combatZoneCannons() {
        for (Player player: players) {
           ClientListener l= listenerbyPlayer.get(player);
            l.onEvent(eventCrafter(GameState.CHOOSE_CANNON, null, player));
        }
    }

    public void combatZoneEngine() {
        for (Player player: players) {
            ClientListener l= listenerbyPlayer.get(player);
            l.onEvent(eventCrafter(GameState.CHOOSE_ENGINE, null, player));
        }

    }

    public void combatZoneShots(Player minEquipPlayer) {
        Projectile[] shots = ((CombatZoneCard) currentAdventureCard).getCannons();
        int length = shots.length;
        int i = 0;
        while (i < length && shots[i] == null) {
            i++;
        }
        if (i == length || minEquipPlayer == null) {
            System.out.println("combatZoneShots: mando in resetShowAndDraw ");
            resetShowAndDraw();
            return;
        }
        currentProjectile = shots[i];
        shots[i] = null;
        currentDiceThrow = game.throwDices();
        activateMeteor(minEquipPlayer);

    }

    public void defeatedByPirates() {
        Projectile[] projectileArray = ((PiratesCard) currentAdventureCard).getShots();
        int length = projectileArray.length;
        int i = 0;
        while (i < length && projectileArray[i] == null) {
            i++;
        }
        if (i == length || defeatedPlayers.isEmpty()) {
            piratesFlag = false;
            defeatedPlayers.clear();
            resetShowAndDraw();
            return;
        }
        currentProjectile = projectileArray[i];
        projectileArray[i] = null;
        currentDiceThrow = game.throwDices();
        int size = defeatedPlayers.size();
        System.out.println("defeatedPlayers size: " + size);
        /*for(Player p : defeatedPlayers){
            activateMeteor(p);
        }*/
        Player first = defeatedPlayers.get(0);
        System.out.println("defeatedByPirates: mando il primo player in activateMeteor");
        activateMeteor(first);
        if (size >= 2) {
            Player second = defeatedPlayers.get(1);
            System.out.println("defeatedByPirates: mando il secondo player in activateMeteor");
            activateMeteor(second);
            if (size >= 3) {
                Player third = defeatedPlayers.get(2);
                activateMeteor(third);
                if (size == 4) {
                    Player fourth = defeatedPlayers.get(3);
                    activateMeteor(fourth);
                }
            }
        }
    }

    public void defeatedBySlavers() {
        if (defeatedPlayers.isEmpty()) {
            defeatedPlayers.clear();
            resetShowAndDraw();
            return;
        }
        for (Player p: players) {
           ClientListener l= listenerbyPlayer.get(p);
            if (defeatedPlayers.contains(p)) {
                defeatedPlayers.remove(p);
                int lostCrew = ((SlaversCard)currentAdventureCard).getLostCrew();
                l.onEvent(eventCrafter(GameState.CREW_MANAGEMENT, lostCrew, p));
            } else {
                System.out.println("defeatedBySlavers: mando in WAIT_PLAYER");
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }
    }

    public void defeatedBySmugglers() {
        if (defeatedPlayers.isEmpty()) {
            resetShowAndDraw();
            return;
        }
        System.out.println("defeatedBySmugglers: defeatedPlayers size: " + defeatedPlayers.size());
        for (Player player: players) {
           ClientListener l= listenerbyPlayer.get(player);
            Player p = playerbyListener.get(l);
            if (defeatedPlayers.contains(p)) {
                System.out.println("defeatedBySmugglers: defeatedPlayers remove ");
                defeatedPlayers.remove(p);
                sendToRemoveMVGoods(p);
            } else {
                System.out.println("defeatedBySmugglers: mando in WAIT_PLAYER");
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }
    }

    public void sendToRemoveMVGoods(Player p) {
        ClientListener l = listenerbyPlayer.get(p);
        int cardMalus = 0;
        if(currentAdventureCard instanceof SmugglersCard) {
            cardMalus = ((SmugglersCard)currentAdventureCard).getLossMalus();
        } else if (currentAdventureCard instanceof CombatZoneCard) {
            cardMalus = ((CombatZoneCard)currentAdventureCard).getLostOther();
        }

        lastMethodCalled = "sendToRemoveMVGoods";
        System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
        l.onEvent(eventCrafter(GameState.REMOVE_MV_GOODS, cardMalus, p));
        }

    public void waitForEnemies(ClientListener l) {
        Player p = null;

        if(l!=null){
            lastMethodCalled = "waitForEnemies";
            System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
            p = playerbyListener.get(l);
            isDone.put(p, true);
        }

        if (!isDone.containsValue(false)) {
            switch (currentAdventureCard) {
                case SlaversCard sc -> {
                    System.out.println("waitForEnemies: mando in defeatedBySlavers");
                    defeatedBySlavers();
                }
                case SmugglersCard sc -> {
                    System.out.println("waitForEnemies: mando in defeatedBySmugglers");
                    defeatedBySmugglers();
                }
                default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
            }
        } else {
            System.out.println("waitForEnemies: mando in WAIT_PLAYER");
            if(l!=null)
            l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
        }
    }

    public void playerIsDoneCrafting(ClientListener listener){

        lastMethodCalled = "playerIsDoneCrafting";
        System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);

        if(currentGameState == GameState.ASSEMBLY){
            if(listener != null){
                Player player = playerbyListener.get(listener);
                donecraftingPlayers.add(player);
                isDone.put(player, true);
                if (!isDone.containsValue(false))
                    handleCraftingEnded();
                else if(donecraftingPlayers.size() == 1)
                    listener.onEvent(eventCrafter(GameState.WAIT_PLAYER_LEADER, null, null));
                else
                    listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }else{
                if (!isDone.containsValue(false) || (lobby.getNumPlayers() == (donecraftingPlayers.size() + disconnectedPlayers.size())))
                    handleCraftingEnded();
            }

        }else {
            // it means I am entering here from restorePlayers
            if (listener != null) {
                Player player = playerbyListener.get(listener);
                isDone.put(player, true);
                players.add(player);
                if (!isDone.containsValue(false))
                    resetIsDoneDraw();
                else
                    listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            } else {
                if (!isDone.containsValue(false))
                    resetIsDoneDraw();
            }
        }
    }

    private void handleCraftingEnded() {

        currentGameState = GameState.CRAFTING_ENDED;
        int pos = 0;
        ArrayList<Player> disconnected = new ArrayList<>(disconnectedPlayers);


        // it means he disconnected after submitting isDonecrafting
        for(Player player: donecraftingPlayers){
            System.out.println("player " + player);
            if(disconnected.contains(player))
                disconnected.remove(player);
        }

        int ndisconnected = disconnected.size();

        while (!donecraftingPlayers.isEmpty()) {
            Player player = donecraftingPlayers.getFirst();
            pos = ndisconnected + donecraftingPlayers.size();
            pos--;
            int realpos;

            switch (pos) {
                case 0 -> realpos = 0;
                case 1 -> realpos = 1;
                case 2 -> realpos = 3;
                case 3 -> realpos = 6;
                default -> realpos = 7;
            }

            Flightplance flightPlance = game.getFlightplance();
            Placeholder placeholder = flightPlance.getPlaceholderByPlayer(player);
            placeholder.setPosizione(realpos);
            placeholders.add(placeholder);

            if(!disconnectedPlayers.contains(player)){
                ClientListener listener = listenerbyPlayer.get(player);
                String playerColor = flightPlance.getPlaceholderByPlayer(player).getColor().name();
                listener.onEvent(eventCrafter(GameState.PLAYER_COLOR, playerColor, null));
            }

            System.out.println("position: " + realpos);

            donecraftingPlayers.removeFirst();
        }

        while (!disconnected.isEmpty()) {
            Player player = disconnected.getFirst();
            pos = disconnected.size();
            pos--;
            int realpos;

            switch (pos) {
                case 0 -> realpos = 0;
                case 1 -> realpos = 1;
                case 2 -> realpos = 3;
                case 3 -> realpos = 6;
                default -> realpos = 7;
            }

            System.out.println("position: " + realpos + "disconnectednick " + player.getNickname());

            Flightplance flightPlance = game.getFlightplance();
            Placeholder placeholder = flightPlance.getPlaceholderByPlayer(player);
            placeholder.setPosizione(realpos);
            placeholders.add(placeholder);

            disconnected.removeFirst();
        }

        notifyAllRealListeners(eventCrafter(GameState.CRAFTING_ENDED, null, null));


        synchronized (isDone) {
            for (Player player: players) {
                isDone.put(player, false);
            }
        }


        boolean allOk = true;
        for (Player p : players) {
            ClientListener l = listenerbyPlayer.get(p);
            if (!p.getSpaceshipPlance().checkCorrectness()) {
                printSpaceshipAdjustment(l);
                allOk = false;
            } else {
                p.getSpaceshipPlance().updateLists();
                isDone.put(p, true);
                printSpaceship(l);
            }
        }

        if (allOk) {
            chooseAliens();
        } else { // for each already done client I send state to wait for the ones who aren't done cause they have to adjust
            isDone.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .forEach(entry -> {
                        Player p = entry.getKey();
                        ClientListener l = listenerbyPlayer.get(p);
                        l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                    });
        }

    }

    private String[] handleBoardView() {

        String[] boardView = new String[24];
        Arrays.fill(boardView, "[]");


        // 3) Li “sparo” nella board in base alla loro posizione
        for (Placeholder p : placeholders ) {
            int pos = (p.getPosizione()) % 24;
            if (pos < 0) {
                pos = pos + 24;
            }
            // prendo solo la prima lettera di ogni enum
            boardView[pos] = ("[" + p.getColor().name().charAt(0) + "]");

        }
        return boardView;
    }

    public void checkStorage(ClientListener listener) throws CargoManagementException {
        Player player = playerbyListener.get(listener);
        if (player.getSpaceshipPlance().checkStorage()) {
            ArrayList<CargoHolds> playerCargos = player.getSpaceshipPlance().getCargoHolds();
            GoodsBlock[] playerReward = player.getReward();
            // Creazione della lista di GoodsContainer
            ArrayList<GoodsContainer> goodsContainers = new ArrayList<>();

            goodsContainers.add(new GoodsContainer(playerReward, true, -1));

            for (CargoHolds cargo : playerCargos) {
                GoodsBlock[] goods = cargo.getGoods();
                goodsContainers.add(new GoodsContainer(goods, cargo.isSpecial(), cargo.getId()));
            }

            player.getSpaceshipPlance().setGoodsContainers(goodsContainers);

            if(!playerCargos.isEmpty())
            printSpaceshipbyTile(listener, playerCargos.getFirst());

            listener.onEvent(new Event(GameState.CARGO_VIEW, new Cargos(goodsContainers)));
        } else {
            // qua ci sarebbe da gestire se siamo in planets quindi devi aspettare altri oppure in un reward generico quindi lui gestisce e finisce il turno per tutti...
            // separiamo i casi per ogni tipo di carta per vedere se termina subito o passa agli altri player
            try {
                throw new CargoManagementException("You got 0 storage space, you can't manage any good");
            } finally {
                endCargoManagement(listener);
            }
        }
    }

    public void checkStorageOk(ClientListener listener) throws CargoManagementException {
        Player player = playerbyListener.get(listener);
        if (!player.getSpaceshipPlance().checkStorage()) {
            throw new CargoManagementException("You got 0 storage space, you can't manage any good");
        }
    }

    private void printSpaceshipbyTile(ClientListener listener, ComponentTile tile) {
        Player player = playerbyListener.get(listener);
        String complete_ship = player.getSpaceshipPlance().reserveSpotToString() + "\n" + player.getSpaceshipPlance().tileGridToStringTile(tile);
        DataString ds = new DataString(complete_ship);
        listener.onEvent(eventCrafter(GameState.BYTILE_SHIP, ds, null));
    }

    public void addGood(ClientListener listener, int cargoIndex, int goodIndex, int rewardIndex) throws CargoManagementException {
        Player player = playerbyListener.get(listener);
        game.addGood(player, cargoIndex, goodIndex, rewardIndex);
        checkStorage(listener);
    }

    public void swapGoods(ClientListener listener, int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) throws CargoManagementException {
        Player player = playerbyListener.get(listener);
        game.swapGoods(player, cargoIndex1, cargoIndex2, goodIndex1, goodIndex2);
        checkStorage(listener);
    }

    public void removeGood(ClientListener listener, int cargoIndex, int goodIndex) throws CargoManagementException {
        Player player = playerbyListener.get(listener);
        game.removeGood(player, cargoIndex, goodIndex);
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
        for (ClientListener c : realListeners) {
            Player player = playerbyListener.get(c);
            DataString ds = new DataString(player.getSpaceshipPlance().tileGridToString(), player.getSpaceshipPlance().getTileIds());
            c.onEvent(eventCrafter(GameState.SHOW_SHIP, ds, null));
        }
    }

    public void printSpaceship(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        String complete_ship = player.getSpaceshipPlance().reserveSpotToString() + "\n" + player.getSpaceshipPlance().tileGridToStringAdjustments();
        DataString ds = new DataString(complete_ship, player.getSpaceshipPlance().getTileIds());
        listener.onEvent(eventCrafter(GameState.SHOW_SHIP, ds, null));
    }

    public void printSpaceshipAdjustment(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        String complete_ship = player.getSpaceshipPlance().tileGridToStringAdjustments();
        lastMethodCalled = "printSpaceshipAdjustment";
        System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
        DataString ds = new DataString(complete_ship, player.getSpaceshipPlance().getTileIds());
        listener.onEvent(eventCrafter(GameState.ADJUST_SHIP, ds, null));
    }

    public void endCard() {
        for (Player p : players) {
            p.getSpaceshipPlance().updateLists();
            ClientListener listener = listenerbyPlayer.get(p);
            listener.onEvent(eventCrafter(GameState.SHOW_PLAYER, null, p));
        }
    }

    public void charge(ClientListener listener, int i) throws ControllerExceptions {
        Player player = playerbyListener.get(listener);
        ArrayList<Engine> engines = player.getSpaceshipPlance().getEngines();
        ArrayList<DoubleEngine> doubleEngines = new ArrayList<>();
        for (Engine e : engines) {
            if (e instanceof DoubleEngine) {
                doubleEngines.add((DoubleEngine) e);
            }
        }
        int batteries = player.getSpaceshipPlance().getnBatteries();
        if (i < 0 || i > doubleEngines.size()) {
            throw new ControllerExceptions("You selected a wrong double engines number");
        } else if (i > batteries) {
            throw new ControllerExceptions("You don't have enough batteries");
        } else {
            for (int j = 0; j < i; j++) {
                doubleEngines.get(j).setCharged(true);
            }
            listener.onEvent(eventCrafter(GameState.BATTERIES_MANAGEMENT, i, player));
        }
    }

    public void chargeCannons(ClientListener listener, ArrayList<Integer> chosenIndices) throws ControllerExceptions {
        Player player = playerbyListener.get(listener);
        ArrayList<Cannon> cannons = player.getSpaceshipPlance().getCannons();
        ArrayList<DoubleCannon> doubleCannons = new ArrayList<>();
        for (Cannon c : cannons) {
            if (c instanceof DoubleCannon) {
                doubleCannons.add((DoubleCannon) c);
            }
        }
        int batteries = player.getSpaceshipPlance().getnBatteries();
        if (chosenIndices.size() > batteries)
            throw new ControllerExceptions("You don't have enough batteries, type 0 to continue");
        for (Integer i : chosenIndices) {
            if (i < 0 || i >= doubleCannons.size()) {
                throw new ControllerExceptions("You selected a wrong chosen cannons number, type 0 to skip or 1 to charge");
            } else {
                doubleCannons.get(i).setCharged(true);
            }
        }

        listener.onEvent(eventCrafter(GameState.BATTERIES_MANAGEMENT, chosenIndices.size(), player));
    }

    public void choosePlanets(ClientListener listener, int i) throws CargoManagementException {
        Player player = playerbyListener.get(listener);
        PlanetsCard currentPlanetsCard = (PlanetsCard) currentAdventureCard;
        ArrayList<Planet> planets = currentPlanetsCard.getPlanets();

        if (i < 0 || i > planets.size() - 1) {
            throw new ControllerExceptions("You selected a wrong planet number");
        } else if (planets.get(i).isBusy()) {
            throw new ControllerExceptions("The chosen planet is busy");
        } else if (!player.getSpaceshipPlance().checkStorage()) {
            try {
                throw new CargoManagementException("You got 0 storage space, you can't manage any good");
            }finally {
                handlePlanets(listener);
            }
        }

        // Se tutto va bene
        Planet planet = planets.get(i);
        planet.setBusy(true);
        currentPlanetsCard.setActivatedPlayer(player);
        currentPlanetsCard.setChosenPlanet(planet);
        int ld = ((PlanetsCard) currentAdventureCard).getLostDays();
        listener.onEvent(eventCrafter(GameState.MOVE_PLAYER, ld, null));
        currentPlanetsCard.activate();
        listener.onEvent(eventCrafter(GameState.CARGO_MANAGEMENT, null, null));
    }

    public void putTileBack(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        ComponentTile tile = player.getHandTile();
        ComponentTile[] tiles = game.getAssemblingTiles();
        tiles[tile.getId()] = tile;
        player.setHandTile(null);
        listener.onEvent(eventCrafter(GameState.ASSEMBLY, null, player));
    }


    public void addReserveSpot(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        ComponentTile tile = player.getHandTile();

        if (player.getSpaceshipPlance().getReserveSpot().size() >= 2) {
            listener.onEvent(eventCrafter(GameState.FULL_RESERVE_SPOT, null, null));
            putTileBack(listener);
        } else {
            player.getSpaceshipPlance().addReserveSpot(tile);
            player.setHandTile(null);
            printSpaceship(listener);
            listener.onEvent(eventCrafter(GameState.ASSEMBLY, null, player));
        }

    }

    public void endCargoManagement(ClientListener listener) {

        if(listener!=null){
            Player player = playerbyListener.get(listener);
            //tmpPlayers.remove(player);
            player.setReward(null);
            cargoended = true;
            System.out.println("Cargo management ended");

            // reset GoodsContainers as default one, without reward cargo
            ArrayList<CargoHolds> playerCargos = player.getSpaceshipPlance().getCargoHolds();
            ArrayList<GoodsContainer> goodsContainers = new ArrayList<>();
            for (CargoHolds cargo : playerCargos) {
                GoodsBlock[] goods = cargo.getGoods();
                goodsContainers.add(new GoodsContainer(goods, cargo.isSpecial(), cargo.getId()));
            }
            player.getSpaceshipPlance().setGoodsContainers(goodsContainers);
        }


        if(currentAdventureCard instanceof PlanetsCard){
            handlePlanets(listener);
        }
        else
            manageCard();
    }


    public void rotateClockwise(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        ComponentTile tile = player.getHandTile();
        tile.rotateClockwise();
        printSpaceship(listener);
        listener.onEvent(eventCrafter(GameState.PICKED_TILE, tile, player));
    }

    public void removeAdjust(ClientListener listener, int xIndex, int yIndex) throws SpaceShipPlanceException {

        Player player = playerbyListener.get(listener);
        int stumps = player.getSpaceshipPlance().remove(xIndex, yIndex);
        // means the method is invoked at the beginning of the game
        if (currentAdventureCard == null) {
            if (stumps <= 1) {
                if (player.getSpaceshipPlance().checkCorrectness()) {
                    player.getSpaceshipPlance().updateLists();
                    isDone.put(player, true);
                    printSpaceship(listener);
                    if (handleAdjustmentEnded())
                        chooseAliens();
                    else
                        listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                } else
                    printSpaceshipAdjustment(listener);
            }
            // se non c'è più di un troncone, faccio un check di correttezza: se è ok, allora sono apposto altrimenti ritorno nello stato di ShipAdjustment
            else {
                printSpaceshipParts(listener);
            }
        } else {
            if (stumps <= 1) {
                if( currentGameState == GameState.TURN_START){
                    isDone.put(player,true);
                    if (handleAdjustmentEnded())
                        resetIsDoneDraw();
                    else
                        listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                }else{
                    switch(currentAdventureCard) {
                        case CombatZoneCard czc -> {
                            Player p = null;
                            if(listener != null)
                                p = playerbyListener.get(listener);
                            System.out.println("removeAdjust: vado in combatZoneShots");
                            combatZoneShots(p);
                        }
                        case MeteorSwarmCard msc -> waitForNextShotMeteor(listener);
                        case PiratesCard pc -> waitForNextShotPirates(listener);
                        default -> throw new IllegalStateException();
                    }
                }
            } else {
                if(listener != null) {
                    System.out.println("removeAdjust: vado in printSpaceshipParts");
                    printSpaceshipParts(listener);
                } else {
                    if (handleAdjustmentEnded())
                        resetIsDoneDraw();
                }
            }
        }
    }

    private void printSpaceshipParts(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        String complete_ship = player.getSpaceshipPlance().tileGridToStringParts();
        lastMethodCalled = "printSpaceshipParts";
        System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
        DataString ds = new DataString(complete_ship, player.getSpaceshipPlance().getTileIds());
        listener.onEvent(eventCrafter(GameState.SELECT_SHIP, ds, null));
    }

    public void selectShipPart(ClientListener listener, int part) {
        Player p = playerbyListener.get(listener);
        p.getSpaceshipPlance().selectPart(part);
        if (currentAdventureCard == null) {
            if (!p.getSpaceshipPlance().checkCorrectness()) {
                printSpaceshipAdjustment(listener);
            } else {
                p.getSpaceshipPlance().updateLists();
                isDone.put(p, true);
                printSpaceship(listener);
                if (handleAdjustmentEnded())
                    chooseAliens();
                else
                    listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        } else {
            if (!p.getSpaceshipPlance().checkCorrectness()) {
                printSpaceshipAdjustment(listener);
            } else {
                if( currentGameState == GameState.TURN_START){
                    isDone.put(playerbyListener.get(listener),true);
                    if (handleAdjustmentEnded())
                        resetIsDoneDraw();
                    else
                        listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                }else {
                    switch (currentAdventureCard) {
                        case CombatZoneCard czc -> {
                            System.out.println("selectShipPart: vado in combatZoneShots");
                            combatZoneShots(p);
                        }
                        case MeteorSwarmCard msc -> waitForNextShotMeteor(listener);
                        case PiratesCard pc -> waitForNextShotPirates(listener);
                        default -> {
                            isDone.put(playerbyListener.get(listener), true);
                            if (handleAdjustmentEnded())
                                resetIsDoneDraw();
                            else
                                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                        }
                    }
                }
                /*if(currentAdventureCard instanceof MeteorSwarmCard){
                    waitForNextShot(listener);
                } else {
                    waitForNextShotPirates(listener);
                }*/
                // waitForNextShot(listener);
            }
        }
    }

    private void chooseAliens() {

        isDone.replaceAll((c, v) -> false);


        for (Player p: players) {
           ClientListener l= listenerbyPlayer.get(p);
            ArrayList<Cabin> cabins = p.getSpaceshipPlance().getCabins();
            ArrayList<CabinAliens> cabinAliens = new ArrayList<>();
            boolean atLeastOneSupport = false;
            for (Cabin c : cabins) {
                boolean brown = false;
                boolean purple = false;
                System.out.println(c);
                AlienColor[] lifeSupportSystemColors = c.getLifeSupportSystemColor();
                System.out.println(Arrays.toString(lifeSupportSystemColors));
                if (Arrays.stream(lifeSupportSystemColors)
                        .anyMatch(s -> s == AlienColor.BROWN)) {
                    brown = true;
                    atLeastOneSupport = true;
                }

                if (Arrays.stream(lifeSupportSystemColors)
                        .anyMatch(s -> s == AlienColor.PURPLE)) {
                    purple = true;
                    atLeastOneSupport = true;
                }

                // if there is at least one support connected to this cabin I push it
                if (atLeastOneSupport)
                    cabinAliens.add(new CabinAliens(c, brown, purple));
            }


            if (!cabinAliens.isEmpty()) {
                printSpaceshipbyTile(l, cabinAliens.getFirst().getCabin());
                l.onEvent(eventCrafter(GameState.CHOOSE_ALIEN, cabinAliens, null));
            } else {
                isDone.put(p, true);
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }

        // if everyone went to waitPlayer, so isDone is all true
        if (!isDone.containsValue(false)) {
            restoreReconnectedPlayers();
        }

    }

    public void playerHit(ClientListener listener) {
        Direction direction = currentProjectile.getDirection();
        System.out.println("playerHit: mando in takeHit");
        takeHit(playerbyListener.get(listener), direction, currentDiceThrow);
    }

    public void playerProtected(ClientListener listener) throws ControllerExceptions {
        Player p = playerbyListener.get(listener);
        // togliere una batteria dato che ha attivato uno scudo o un doppio cannone
        Player player = playerbyListener.get(listener);
        int batteries = player.getSpaceshipPlance().getnBatteries();
        if (batteries > 0) {
            listener.onEvent(eventCrafter(GameState.BATTERIES_MANAGEMENT, 1, player));
        } else {
            try {
                throw new ControllerExceptions("Batteries not enough to protect");
            } finally {
                playerHit(listener);
            }
        }
    }

    public void waitForNextShotMeteor(ClientListener listener) {
        if(listener!=null){
            lastMethodCalled = "waitForNextShot";
            System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
            Player p = playerbyListener.get(listener);
            isDone.put(p, true);
        }

        if (!isDone.containsValue(false)) {
            isDone.replaceAll((c, v) -> false);
            manageCard();
        } else {
            if(listener!=null)
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
        }
    }

    public void waitForNextShotPirates(ClientListener listener) {
        if(listener!=null){
            lastMethodCalled = "waitForNextShotPirates";
            System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
            Player p = playerbyListener.get(listener);
            isDonePirates.put(p, true);
        }

        if (!isDonePirates.containsValue(false)){
            isDonePirates.replaceAll((c, v) -> false);
            defeatedByPirates();
        } else {
            if(listener!=null)
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
        }
    }

    public boolean addAlienCabin(ClientListener listener, int cabinId, String alienColor) {
        Player p = playerbyListener.get(listener);
        ArrayList<Cabin> cabins = p.getSpaceshipPlance().getCabins();
        for (Cabin c : cabins) {
            if (c.getId() == cabinId) {
                AlienColor[] colors = c.getLifeSupportSystemColor();
                if (Objects.equals(alienColor, "b")) {
                    if (colors[AlienColor.BROWN.ordinal()] != null) {
                        Figure[] figures = c.getFigures();
                        figures[0] = new Alien(1, AlienColor.BROWN);
                        figures[1] = null;
                        return true;
                    }
                }

                if (Objects.equals(alienColor, "p")) {
                    if (colors[AlienColor.PURPLE.ordinal()] != null) {
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
        if(listener!=null){
            lastMethodCalled = "handleEndChooseAliens";
            System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
            Player p = playerbyListener.get(listener);
            isDone.put(p, true);
        }

        // player who didn't have cabins to put aliens in or finished they alien chosen have isDone = true
        if (!isDone.containsValue(false))
            restoreReconnectedPlayers();
        else{
            if(listener!=null)
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
        }
    }

    public boolean removeFigure(ClientListener listener, int cabinId) {
        Player p = playerbyListener.get(listener);
        ArrayList<Cabin> cabins = p.getSpaceshipPlance().getCabins();
        for (Cabin c : cabins) {
            if (c.getId() == cabinId) {
                Figure[] figures = c.getFigures();

                if (figures[1] != null) {
                    figures[1] = null;
                    return true;
                } else if (figures[0] != null) {
                    figures[0] = null;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeFigureEpidemic(ClientListener listener, int cabinId) {
        Player p = playerbyListener.get(listener);
        ArrayList<Cabin> interconnectedCabins = new ArrayList<> (p.getSpaceshipPlance().getInterconnectedCabins());

        System.out.println("sono entrato in removeFigureEpidemic " + p);

        for(Cabin cabin: interconnectedCabins)
            System.out.println(cabin + "removeFigureEpidemic");

        for (Cabin c : interconnectedCabins) {
            if (c.getId() == cabinId) {
                Figure[] figures = c.getFigures();

                if (figures[1] != null) {
                    figures[1] = null;
                    p.getSpaceshipPlance().removeInterconnectedCabin(c);

                    for(Cabin cabin: interconnectedCabins)
                        System.out.println(cabin + "removeFigureEpidemic");

                    return true;
                } else if (figures[0] != null) {
                    figures[0] = null;
                    p.getSpaceshipPlance().removeInterconnectedCabin(c);

                    for(Cabin cabin: interconnectedCabins)
                        System.out.println(cabin + "removeFigureEpidemic");

                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEpidemicDone(ClientListener listener) {
        Player p = playerbyListener.get(listener);
        System.out.println("sono entrato in isEpidemicDone " + p);
        return p.getSpaceshipPlance().checkInterconnectedCabinsEmpty();
    }




    private void checkEarlyEndConditions() {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getSpaceshipPlance().getnAstronauts() == 0 ||
                    players.getLast().getPlaceholder().getPosizione() > player.getPlaceholder().getPosizione() + 18) {
                System.out.println("Player early condition checked " + player);
                iterator.remove();
                handleEarlyEnd(player);
            }
        }
    }

    private void endTurn() {
        checkEarlyEndConditions();
        if (players.isEmpty()) {
            for(Player player: reconnectedPlayers)
                realListeners.add(listenerbyPlayer.get(player));
            notifyAllRealListeners(eventCrafter(GameState.END_GAME, null, null));
        } else {
            isDone.replaceAll((c, v) -> false);
            for (Player player : players) {
                ClientListener listener = listenerbyPlayer.get(player);
                listener.onEvent(eventCrafter(GameState.ASK_SURRENDER, null, null));
            }
        }
    }

    private boolean handleAdjustmentEnded() {

        Map<String, Boolean> nicknameMap = isDone.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getNickname(),
                        Map.Entry::getValue
                ));

        nicknameMap.forEach((nickname, done) ->
                System.out.println(nickname + " -> " + done)
        );

        return !isDone.containsValue(false);
    }

    public void handleSurrenderEnded(ClientListener listener) {

        if(listener!=null){
            lastMethodCalled = "handleSurrenderEnded";
            System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
            Player p = playerbyListener.get(listener);
            synchronized (isDone) {
                isDone.put(p, true);
            }
        }

        synchronized (isDone) {
            if (!isDone.containsValue(false))
                restoreReconnectedPlayers();
            else {
                if(listener!=null)
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }
    }

    public void surrender(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        isDone.remove(player);
        players.remove(player);
        player.setSurrended(true);
        listener.onEvent(eventCrafter(GameState.DIED, null, null));
    }

    public boolean removeBatteries(ClientListener listener, int powerCenterId, int batteries) {

        Player p = playerbyListener.get(listener);
        ArrayList<PowerCenter> powerCenters = p.getSpaceshipPlance().getPowerCenters();

        for (PowerCenter pc : powerCenters) {
            if (pc.getId() == powerCenterId) {
                boolean[] pcBatteries = pc.getBatteries();
                int available = 0;

                for (boolean b : pcBatteries) {
                    if (b) available++;
                }

                if (available < batteries) return false;

                // rimuovo da sx a dx
                int removed = 0;
                for (int i = 0; i < pcBatteries.length && removed < batteries; i++) {
                    if (pcBatteries[i]) {
                        pcBatteries[i] = false;
                        removed++;
                    }
                }

                return true;
            }
        }

        return false;
    }

    public void endManagement(ClientListener listener) {


        Player p = playerbyListener.get(listener);
        p.getSpaceshipPlance().updateLists();

        switch(currentAdventureCard){
            case CombatZoneCard czc -> {
                if(!combatZoneFlag || !afterShots)
                    fromChargeToManage(listener);
                else
                    combatZoneShots(p);
            }
            case MeteorSwarmCard msc -> waitForNextShotMeteor(listener);
            case OpenSpaceCard osc -> fromChargeToManage(listener);
            case PiratesCard pc -> {
                if (piratesFlag)
                    waitForNextShotPirates(listener);
                else
                    fromChargeToManage(listener);
            }
            case SlaversCard sc -> fromChargeToManage(listener);
            case SmugglersCard sc -> {
                if (smugglersFlag)
                    waitForEnemies(listener);
                else
                    fromChargeToManage(listener);
            }
            default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
        }
    }

    public void endCrewManagement(ClientListener listener) {
        switch (currentAdventureCard) {
            case SlaversCard sc -> waitForEnemies(listener);
            // controllare a cosa far tornare per asc e czc
            case AbandonedShipCard asc -> manageCard();
            case CombatZoneCard czc -> combatZoneCannons();
            case EpidemicCard ep -> handleEpidemic(listener);
            default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
        }
    }

    public void handleEpidemic(ClientListener listener) {
        if(listener!=null){
            lastMethodCalled = "handleEpidemic";
            System.out.println("Stampa temporanea: lastMethodCalled " + lastMethodCalled);
            Player p = playerbyListener.get(listener);
            isDone.put(p, true);
        }

        if(!isDone.containsValue(false)){
            resetShowAndDraw();
        } else {
            if(listener!=null)
            listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
        }
    }

    public void fromMvGoodstoBatteries(ClientListener listener, int nBatteries) {
        if(nBatteries > 0)
        listener.onEvent(eventCrafter(GameState.REMOVE_EXTRA_BATTERIES, nBatteries, playerbyListener.get(listener)));
        else
            endMVGoodsManagement(listener);
    }

    public void endMVGoodsManagement(ClientListener listener) {
        if(listener!= null){
            Player player = playerbyListener.get(listener);
            player.getSpaceshipPlance().updateLists();
        }
        if (currentAdventureCard instanceof SmugglersCard) {
            waitForEnemies(listener);
        } else if (currentAdventureCard instanceof CombatZoneCard) {
            combatZoneLastMinEquip();
        }
    }

    public boolean removeMVGood(ClientListener listener, int cargoIndex, int goodIndex) throws CargoManagementException {
        Player player = playerbyListener.get(listener);
        return player.getSpaceshipPlance().removeMVGood(cargoIndex, goodIndex);
    }

    public void takeHit(Player p, Direction direction, int position) {
        // cammini partendo dalla casella indicata verso il centro
        // appena trovi un componente lo rimuovi
        // aggiungere prima i check per la posizione
        ComponentTile[][] components = p.getSpaceshipPlance().getComponents();
        int max_lenght = 7;

        ClientListener l = null;
        if(!disconnectedPlayers.contains(p))
            l=listenerbyPlayer.get(p);

        System.out.println("position " + position);
        // casella da cui partire
        int x = 0, y = 0;
        switch (direction) {
            case NORTH:
                x = position - 4;
                y = 0;
                max_lenght = 5;
                break;
            case EAST:
                x = 6;
                y = position - 5;
                break;
            case SOUTH:
                x = position - 4;
                y = 4;
                max_lenght = 5;
                break;
            case WEST:
                x = 0;
                y = position - 5;
                break;
        }

        System.out.println("y " + y + " x " + x);

        ComponentTile hit = null;

        if (inBounds(x, y))
            hit = components[y][x];


        for (int i = 0; i < max_lenght && hit == null; i++) {
            switch (direction) {
                case NORTH:
                    y += 1;
                    break;
                case EAST:
                    x -= 1;
                    break;
                case SOUTH:
                    y -= 1;
                    break;
                case WEST:
                    x += 1;
                    break;
            }

            if (inBounds(x, y))
                hit = components[y][x];
        }

        if (hit != null) {
            if(l!=null){
                l.onEvent(eventCrafter(GameState.SHOT_HIT, null, null));
                removeAdjust(l, x, y);
            }
        } else {
            if(l!=null)
                l.onEvent(eventCrafter(GameState.NO_HIT, null, null));

            switch(currentAdventureCard) {
                case CombatZoneCard czc -> {
                    System.out.println("takeHit: vado in combatZoneShots");
                    combatZoneShots(p);
                }
                case MeteorSwarmCard msc -> waitForNextShotMeteor(l);
                case PiratesCard pc -> waitForNextShotPirates(l);
                default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
            }
        }
    }

    public void orderPlayers() {
        players.sort(Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
    }

    private boolean inBounds(int x, int y) {
        // Prima controlla i bound standard
        boolean standardBounds = (x >= 0 && x < 7 && y >= 0 && y < 5);

        // Poi verifica gli edge case specifici della forma
        return standardBounds && !edgeCases(y, x);
    }

    private boolean edgeCases(int y, int x) {
        if (y == 0) {
            return x == 0 || x == 1 || x == 3 || x == 5 || x == 6;
        } else if (y == 1) {
            return x == 0 || x == 6;
        } else if (y == 4) {
            return x == 3;
        }
        return false;
    }

    public void showDecks(ClientListener listener) {
        listener.onEvent(eventCrafter(GameState.SHOW_DECKS, null, null));
    }

    public boolean showCardsbyDeck(ClientListener listener, int nDeck) {
        synchronized (busyDecks) {
            if (busyDecks[nDeck - 1]) {
                return false;
            }
        }

        listener.onEvent(eventCrafter(GameState.SHOW_CARDS, nDeck, null));
        busyDecks[nDeck - 1] = true;
        return true;
    }

    public void endShowCards(ClientListener listener, int nDeck) {

        if(nDeck != -1){
            synchronized (busyDecks) {
                busyDecks[nDeck - 1] = false;
            }
        }

        listener.onEvent(eventCrafter(GameState.ASSEMBLY, null, playerbyListener.get(listener)));
    }

    public boolean startTimer() {

        Timer timer = game.getTimer();

        if(timer.isDone())
            timer.reset();
        else
            return false;


        new Thread(() -> {
            while (!timer.isDone()) {
                System.out.println("Timer: " + timer.getRemainingTime() + " seconds");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException");
                    return;
                }
            }
            System.out.println("Timer done");

                if(currentGameState == GameState.ASSEMBLY){
                    notifyAllRealListeners(eventCrafter(GameState.TIMER_DONE, null, null));

                    for(ClientListener listener: realListeners) {
                        if(!isDone.get(playerbyListener.get(listener))){
                            try {
                                playerIsDoneCrafting(listener);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                }
            }
        }).start();

        return true;

    }

    public int[] guiBoardInfo() {

        int i = 0;
        int size = placeholders.size() * 2;
        int[] infos = new int[size];
        for(Placeholder p: placeholders){
                infos[i] = p.getColor().getValue();
                i++;
                int pos = (p.getPosizione()) % 24;
                if (pos < 0) {
                    pos = pos + 24;
                }
                infos[i] = pos;
                i++;
        }
        return infos;
    }

    public HashMap<String,Integer> guiPlayersColors(){
        HashMap<String,Integer> playerColor = new HashMap<>();
        for(Player player: players){
            playerColor.put(player.getNickname(), player.getPlaceholder().getColor().getValue());
        }
        return playerColor;
    }

    public void handleDisconnect(ClientListenerRmi listener) {
        if(currentGameState == GameState.LOBBY_PHASE){
            lobby.removePlayerNickname(registredListeners.get(listener));
            registredListeners.remove(listener);
            realListeners.remove(listener);

            for (ClientListener l : registredListeners.keySet()) {
                l.onEvent(eventCrafter(GameState.WAIT_LOBBY, null, null));
            }

            return;
        }
        Player disconnectedPlayer = playerbyListener.get(listener);

        disconnectedPlayers.add(disconnectedPlayer);
        players.remove(disconnectedPlayer);
        playerbyListener.remove(listener);
        listenerbyPlayer.put(disconnectedPlayer,null);
        defeatedPlayers.remove(disconnectedPlayer);
        realListeners.remove(listener);

        if(players.size() != 1){
            // the last who reconnects after pause has the same status as before
            isDone.remove(disconnectedPlayer);
            isDonePirates.remove(disconnectedPlayer);
            checklastMethodCalled(disconnectedPlayer);
        }
    }

    private void checklastMethodCalled(Player disconnectedPlayer) {
        if(lastMethodCalled == null)
            return;

        System.out.println("Stampa metodo: lastMethodCalled " + lastMethodCalled);

        switch (lastMethodCalled) {
            case "handlePlanets":
                handlePlanets(null);
                break;
            case "fromChargeToManage", "handleWaitersBattery","handleWaitersEnemy":
                fromChargeToManage(null);
                break;
            case "waitForEnemies":
                waitForEnemies(null);
                break;
            case "playerIsDoneCrafting","printSpaceshipAdjustment","printSpaceshipParts":
                playerIsDoneCrafting(null);
                break;
            case "waitForNextShot":
                waitForNextShotMeteor(null);
                break;
            case "waitForNextShotPirates":
                waitForNextShotPirates(null);
                break;
            case "handleEndChooseAliens":
                handleEndChooseAliens(null);
                break;
            case "handleSurrenderEnded":
                handleSurrenderEnded(null);
                break;
            case "handleEpidemic":
                handleEpidemic(null);
                break;
            case "handleWaitersPlayer":
                manageCard();
                break;
            case "handleWaitersPlanets":
                endCargoManagement(null);
                break;
            case "sendToRemoveMVGoods":
                endMVGoodsManagement(null);
            case "checkProtection":
                takeHit(disconnectedPlayer,currentProjectile.getDirection(),currentDiceThrow);
            case "crewManagement":
                endCrewManagement(null);
            case "epidemicManagement":
                handleEpidemic(null);

            default:
                break;
        }
        return;
    }

    public void handleReconnect(ClientListenerRmi listener, String nickname) {
        Optional<Player> optionalPlayer = disconnectedPlayers.stream()
                .filter(player -> player.getNickname().equals(nickname))
                .findFirst();

        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();


            listenerbyPlayer.put(player, listener);
            playerbyListener.put(listener, player);
            disconnectedPlayers.remove(player);
            reconnectedPlayers.add(player);

            System.out.println("Player " + nickname + " reconnected.");

            if(currentGameState == GameState.ASSEMBLY){
                players.add(player);
                realListeners.add(listenerbyPlayer.get(player));
                isDone.put(player, false);
                isDonePirates.put(player, false);
                reconnectedPlayers.remove(player);
                listener.onLastEvent();
            }

        } else {
            System.out.println("No matching disconnected player for nickname: " + nickname);
        }
    }

    public void pause() {
        final long timeout = 30000;
        final long startTime = System.currentTimeMillis();
        System.out.println("wonGameForDisconessions timer set: " + timeout + " ms");

        while (pause) {
            long currentTimeMillis = System.currentTimeMillis() - startTime;
            System.out.println("wonGameForDisconessions timer: " + currentTimeMillis + " ms");
            if (currentTimeMillis >= timeout) {
                System.out.println("Timer expired");
                wonGameForDisconessions();
                return;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void wonGameForDisconessions(){
        synchronized (realListeners) {
            ClientListener listener = realListeners.getFirst();
            listener.onEvent(eventCrafter(GameState.WON_FOR_DISCONESSION, null, null));
        }
    }

    public void handleReconnectPause(ClientListenerRmi listener, String nickname) {
        Optional<Player> optionalPlayer = disconnectedPlayers.stream()
                .filter(player -> player.getNickname().equals(nickname))
                .findFirst();

        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            listenerbyPlayer.put(player, listener);
            playerbyListener.put(listener, player);
            disconnectedPlayers.remove(player);
            players.add(player);
            realListeners.add(listenerbyPlayer.get(player));

            System.out.println("Player " + nickname + " reconnected.");
        } else {
            System.out.println("No matching disconnected player for nickname: " + nickname);
        }
    }
}