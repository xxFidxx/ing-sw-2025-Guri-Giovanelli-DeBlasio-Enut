package it.polimi.ingsw.Socket.Server;

import it.polimi.ingsw.controller.ClientListener;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.EventListenerInterface;

import java.io.ObjectOutputStream;


public class ClientListenerSocket implements EventListenerInterface, ClientListener {
    private final SocketServer server;
    private final ObjectOutputStream client;

    public ClientListenerSocket(SocketServer server, ObjectOutputStream client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void onEvent(Event event) {
        server.notifyClient(client,event);
    }
}
