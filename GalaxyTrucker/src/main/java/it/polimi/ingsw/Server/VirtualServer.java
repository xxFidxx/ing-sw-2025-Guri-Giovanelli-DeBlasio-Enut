package it.polimi.ingsw.Server;


import it.polimi.ingsw.Rmi.ClientRmi;
import it.polimi.ingsw.Rmi.VirtualViewRmi;

/**
 * Interfaccia che definisce i metodi del server che devono essere resi accessibili ai client per poter interagire con
 * la logica del server (controller).
 */
public interface VirtualServer {
    void addNickname(VirtualViewRmi client,String Nickname)  throws Exception;

    void createLobby(VirtualViewRmi client, int number) throws Exception;

}
