package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.EventListenerInterface;
import it.polimi.ingsw.controller.network.Lobby;
import it.polimi.ingsw.controller.network.data.*;
import it.polimi.ingsw.model.adventureCards.AbandonedShipCard;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.resources.GoodsContainer;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static it.polimi.ingsw.Server.GameState.CARGO_MANAGEMENT;


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

    public Controller() {
        this.game = null;
        this.queue = new LinkedBlockingQueue<>();
        this.lobby = null;
        this.currentAdventureCard = null;
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

    public void pickTile(ClientListener listener, int tileId) throws LobbyExceptions {
        Player player = playerbyListener.get(listener);
        ComponentTile tile = game.pickTile(player,tileId);

        if(tile != null){
            String tileName = tiletoString(tile);
            ConnectorType[] connectors = tile.getConnectors();
            listener.onEvent(eventCrafter(GameState.PICKED_TILE, new PickedTile(tileName,connectors)));
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

            case PICKED_TILE -> {
                event = new Event(this, state, (PickedTile)data);
            }
            case DRAW_CARD -> {
                event = new Event(this, state, (DataContainer) data);
            }
            case SHOW_PLAYER -> {
                event = new Event(this, state, (PlayerInfo) data);
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
            for (int i = 0; i < listenerbyPlayer.size(); i++) {
                listenerbyPlayer.put(players.get(i), listeners.get(i));
            }
        }

        synchronized(isDonecrafting){
            for(int i=0; i<isDonecrafting.size(); i++){
                isDonecrafting.put(listeners.get(i), false);
            }
        }

        Event event = eventCrafter(currentGameState,null);
        notifyAllListeners(event);
    }

    public String[] tileCrafter(String name, ConnectorType[] connectors){
         char[][] tile =  new char[2][2];
         for(int i=0; i<2; i++){
             ConnectorType connector = connectors[i];
             switch(connector){
                 case ConnectorType.UNIVERSAL ->
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

    public void drawCard(ClientListener listener) {
        AdventureCard[] cards = game.getFlightPlance().getDeck().getCards();
        currentAdventureCard = cards[0];
        // AdventureCard adCard = cards[0];
        String cardName = currentAdventureCard.getName();
        int cardLevel = currentAdventureCard.getLevel();
        Card card = new Card(cardName, cardLevel);

        if (cardName != null)
            notifyAllListeners(eventCrafter(GameState.DRAW_CARD, card));
        else
            notifyAllListeners(eventCrafter(GameState.END_GAME, null));
    }

    public void manageCard(){
        switch(currentAdventureCard){
            case AbandonedShipCard asc -> {
                Player p = (game.choosePlayer(currentAdventureCard));
                ClientListener l = listenerbyPlayer.get(p);
                    if(p!=null)
                        handleWaiters(l);
                        // se choosePlayer da' null vuol dire che ha finito i players a cui chiedere
                    else{
                        notifyAllListeners(eventCrafter(GameState.END_CARD, null));
                    }
            }
            // default -> listener.onEvent(eventCrafter(GameState.ACTIVATE_CARD, card));
            default -> throw new IllegalStateException("Unexpected value: " + currentAdventureCard);
        }
    }

    public void activateCard(ClientListener listener) throws LobbyExceptions {
        AdventureCard[] cards = game.getFlightPlance().getDeck().getCards();
        cards[0].activate();
        notifyAllListeners(eventCrafter(GameState.END_CARD, null));
    }

    public void handleWaiters(ClientListener listener){
        for(ClientListener l: listeners){
            if(l == listener)
                listener.onEvent(eventCrafter(GameState.CHOOSE_PLAYER, null));
            else
                listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
        }
    }

    public void playerIsDoneCrafting(ClientListener listener) throws Exception {
        if(isDonecrafting.get(listener))
            throw new Exception ("The player was already done crafting!\n");

        synchronized(isDonecrafting){
            isDonecrafting.replace(listener,true);
        }

        synchronized(isDonecrafting){
            if(!isDonecrafting.containsValue(false))
               handleCraftingEnded();
        }
    }

    public void handleCraftingEnded(){
        notifyAllListeners(eventCrafter(GameState.CRAFTING_ENDED, null));
    }

    public void checkStorage(ClientListener listener) throws CargoManagementException {
        Player player = playerbyListener.get(listener);
        if(game.checkStorage(player)){
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
        }else
            // qua ci sarebbe da gestire se siamo in planets quindi devi aspettare altri oppure in un reward generico quindi lui gestisce e finisce il turno per tutti...
            listener.onEvent(eventCrafter(GameState.WAIT_PLAYER, null));
        throw new CargoManagementException("You got 0 storage space, you can't manage any good");
    }

    public void addGood(ClientListener listener,int cargoIndex, int goodIndex, int rewardIndex) {
        Player player = playerbyListener.get(listener);
        game.addGood(player,cargoIndex,goodIndex,rewardIndex);
    }

    public void swapGoods(ClientListener listener,int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) {
        Player player = playerbyListener.get(listener);
        game.swapGoods(player,cargoIndex1,cargoIndex2,goodIndex1,goodIndex2);
    }

    public void removeGood(ClientListener listener, int cargoIndex, int goodIndex) {
        Player player = playerbyListener.get(listener);
        game.removeGood(player,cargoIndex,goodIndex);
    }

    public void acceptCard() {
        // game.resetResponded();
        notifyAllListeners(eventCrafter(GameState.ACTIVATE_CARD, null));
    }

    public void rejectCard() {
        notifyAllListeners(eventCrafter(GameState.MANAGE_CARD, null));
    }

    public void hasResponded(ClientListener listener) {
        Player player = playerbyListener.get(listener);
        player.setResponded(true);
    }

    public void endCard(ClientListener listener) {
        game.endTurn();
        Player player = playerbyListener.get(listener);
        ArrayList<Player> players = game.getPlayers();
        String nick=null;
        int pos, cred, astr, al;
        pos=cred=astr=al=0;
        for(Player p: players){
            if(p == player){
                nick = p.getNickname();
                pos = p.getPlaceholder().getPosizione();
                cred = p.getCredits();
                astr = p.getNumAstronauts();
                al = p.getNumAliens();
                PlayerInfo pi = new PlayerInfo(nick, pos, cred, astr, al);
                listener.onEvent(eventCrafter(GameState.SHOW_PLAYER, pi));
            }
        }
    }
}
