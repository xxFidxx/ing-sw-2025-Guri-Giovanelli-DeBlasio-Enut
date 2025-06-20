package it.polimi.ingsw.model.game;

public class Timer {
    private int time;
    private long startTimeMillis;
    private boolean done;

    public Timer(int time) {
        this.time = time;
        this.done = true;
    }

    public void start() {
        this.startTimeMillis = System.currentTimeMillis();
        this.done = false;
    }

    public int getRemainingTime() {
        if (done) return time;
        long now = System.currentTimeMillis();
        int currTime = (int)((now - startTimeMillis) / 1000);
        return Math.max(0, time - currTime);
    }

    public boolean isDone() {
        return getRemainingTime() == 0;
    }

    public void reset() {
        start();
    }
}