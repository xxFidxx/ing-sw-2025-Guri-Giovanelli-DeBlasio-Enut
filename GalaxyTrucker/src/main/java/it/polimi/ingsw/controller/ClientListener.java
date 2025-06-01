package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.network.Event;

public interface ClientListener {

    void onEvent(Event event);
}
