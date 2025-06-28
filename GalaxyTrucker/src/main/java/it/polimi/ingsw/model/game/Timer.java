package it.polimi.ingsw.model.game;

/**
 * Represents a timer that tracks the remaining time in seconds.
 * The timer can be started, reset, and checked if it has completed.
 */
public class Timer {
    private int time;
    private long startTimeMillis;
    private boolean done;

    /**
     * Constructs a Timer with a specified duration.
     *
     * @param time the duration of the timer in seconds
     */
    public Timer(int time) {
        this.time = time;
        this.done = true;
    }

    /**
     * Starts the timer. The timer begins counting down from the remaining time.
     */
    public void start() {
        this.startTimeMillis = System.currentTimeMillis();
        this.done = false;
    }

    /**
     * Returns the remaining time in seconds. If the timer is not running,
     * it will return the full duration or zero if the timer has completed.
     *
     * @return the remaining time in seconds, or 0 if the timer is done
     */
    public int getRemainingTime() {
        if (done) return time;
        long now = System.currentTimeMillis();
        int currTime = (int)((now - startTimeMillis) / 1000);
        return Math.max(0, time - currTime);
    }

    /**
     * Checks if the timer has finished counting down.
     *
     * @return {@code true} if the timer has completed (time is up), otherwise {@code false}
     */
    public boolean isDone() {
        return getRemainingTime() == 0;
    }

    /**
     * Resets and starts the timer from the initial duration.
     */
    public void reset() {
        start();
    }
}