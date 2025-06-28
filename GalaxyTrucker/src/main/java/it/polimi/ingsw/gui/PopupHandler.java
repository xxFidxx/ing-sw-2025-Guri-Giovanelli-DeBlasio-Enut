package it.polimi.ingsw.gui;

import javafx.application.Platform;

import java.util.LinkedList;
import java.util.Queue;

public class PopupHandler {

    private static PopupHandler instance = null;

    private final Queue<Runnable> queue = new LinkedList<>();
    private boolean isRunning = false;

    private PopupHandler() {
        // used only for getInstance for static method
    }

    public static PopupHandler getInstance() {
        if (instance == null) {
            instance = new PopupHandler();
        }
        return instance;
    }

    public synchronized void enqueue(Runnable popupAction) {
        queue.add(popupAction);
        if (!isRunning) {
            isRunning = true;
            showNext();
        }
    }

    private void showNext() {
        if (queue.isEmpty()) {
            isRunning = false;
            return;
        }

        Runnable popup = queue.poll();
        Platform.runLater(popup);
    }

    public synchronized void dequeue() {
        showNext();
    }
}
