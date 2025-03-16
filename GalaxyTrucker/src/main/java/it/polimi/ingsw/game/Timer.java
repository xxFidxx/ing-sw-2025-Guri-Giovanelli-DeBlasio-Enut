package it.polimi.ingsw.game;

public class Timer {
    private int time;
    private boolean done;

    public Timer(int time, boolean done) {
        this.time = time;
        this.done = done;
    }

    public int getTime() {
        return time;
    }

    public boolean isDone() {
        return done;
    }

    public void reset() {}
}
