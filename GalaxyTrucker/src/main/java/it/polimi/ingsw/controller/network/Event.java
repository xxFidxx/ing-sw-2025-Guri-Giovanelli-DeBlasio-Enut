package it.polimi.ingsw.controller.network;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.data.DataContainer;

import java.util.EventObject;


public class Event extends EventObject implements EventInterface {

    private final GameState gameState;
    private final DataContainer data;    // e.g., BoardData, MoveValidationData

    public Event(Object source, GameState gameState, DataContainer data) {
        super(source);
        this.gameState = gameState;
        this.data = data;
    }


    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public GameState getState() {
        return gameState;
    }

    public DataContainer getData() {
        return data;
    }
}
