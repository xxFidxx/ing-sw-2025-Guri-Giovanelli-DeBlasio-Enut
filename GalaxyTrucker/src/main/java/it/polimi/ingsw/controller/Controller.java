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
import it.polimi.ingsw.model.resources.*;
import it.polimi.ingsw.model.game.*;

import java.util.*;
import java.util.stream.Collectors;


public class Controller{
    private Game game;
    private Lobby lobby;
    private GameState currentGameState = GameState.IDLE;
    private final List<ClientListener> listeners = new ArrayList<>();
    private final List<ClientListener> registredListeners = new ArrayList<>();
    private final Object LobbyLock = new Object();
    private final Object GameLock = new Object();
    private final boolean[] busyDecks;
    final Map<ClientListener, Player> playerbyListener = new HashMap<>();
    final Map<Player, ClientListener> listenerbyPlayer = new HashMap<>();
    final Map <ClientListener, Boolean> isDone = new HashMap<>();
    private AdventureCard currentAdventureCard;
    private Player currentPlayer;
    private ArrayList<Player> players;
    private boolean cargoended;
    private boolean piratesended;
    private boolean crewended;
    private Projectile currentProjectile;
    private int currentDiceThrow;
    private ArrayList<Player> tmpPlayers;
    private List<AdventureCard> cards;
    private final ArrayList<Player> defeatedPlayers;
    private boolean combatZoneFlag;
    private boolean piratesFlag;
    private boolean smugglersFlag;
    private boolean enemyDefeated;

