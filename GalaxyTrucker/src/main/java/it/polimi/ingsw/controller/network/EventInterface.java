package it.polimi.ingsw.controller.network;

import it.polimi.ingsw.Server.GameState;

import java.io.Serializable;

public interface EventInterface extends Serializable {
    boolean isValid();
    GameState getState();
}
