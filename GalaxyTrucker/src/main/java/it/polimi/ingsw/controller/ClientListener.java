package it.polimi.ingsw.controller;

import it.polimi.ingsw.Rmi.ServerRmi;
import it.polimi.ingsw.Rmi.VirtualViewRmi;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.EventListenerInterface;

public class ClientListener implements EventListenerInterface {
    private final VirtualViewRmi client;
    private final ServerRmi server;

    public ClientListener(VirtualViewRmi client, ServerRmi server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void onEvent(Event event) {
        server.notifyClient(client, event);
    }

    public VirtualViewRmi getClient() {
        return client;
    }

    public ServerRmi getServer() {
        return server;
    }
}
