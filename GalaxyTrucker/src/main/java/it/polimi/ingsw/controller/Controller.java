package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.EventListenerInterface;
import it.polimi.ingsw.controller.network.Lobby;
import it.polimi.ingsw.model.game.Game;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Controller implements EventListenerInterface {
    Game game;
    Lobby lobby;
    // queue of messageEvents, because you don't want the client to wait the computational time
    private final BlockingQueue<Event> queue;

    public Controller() {
        this.game = null;
        this.queue = new LinkedBlockingQueue<>();
        this.lobby = null;
    }

    public void addGameListener(EventListenerInterface listener) {
        game.addEventListener(listener);
    }

    public void removeGameListener(EventListenerInterface listener) {
        game.removeEventListener(listener);
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
    }

    public ArrayList<String> addNickname(String nickname) throws LobbyExceptions {
        if(lobby == null)
            throw new LobbyExceptions("Not existing lobby");

        lobby.setPlayersName(nickname);

        return lobby.getPlayersName();
    }

    public boolean isLobbyFull(){
        return lobby.isFull();
    }


}