    public Controller() {
        this.game = null;
        this.lobby = null;
        this.currentAdventureCard = null;
        this.currentPlayer = null;
        this.players = null;
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
        this.busyDecks = new boolean[3];
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

    public void addNickname(ClientListener listener, String nickname) throws LobbyExceptions {
        if (lobby == null)
            throw new LobbyExceptions("Not existing lobby");

        lobby.setPlayersNicknames(nickname);
        registredListeners.add(listener);

        for (ClientListener l : registredListeners) {
            l.onEvent(eventCrafter(GameState.WAIT_LOBBY, null, null));
        }

        if (lobby.isFull()) {
            gameInit();
        }
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
        listener.onEvent(eventCrafter(currentGameState, null, null));
    }

    public Event eventCrafter(GameState state, Object data, Player player) {
        Event event;
        switch (state) {
            case WAIT_LOBBY -> {
                ArrayList<String> nicks;
                synchronized (LobbyLock) {
                    nicks = lobby.getPlayersNicknames();
                }
                event = new Event(state, new LobbyNicks(nicks));
            }
            case ASSEMBLY -> {
                Integer[] assemblingTilesIds;
                synchronized (GameLock) {
                    assemblingTilesIds = game.getTilesId();
                }
                ArrayList<ComponentTile> reservedTiles = player.getSpaceshipPlance().getReserveSpot();
                event = new Event(state, new PickableTiles(assemblingTilesIds, reservedTiles));
            }

            case SHOW_SHIP -> {
                event = new Event(state, (DataString) data);
            }

            case PICKED_TILE,PICK_RESERVED_CARD -> {
                ComponentTile tile = (ComponentTile) data;
                event = new Event(state, new PickedTile(tile.toString()));
            }

            case DRAW_CARD -> {
                event = new Event(state, (Card) data);
            }

            case SHOW_CARDS ->{
                int nDeck = (int) data;
                ArrayList<AdventureCard> advCardsToShow = new ArrayList<>();

                switch(nDeck) {
                    case 1 -> {
                        advCardsToShow = new ArrayList<>(cards.subList(0, 4));
                    }
                    case 2 -> {
                        advCardsToShow = new ArrayList<>(cards.subList(5, 9));
                    }
                    case 3 -> {
                        advCardsToShow = new ArrayList<>(cards.subList(9, 13));
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
            case CHOOSE_BATTERY -> {
                int es = player.getEngineStrenght();
                int numDE = 0;
                for (Engine e : player.getSpaceshipPlance().getEngines()) {
                    if (e instanceof DoubleEngine) {
                        numDE++;
                    }
                }
                event = new Event(state, new DoubleEngineNumber(es, numDE));
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
                    ClientListener listener = listenerbyPlayer.get(player);
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
            case LOST_CREW -> {
                event = new Event(state, new LostCrew((int) data));
            }

            case CREW_MANAGEMENT -> {
                int astr = player.getSpaceshipPlance().getnAstronauts();
                int al = player.getSpaceshipPlance().getBrownAliens() + player.getSpaceshipPlance().getPurpleAliens();
                ArrayList<Cabin> cabins = player.getSpaceshipPlance().getCabins();
                int playersCrew = astr + al;
                int lostCrew = (int) data;

                if (playersCrew < lostCrew) {
                    lostCrew = playersCrew;
                }
                event = new Event(state, new CrewManagement(cabins, lostCrew));
            }

            case BATTERIES_MANAGEMENT -> {
                ArrayList<PowerCenter> pc = player.getSpaceshipPlance().getPowerCenters();
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
                            batteriesToRemove = -diffBatteries;
                        }
                    }

                    if (playerGoods > 0) {
                        cargosToRemove = playerGoods;
                    } else {
                        cargosToRemove = 0;
                    }
                }

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
        notifyAllListeners(eventCrafter(currentGameState, null, null));

        currentGameState = GameState.ASSEMBLY;

        ArrayList<String> nicks = lobby.getPlayersNicknames();
        game = new Game(nicks);
        players = new ArrayList<>(game.getPlayers());
        cards = game.getFlightplance().getDeck().getCards();

        for (boolean b : busyDecks)
            b = false;


        synchronized (playerbyListener) {
            for (int i = 0; i < players.size(); i++) {
                playerbyListener.put(listeners.get(i), players.get(i));
            }
        }

        synchronized (listenerbyPlayer) {
            for (int i = 0; i < listeners.size(); i++) {
                listenerbyPlayer.put(players.get(i), listeners.get(i));
            }
        }

        synchronized (isDone) {
            for (ClientListener l : listeners) {
                isDone.put(l, false);
            }
        }

        for(ClientListener listener: listeners){
            listener.onEvent(eventCrafter(currentGameState, null, playerbyListener.get(listener)));
        }

    }


    public void drawCard() {
        notifyAllListeners(eventCrafter(GameState.TURN_START, null, null));


        if (!cards.isEmpty() || players.isEmpty()) {
            currentAdventureCard = cards.getFirst();
            String cardName = currentAdventureCard.getName();
            int cardLevel = currentAdventureCard.getLevel();
            Card card = new Card(cardName, cardLevel);


            // aggiorniamo liste della nave prima di attivare la carta
            for (Player player : players) {
                player.getSpaceshipPlance().updateLists();
            }
            if (cardName != null) {
                notifyAllListeners(eventCrafter(GameState.DRAW_CARD, card, null));
                if(players.size() > 1){
                    orderPlayers();
                    tmpPlayers = new ArrayList<>(players);
                    isDone.replaceAll((c, v) -> false);
                    manageCard();
                }else{
                    if(currentAdventureCard instanceof CombatZoneCard){
                        drawCard();
                        notifyAllListeners(eventCrafter(GameState.SKIPPED_CARD, card, null));
                    }

                }

            }

        } else {
            notifyAllListeners(eventCrafter(GameState.END_GAME, null, null));
        }
    }

    public void manageCard() {
        // nelle carte dove si chiede di rimuovere alieni/batterie, voi fate finta che, chw l'abbiano già fatto e a fine turno chi deve rimuovere
        // invece glieli facciamo fisicamente rimuovere, dopo che tutti li avranno rimossi, allora vai in resetShowandDrawn
        switch (currentAdventureCard) {
            case AbandonedShipCard asc -> {
                if (tmpPlayers.isEmpty() || crewended) {
                    crewended = false;
                    resetShowAndDraw();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                ClientListener l = listenerbyPlayer.get(currentPlayer);
                if (currentAdventureCard.checkCondition(currentPlayer)) {
                    tmpPlayers.remove(currentPlayer);
                    handleWaitersPlayer(l);
                } else {
                    l.onEvent(eventCrafter(GameState.FAILED_CARD, null, null));
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
                ClientListener l = listenerbyPlayer.get(currentPlayer);
                if (currentAdventureCard.checkCondition(currentPlayer)) {
                    tmpPlayers.remove(currentPlayer);
                    handleWaitersPlayer(l);
                } else {
                    l.onEvent(eventCrafter(GameState.FAILED_CARD, null, null));
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
                    if (numDE > 0) {
                        handleWaitersBattery(l, currentPlayer);
                    } else {
                        tmpPlayers.remove(currentPlayer);
                        fromChargeToManage(l);
                    }
                }
            }

            case SlaversCard sl -> {
                if (tmpPlayers.isEmpty() || enemyDefeated) {
                    System.out.println("manageCard: vado in defeatedBySlavers");
                    defeatedBySlavers();
                    return;
                }
                currentPlayer = tmpPlayers.getLast();
                ClientListener l = listenerbyPlayer.get(currentPlayer);
                tmpPlayers.remove(currentPlayer);
                handleWaitersEnemy(l);
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
                ClientListener l = listenerbyPlayer.get(currentPlayer);
                tmpPlayers.remove(currentPlayer);
                handleWaitersEnemy(l);
            }

            case PiratesCard pc -> {
                if (tmpPlayers.isEmpty() || piratesended) {
                    piratesended = false;
                    piratesFlag = true;
                    System.out.println("manageCard: vado in defeatedByPirates");
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
                if (game.freePlanets(currentAdventureCard, currentPlanetsCard.getPlanets())) {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    tmpPlayers.remove(currentPlayer);
                    handleWaitersPlanets(l);
                } else {
                    ClientListener l = listenerbyPlayer.get(currentPlayer);
                    l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                    tmpPlayers.remove(currentPlayer);
                    manageCard();
                }
            }

            case MeteorSwarmCard msc -> {
                System.out.println("manageCard: entro");
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

            case EpidemicCard ec -> {
                currentAdventureCard.activate();
                resetShowAndDraw();
            }

            case StardustCard sc -> {
                currentAdventureCard.activate();
                resetShowAndDraw();
            }

            case CombatZoneCard czc -> {
                if(((CombatZoneCard)currentAdventureCard).getType() == CombatZoneType.LOSTCREW){
                    int minEquip = tmpPlayers.stream().mapToInt(Player::getNumEquip).min().orElse(Integer.MAX_VALUE);
                    List<Player> minEquipPlayers = tmpPlayers.stream().filter(p -> p.getNumEquip() == minEquip).collect(Collectors.toList());
                    if (minEquipPlayers.size() == 1) {
                        Player minEquipPlayer = minEquipPlayers.get(0);
                        int ld= ((CombatZoneCard) currentAdventureCard).getLostDays();
                        ClientListener l = listenerbyPlayer.get(minEquipPlayer);
                        handleMinEquip(l);
                        l.onEvent(eventCrafter(GameState.MOVE_PLAYER, ld, null));
                        game.getFlightplance().move(-ld, minEquipPlayer);
                    } else {
                        notifyAllListeners(eventCrafter(GameState.SAME_EQUIP, null, null));
                    }

                    for (ClientListener listener : listeners) {
                        Player player = playerbyListener.get(listener);
                        System.out.println("listener.onEvent(eventCrafter(GameState.CHOOSE_BATTERY, null, player));");
                        listener.onEvent(eventCrafter(GameState.CHOOSE_BATTERY, null, player));
                    }
                } else {
                    for (ClientListener listener : listeners) {
                        Player player = playerbyListener.get(listener);
                        listener.onEvent(eventCrafter(GameState.CHOOSE_CANNON, null, player));
                    }
                }

            }

            default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
        }
    }

    private void handleEarlyEnd(Player player) {
        ClientListener listener = listenerbyPlayer.get(player);
        isDone.remove(listener);
        System.out.println("handleEarlyEnd " + player);
        players.remove(player);
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
                            l.onEvent(eventCrafter(GameState.ASK_SHIELD, null, null));
                            return;
                        }
                    }
                    playerHit(l);
                } else {
                    l.onEvent(eventCrafter(GameState.NO_EXPOSED_CONNECTORS, null, null));
                    waitForNextShot(l);
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
                    waitForNextShot(l);
                } else if (result == 0) {
                    playerHit(l);
                } else if (result == 1) {
                    l.onEvent(eventCrafter(GameState.SINGLE_CANNON_PROTECTION, null, null));
                    waitForNextShot(l);
                } else {
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
        notifyAllListeners(eventCrafter(GameState.END_CARD, null, null));
        game.endTurn();
        isDone.replaceAll((c, v) -> false);
        cargoended = false;
        combatZoneFlag = false;
        piratesFlag = false;
        smugglersFlag = false;
        enemyDefeated = false;
        piratesended = false;
        crewended = false;
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
        currentAdventureCard.activate();
        crewended=true;
        sendToCrewManagement(p);
    }

    private void activateAbandonedStationCard(ClientListener listener) {
        Player p = playerbyListener.get(listener);
        AbandonedStationCard currentAbandonedStationCard = (AbandonedStationCard) currentAdventureCard;
        currentAbandonedStationCard.setActivatedPlayer(p);
        currentAdventureCard.activate();
        cargoended = true;
        listener.onEvent(eventCrafter(GameState.CARGO_MANAGEMENT, null, null));
    }

    public void handleWaitersPlayer(ClientListener listener) {
        for (ClientListener l : listeners) {
            if (l == listener) {
                l.onEvent(eventCrafter(GameState.CHOOSE_PLAYER, null, null));
            } else {
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }
    }

    public void handleMinEquip(ClientListener listener) {
        for (ClientListener l : listeners) {
            if (l == listener) {
                l.onEvent(eventCrafter(GameState.LEAST_CREW, null, null));
            } else {
                l.onEvent(eventCrafter(GameState.NOT_MIN_EQUIP, null, null));
            }
        }
    }

    public void handleMinEngine(ClientListener listener) {
        for (ClientListener l : listeners) {
            if (l == listener) {
                l.onEvent(eventCrafter(GameState.LEAST_ENGINE, null, null));
            } else {
                l.onEvent(eventCrafter(GameState.NOT_MIN_ENGINE, null, null));
            }
        }
    }

    public void handleMinFire(ClientListener listener) {
        for (ClientListener l : listeners) {
            if (l == listener) {
                l.onEvent(eventCrafter(GameState.CANNON_FIRE, null, null));
            } else {
                l.onEvent(eventCrafter(GameState.NOT_MIN_FIRE, null, null));
            }
        }
    }

    public void handleWaitersBattery(ClientListener listener, Player player) {
        for (ClientListener l : listeners) {
            if (l == listener) {
                listener.onEvent(eventCrafter(GameState.CHOOSE_BATTERY, null, player));
            } else {
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }
    }

    public void handleWaitersEnemy(ClientListener listener) {
        for (ClientListener l : listeners) {
            System.out.println("handleWaitersEnemy: Listener: " + l);
            if (l == listener) {
                l.onEvent(eventCrafter(GameState.SHOW_ENEMY, null, currentPlayer));
            } else {
                System.out.println("handleWaitersEnemy: mando in WAIT_PLAYER");
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }
    }

    private void handleWaitersPlanets(ClientListener listener) {
        for (ClientListener l : listeners) {
            if (l == listener) {
                PlanetsCard currentPlanetsCard = (PlanetsCard) currentAdventureCard;
                l.onEvent(eventCrafter(GameState.CHOOSE_PLANETS, currentPlanetsCard.getPlanets(), null));
            } else
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
        }

    }

    public void fromChargeToManage(ClientListener listener) {
        AdventureCard currentCastedCard = currentAdventureCard;
        switch (currentCastedCard) {
            case OpenSpaceCard osc -> {
                ((OpenSpaceCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                currentAdventureCard.activate();
                manageCard();
            }
            case SlaversCard sc -> {
                ((SlaversCard) currentCastedCard).setActivatedPlayer(currentPlayer);
                currentAdventureCard.activate();
                int outcome = ((SlaversCard) currentCastedCard).getFightOutcome(currentPlayer);
                if (outcome == 1) {
                    listener.onEvent(eventCrafter(GameState.ENEMY_WIN, null, null));
                    enemyDefeated = true;
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
                if (((CombatZoneCard) currentAdventureCard).getType() == CombatZoneType.LOSTCREW) {
                    if (!combatZoneFlag) {
                        combatZoneFlag = true;
                        isDone.put(listener,true);
                        if(!isDone.containsValue(false)){
                            int minEngine = tmpPlayers.stream().mapToInt(Player::getEngineStrenght).min().orElse(Integer.MAX_VALUE);
                            List<Player> minEnginePlayers = tmpPlayers.stream().filter(p -> p.getEngineStrenght() == minEngine).collect(Collectors.toList());
                            if (minEnginePlayers.size() == 1) {
                                Player minEnginePlayer = minEnginePlayers.get(0);
                                int lostOther = ((CombatZoneCard) currentAdventureCard).getLostOther();
                                ClientListener l = listenerbyPlayer.get(minEnginePlayer);
                                handleMinEngine(l);
                                l.onEvent(eventCrafter(GameState.LOST_CREW, lostOther,null));
                                //minEnginePlayer.loseCrew(lostOther);
                                sendToCrewManagement(minEnginePlayer);
                            } else {
                                notifyAllListeners(eventCrafter(GameState.SAME_ENGINE, null, null));
                            }
                            //combatZoneCannons();
                        } else {
                            listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                        }
                    } else {
                        combatZoneFlag = false;
                        isDone.put(listener, true);
                        if (!isDone.containsValue(false)) {
                            Player minFirePlayer = players.stream().min(Comparator.comparing(Player::getFireStrenght)).orElse(null);
                            if (minFirePlayer != null) {
                                ClientListener l = listenerbyPlayer.get(minFirePlayer);
                                handleMinFire(l);
                                combatZoneShots(minFirePlayer);
                            } else {
                                notifyAllListeners(eventCrafter(GameState.SAME_FIRE, null, null));
                                resetShowAndDraw();
                            }
                        } else {
                            listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                        }
                    }
                } else {
                    if (!combatZoneFlag) {
                        combatZoneFlag = true;
                        isDone.put(listener, true);
                        if (!isDone.containsValue(false)) {
                            Player minFirePlayer = players.stream().min(Comparator.comparing(Player::getFireStrenght)).orElse(null);
                            if (minFirePlayer != null) {
                                int ld = ((CombatZoneCard) currentAdventureCard).getLostDays();
                                ClientListener l = listenerbyPlayer.get(minFirePlayer);
                                handleMinFire(l);
                                l.onEvent(eventCrafter(GameState.MOVE_PLAYER, ld, null));
                                game.getFlightplance().move(-ld, minFirePlayer);
                            } else {
                                listener.onEvent(eventCrafter(GameState.SAME_FIRE, null, null));
                            }
                            combatZoneEngine();

                        } else {
                            listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                        }
                    } else {
                        combatZoneFlag = false;
                        isDone.put(listener, true);
                        if (!isDone.containsValue(false)) {
                            Player minEnginePlayer = players.stream().min(Comparator.comparingInt(Player::getEngineStrenght)).orElse(null);
                            if (minEnginePlayer != null) {
                                ClientListener l = listenerbyPlayer.get(minEnginePlayer);
                                handleMinEngine(l);
                                sendToRemoveMVGoods(minEnginePlayer);
                            } else {
                                notifyAllListeners(eventCrafter(GameState.SAME_ENGINE, null, null));
                                combatZoneLastMinEquip();
                            }
                        } else {
                            listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                        }
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + currentCastedCard);
        }
    }

    public void combatZoneLastMinEquip() {
        Player minEquipPlayer = tmpPlayers.stream().min(Comparator.comparingInt(Player::getNumEquip)).orElse(null);
        if (minEquipPlayer != null) {
            ClientListener l2 = listenerbyPlayer.get(minEquipPlayer);
            handleMinEquip(l2);
            combatZoneShots(minEquipPlayer);
        } else {
            notifyAllListeners(eventCrafter(GameState.SAME_EQUIP, null, null));
            resetShowAndDraw();
        }
    }

    public void combatZoneCannons() {
        for (ClientListener listener : listeners) {
            Player player = playerbyListener.get(listener);
            listener.onEvent(eventCrafter(GameState.CHOOSE_CANNON, null, player));
        }
    }

    public void combatZoneEngine() {
        for (ClientListener listener : listeners) {
            Player player = playerbyListener.get(listener);
            listener.onEvent(eventCrafter(GameState.CHOOSE_BATTERY, null, player));
        }

    }

    public void combatZoneShots(Player minEquipPlayer) {
        Projectile[] shots = ((CombatZoneCard) currentAdventureCard).getCannons();
        int length = shots.length;
        int i = 0;
        while (i < length && shots[i] == null) {
            i++;
        }
        if (i == length) {
            resetShowAndDraw();
            return;
        }
        currentProjectile = shots[i];
        shots[i] = null;
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
        for (ClientListener l : listeners) {
            Player p = playerbyListener.get(l);
            if (defeatedPlayers.contains(p)) {
                defeatedPlayers.remove(p);
                sendToCrewManagement(p);
            } else {
                System.out.println("defeatedBySlavers: mando in WAIT_PLAYER");
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }
    }

    public void sendToCrewManagement(Player p){
        ClientListener l = listenerbyPlayer.get(p);
        int astr = p.getSpaceshipPlance().getnAstronauts();
        int al = p.getSpaceshipPlance().getBrownAliens() + p.getSpaceshipPlance().getPurpleAliens();
        ArrayList<Cabin> cabins = p.getSpaceshipPlance().getCabins();
        int lostCrew;
        switch(currentAdventureCard){
            case CombatZoneCard czc -> lostCrew = ((CombatZoneCard)currentAdventureCard).getLostOther();
            case SlaversCard sc -> lostCrew = ((SlaversCard)currentAdventureCard).getLostCrew();
            case AbandonedShipCard asc -> lostCrew = ((AbandonedShipCard)currentAdventureCard).getLostCrew();
            default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
        }
        CrewManagement cm;
        System.out.println("sendToCrewManagement: mando in CREW_MANAGEMENT");

        printSpaceshipbyTile(l, cabins.getFirst());
        l.onEvent(eventCrafter(GameState.CREW_MANAGEMENT, lostCrew, p));
    }

    public void defeatedBySmugglers() {
        if (defeatedPlayers.isEmpty()) {
            resetShowAndDraw();
            return;
        }
        System.out.println("defeatedBySmugglers: defeatedPlayers size: " + defeatedPlayers.size());
        for (ClientListener l : listeners) {
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

        System.out.println("sendToRemoveMVGoods: diff: ");
            l.onEvent(eventCrafter(GameState.REMOVE_MV_GOODS, cardMalus, p));
        }

    public void waitForEnemies(ClientListener l) {
        isDone.put(l, true);
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
            l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
        }
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

        switch (pos) {
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
        listener.onEvent(eventCrafter(GameState.PLAYER_COLOR, playerColor, null));

        synchronized (isDone) {
            if (!isDone.containsValue(false))
                handleCraftingEnded();
            else
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
        }
    }

    private void handleCraftingEnded() {

        notifyAllListeners(eventCrafter(GameState.CRAFTING_ENDED, null, null));

        synchronized (isDone) {
            for (ClientListener l : listeners) {
                isDone.put(l, false);
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
                isDone.put(l, true);
                printSpaceship(l);
            }
        }

        if (allOk) {
            chooseAliens();
        } else { // for each already done client I send state to wait for the ones who aren't done cause they have to adjust
            isDone.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .forEach(entry -> {
                        ClientListener l = entry.getKey();
                        l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
                    });
        }

    }

    private String[] handleBoardView() {

        String[] boardView = new String[18];
        Arrays.fill(boardView, "[]");


        // 3) Li “sparo” nella board in base alla loro posizione
        for (Player player : players) {
            Placeholder p = player.getPlaceholder();

            int pos = (p.getPosizione()) % 18;
            if (pos < 0) {
                pos = pos + 18;
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
        for (ClientListener c : listeners) {
            Player player = playerbyListener.get(c);
            DataString ds = new DataString(player.getSpaceshipPlance().tileGridToString());
            c.onEvent(eventCrafter(GameState.SHOW_SHIP, ds, null));
        }
    }

    public void printSpaceship(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        String complete_ship = player.getSpaceshipPlance().reserveSpotToString() + "\n" + player.getSpaceshipPlance().tileGridToStringAdjustments();
        DataString ds = new DataString(complete_ship);
        listener.onEvent(eventCrafter(GameState.SHOW_SHIP, ds, null));
    }

    public void printSpaceshipAdjustment(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        String complete_ship = player.getSpaceshipPlance().tileGridToStringAdjustments();
        DataString ds = new DataString(complete_ship);
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
            throw new ControllerExceptions("You don't have enough batteries");
        for (Integer i : chosenIndices) {
            if (i < 0 || i > doubleCannons.size()) {
                throw new ControllerExceptions("You selected a wrong chosen cannons number");
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
            putTileBack(listener);
        } else {
            player.getSpaceshipPlance().addReserveSpot(tile);
            player.setHandTile(null);
            printSpaceship(listener);
            listener.onEvent(eventCrafter(GameState.ASSEMBLY, null, player));
        }

    }

    public void endCargoManagement(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        //tmpPlayers.remove(player);
        player.setReward(null);
        cargoended = true;
        System.out.println("Cargo management ended");
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
                    isDone.put(listener, true);
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
                waitForNextShot(listener);
            } else {
                printSpaceshipParts(listener);
            }
        }
    }

    private void printSpaceshipParts(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        String complete_ship = player.getSpaceshipPlance().tileGridToStringParts();
        DataString ds = new DataString(complete_ship);
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
                isDone.put(listener, true);
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
                waitForNextShot(listener);
            }
        }
    }

    private void chooseAliens() {

        isDone.replaceAll((c, v) -> false);


        for (ClientListener l : listeners) {
            Player p = playerbyListener.get(l);
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
                isDone.put(l, true);
                l.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }

        // if everyone went to waitPlayer, so isDone is all true
        if (!isDone.containsValue(false)) {
            drawCard();
        }

    }

    public void playerHit(ClientListener listener) {
        Player p = playerbyListener.get(listener);
        Direction direction = currentProjectile.getDirection();
        System.out.println("playerHit: mando in takeHit");
        takeHit(listener, direction, currentDiceThrow);
        if (currentAdventureCard instanceof CombatZoneCard) {
            combatZoneShots(p);
        } else
            waitForNextShot(listener);
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

    public void waitForNextShot(ClientListener listener) {
        isDone.put(listener, true);
        if (!isDone.containsValue(false)) {
            if (currentAdventureCard instanceof MeteorSwarmCard) {
                isDone.replaceAll((c, v) -> false);
                manageCard();
            } else {
                isDone.replaceAll((c, v) -> false);
                defeatedByPirates();
            }
        } else {
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
        isDone.put(listener, true);

        // player who didn't have cabins to put aliens in or finished they alien chosen have isDone = true
        if (!isDone.containsValue(false))
            drawCard();
        else
            listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
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
        ArrayList<Cabin> interconnectedCabins = p.getSpaceshipPlance().getInterconnectedCabins();
        for (Cabin c : interconnectedCabins) {
            if (c.getId() == cabinId) {
                Figure[] figures = c.getFigures();

                if (figures[1] != null) {
                    figures[1] = null;
                    interconnectedCabins.remove(c);
                    return true;
                } else if (figures[0] != null) {
                    figures[0] = null;
                    interconnectedCabins.remove(c);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEpidemicDone(ClientListener listener) {
        Player p = playerbyListener.get(listener);
        return p.getSpaceshipPlance().checkInterconnectedCabinsEmpty();
    }




    private void checkEarlyEndConditions() {
        List<Player> playersToRemove = new ArrayList<>();

        for (Player player : players) {
            System.out.println("Player early condition checked " + player);
            if (player.getSpaceshipPlance().getnAstronauts() == 0 ||
                    players.getLast().getPlaceholder().getPosizione() > player.getPlaceholder().getPosizione() + 18) {
                playersToRemove.add(player);
            }
        }

        for (Player player : playersToRemove) {
            handleEarlyEnd(player);
        }
    }

    private void endTurn() {
        checkEarlyEndConditions();
        if (players.isEmpty()) {
            notifyAllListeners(eventCrafter(GameState.END_GAME, null, null));
        } else {
            for (Player player : players) {
                ClientListener listener = listenerbyPlayer.get(player);
                listener.onEvent(eventCrafter(GameState.ASK_SURRENDER, null, null));
            }
        }

    }

    private boolean handleAdjustmentEnded() {
        synchronized (isDone) {
            return !isDone.containsValue(false);
        }
    }

    public void handleSurrenderEnded(ClientListener listener) {

        synchronized (isDone) {
            isDone.put(listener, true);
        }

        synchronized (isDone) {
            if (!isDone.containsValue(false))
                drawCard();
            else {
                isDone.forEach((client, value) ->
                        System.out.println("Client: " + client + " -> Value: " + value)
                );
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null, null));
            }
        }
    }

    public void surrender(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        isDone.remove(listener);
        players.remove(player);
        player.setSurrended(true);
        listener.onEvent(eventCrafter(GameState.DIED, null, null));
    }

    public boolean removeBatteries(ClientListener listener, int powerCenterId, int batteries) {
        Player p = playerbyListener.get(listener);
        ArrayList<PowerCenter> powerCenters = p.getSpaceshipPlance().getPowerCenters();
        boolean error = false;
        for (PowerCenter pc : powerCenters) {
            if (pc.getId() == powerCenterId) {
                boolean[] pcBatteries = pc.getBatteries();
                while (batteries > 0 && !error) {
                    if (batteries == 1) {
                        if (pcBatteries[1]) {
                            pcBatteries[1] = false;
                            batteries--;
                        } else if (pcBatteries[0]) {
                            pcBatteries[0] = false;
                            batteries--;
                        } else
                            error = true;
                    } else if (batteries == 2) {
                        if (pcBatteries[1]) {
                            pcBatteries[1] = false;
                            batteries--;
                        } else
                            error = true;
                    } else
                        error = true;
                }
            }
        }
        return !error;
    }

    public void endManagement(ClientListener listener) {
        Player p = playerbyListener.get(listener);
        switch(currentAdventureCard){
            case CombatZoneCard czc -> {
                if(!combatZoneFlag)
                    fromChargeToManage(listener);
                else
                    combatZoneShots(p);
            }
            case MeteorSwarmCard msc -> waitForNextShot(listener);
            case OpenSpaceCard osc -> fromChargeToManage(listener);
            case PiratesCard pc -> {
                if (piratesFlag)
                    waitForNextShot(listener);
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
            default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
        }
    }

    public void endMVGoodsManagement(ClientListener listener) {
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

    public void takeHit(ClientListener l, Direction direction, int position) {
        // cammini partendo dalla casella indicata verso il centro
        // appena trovi un componente lo rimuovi
        // aggiungere prima i check per la posizione
        Player p = playerbyListener.get(l);
        ComponentTile[][] components = p.getSpaceshipPlance().getComponents();
        int max_lenght = 7;

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
            l.onEvent(eventCrafter(GameState.SHOT_HIT, null, null));
            removeAdjust(l, x, y);
        } else {
            l.onEvent(eventCrafter(GameState.NO_HIT, null, null));
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

    public void showDecks(ClientListenerRmi listener) {
        listener.onEvent(eventCrafter(GameState.SHOW_DECKS, null, null));
    }

    public boolean showCardsbyDeck(ClientListenerRmi listener, int nDeck) {
        synchronized (busyDecks) {
            if (busyDecks[nDeck - 1]) {
                return false;
            }
        }

        listener.onEvent(eventCrafter(GameState.SHOW_CARDS, nDeck, null));
        busyDecks[nDeck - 1] = true;
        return true;
    }

    public void endShowCards(ClientListenerRmi listener, int nDeck) {

        if(nDeck != -1){
            synchronized (busyDecks) {
                busyDecks[nDeck - 1] = false;
            }
        }

        listener.onEvent(eventCrafter(GameState.ASSEMBLY, null, playerbyListener.get(listener)));
    }


}