package it.polimi.ingsw.controller.network;

import it.polimi.ingsw.controller.LobbyExceptions;

import java.util.ArrayList;

public class Lobby {
    private ArrayList<String> playersName;
    private  int numPlayers;

    public Lobby(int numPlayers) {
        this.playersName = new ArrayList<>();
        this.numPlayers = numPlayers;
    }

    public ArrayList<String> getPlayersName() {
        return playersName;
    }

    public void setPlayersName(String name){
        if (playersName.contains(name)) {
            throw new LobbyExceptions("Nome già utilizzato: " + name);
        }

        if (playersName.size() >= numPlayers) {
            throw new LobbyExceptions("Numero massimo di giocatori raggiunto");
        }
        playersName.add(name);
    }
}
