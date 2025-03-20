package it.polimi.ingsw.game;

public class Timer {
    private int time;
    private long startTime;
    private boolean done;

    public Timer(int time, boolean done) {
        this.time = time;
        this.startTime = System.currentTimeMillis();;
        this.done = done;
    }

    public int getTime() {
        return time;
    }

    public void update() {
        long now = System.currentTimeMillis();
        int elapsedSeconds = (int) ((now - startTime) / 1000); /
        time = Math.max(0, time - elapsedSeconds);
    }

    public boolean isDone() {
        if(time == 0)
            return true;
        return false;
    }

    public void reset() {
        time = (int) startTime / 1000;
    }
}
