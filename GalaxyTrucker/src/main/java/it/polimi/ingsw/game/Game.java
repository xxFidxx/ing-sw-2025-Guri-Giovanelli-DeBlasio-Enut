package it.polimi.ingsw.game;

import it.polimi.ingsw.adventureCards.AdventureCard;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

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

    public Player choosePlayer(AdventureCard card) {
        Player[] tmp = players;
        Arrays.sort(tmp, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        for (int i = tmp.length -1; i >= 0; i--) {
            if(card.checkCondition())
                if(tmp[i].getResponse())
                    return tmp[i];
        }
        return null;
    }

    public int throwDices(){
        Dice dice1 = new Dice;
        Dice dice2 = new Dice;
        return dice1.thr() + dice2.thr();
    }

    public void cargoManagement(Player player){

        if(checkStorage(player) == false){
            System.out.println("Not enough space");
            return;
        }







    }




}
