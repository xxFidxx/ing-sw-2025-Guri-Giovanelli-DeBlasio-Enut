package it.polimi.ingsw.controller.network;

import it.polimi.ingsw.controller.LobbyExceptions;

import java.util.ArrayList;

public class Lobby {
    private ArrayList<String> playersNicknames;
    private  int numPlayers;
    private boolean isFull;

    public Lobby(int numPlayers) {
        this.playersNicknames = new ArrayList<>();
        this.numPlayers = numPlayers;
    }

    public ArrayList<String> getPlayersNicknames() {
        return playersNicknames;
    }

    public void setPlayersNicknames(String name){
        if (playersNicknames.contains(name)) {
            throw new LobbyExceptions("Nome già utilizzato: " + name);
        }

        if (playersNicknames.size() >= numPlayers) {
            throw new LobbyExceptions("Numero massimo di giocatori raggiunto");
        }

        playersNicknames.add(name);

        if(playersNicknames.size() == numPlayers) {
            isFull = true;
        }
    }

    public boolean isFull() {
        return isFull;
    }
}
