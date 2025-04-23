package it.polimi.ingsw.controller.network;

import java.util.EventListener;

public interface EventListenerInterface extends EventListener {

    void onEvent(Event event);
}
