package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.EventListenerInterface;
import it.polimi.ingsw.controller.network.Lobby;
import it.polimi.ingsw.controller.network.data.*;
import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

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

    public Controller() {
        this.game = null;
        this.queue = new LinkedBlockingQueue<>();
        this.lobby = null;
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
        String tileName = game.pickTile(player,tileId);

        if(tileName != null)
        listener.onEvent(eventCrafter(GameState.PICKED_TILE, tileName));
        else{
            listener.onEvent(eventCrafter(GameState.ROBBED_TILE, null));
            listener.onEvent(eventCrafter(GameState.ASSEMBLY, null));
        }
    }

    public void handleOnConnectState(ClientListener listener){
        listener.onEvent(eventCrafter(currentGameState, null));
    }

    public Event eventCrafter(GameState state, Object data){
        Event event = null;
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
                event = new Event(this, state, new PickedTile((String)data));
            }
            case DRAW_CARD -> {
                event = new Event(this, state, (DataContainer) data);
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

        synchronized(playerbyListener){
            for(int i=0; i<players.size(); i++){
                playerbyListener.put(listeners.get(i), players.get(i));
            }
        }

        Event event = eventCrafter(currentGameState,null);
        notifyAllListeners(event);
    }

    public void drawCard(ClientListener listener) {
        AdventureCard[] cards = game.getFlightPlance().getDeck().getCards();
        String cardName = cards[0].getName();
        int cardLevel = cards[0].getLevel();
        Card card = new Card(cardName, cardLevel);

        if(cardName != null)
            listener.onEvent(eventCrafter(GameState.DRAW_CARD, card));
    }

    public void activateCard(ClientListener listener) throws LobbyExceptions {
        AdventureCard[] cards = game.getFlightPlance().getDeck().getCards();
        cards[0].activate();
    }
}
