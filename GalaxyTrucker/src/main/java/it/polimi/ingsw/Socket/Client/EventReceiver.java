package it.polimi.ingsw.Socket.Client;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.data.ServerResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class EventReceiver implements Runnable {
    private final Socket socket;
    private final ObjectInputStream in;
    private final LinkedBlockingQueue<Event> eventQueue;
    private final LinkedBlockingQueue<Event> responseQueue;

    public EventReceiver(Socket socket, LinkedBlockingQueue<Event> eventQueue, LinkedBlockingQueue <Event> responseQueue) throws IOException {
        this.socket = socket;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.eventQueue = eventQueue;
        this.responseQueue = responseQueue;
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                Event event = (Event) in.readObject();
                if (event != null) {
                    if(event.getState() == GameState.SERVER_RESPONSE)
                        responseQueue.put(event);
                    else
                        eventQueue.put(event);
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
