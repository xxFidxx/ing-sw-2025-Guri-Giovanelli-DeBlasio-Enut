package it.polimi.ingsw.Socket.Client;

import it.polimi.ingsw.Socket.SocketWrapper;
import it.polimi.ingsw.Socket.Server.VirtualServerSocket;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final VirtualServerSocket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Socket socket, VirtualServerSocket server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            server.addClient(out);
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof SocketWrapper message) {
                    server.handleCommand(message.getCommand(), message.getParameters(), out);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[ClientHandler] Connection closed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("[ClientHandler] Socket close error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
