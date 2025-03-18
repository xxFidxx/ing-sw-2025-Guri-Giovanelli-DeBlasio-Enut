package it.polimi.ingsw.game;

public class Game {
    private Player[] players;
    private Timer timer;
    private Dice[] dices;
    private Flightplance plance;

    public Game(Player[] player,Timer timer,Dice[] dices,Flightplance plance) {
        this.players = players;
        this.timer = timer;
        this.dices = dices;
        this.plance = plance;
    }

    public void Startgame(){}

    public Player[] getPlayer() {
        return players;
    }

    public Dice[] getDice() {
        return dices;
    }

    public Timer getTimer() {
        return timer;
    }

    public Player choosePlayer() {
        return players[0];
    }
}
