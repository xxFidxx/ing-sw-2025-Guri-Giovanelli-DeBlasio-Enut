package it.polimi.ingsw.Server;


/**
 * Interfaccia che definisce i metodi del server che devono essere resi accessibili ai client per poter interagire con
 * la logica del server (controller).
 */
public interface VirtualServer {
    void addNickname(String Nickname)  throws Exception;

    void createLobby(int number) throws Exception;
}
