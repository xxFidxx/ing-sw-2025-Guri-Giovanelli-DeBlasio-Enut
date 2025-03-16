package it.polimi.ingsw;

public class Game {
    private Players[] player;
    private Timer timer;
    private Dice[] dices;
    private Flightplance plance;

    public Startgame(){}

    public Players getPlayer() {
        return player;
    }

    public Dice getDice() {
        return dice;
    }

    public Timer getTimer() {
        return timer;
    }
}
