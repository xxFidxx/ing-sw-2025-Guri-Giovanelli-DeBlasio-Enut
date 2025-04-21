package it.polimi.ingsw.controller.network;

import java.io.Serializable;

public interface Event extends Serializable {

    boolean isValid();
}
