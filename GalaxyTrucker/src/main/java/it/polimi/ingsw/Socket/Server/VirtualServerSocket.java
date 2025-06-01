package it.polimi.ingsw.Socket.Server;

import it.polimi.ingsw.Server.VirtualServer;

import java.io.ObjectOutputStream;

public interface VirtualServerSocket extends VirtualServer {


    void handleCommand(String command, Object[] parameters, ObjectOutputStream out);

    void addClient(ObjectOutputStream out);
}
