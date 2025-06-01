package it.polimi.ingsw.controller.network;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.data.DataContainer;

import java.io.Serializable;


public class Event implements Serializable {

    private final GameState gameState;
    private final DataContainer data;

    public Event(GameState gameState, DataContainer data) {
        this.gameState = gameState;
        this.data = data;
    }

    public GameState getState() {
        return gameState;
    }

    public DataContainer getData() {
        return data;
    }
}
