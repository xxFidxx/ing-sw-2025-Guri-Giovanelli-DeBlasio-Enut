package it.polimi.ingsw.controller.network;

public interface MessageListenerSubscriber {
    public interface Observable {


        void addEEventListener(EventListenerInterface listener);

        void removeEventListener(EventListenerInterface listener);

        void notifyEventListeners(Event event);

        void notifyEventListener(String identifier, Event event);
    }
}
