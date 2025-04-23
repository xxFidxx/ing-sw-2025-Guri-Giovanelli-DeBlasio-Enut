package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.network.Event;

public interface VirtualView {
    void showUpdate(Event event) throws Exception;
}
