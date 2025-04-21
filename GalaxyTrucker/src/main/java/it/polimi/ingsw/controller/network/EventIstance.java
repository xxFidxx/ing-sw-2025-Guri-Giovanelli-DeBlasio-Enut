package it.polimi.ingsw.controller.network;

import java.util.EventObject;


public class EventIstance extends EventObject {

    private final Event event;

    public EventIstance(Object source, Event event){
        super(source);
        this.event = event;
    }

    public Event getMessage() {
        return event;
    }
}
