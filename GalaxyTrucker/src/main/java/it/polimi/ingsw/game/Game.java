package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.adventureCards.AdventureCard;
import it.polimi.ingsw.componentTiles.CargoHolds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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

    public Player[] getPlayers() {
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

    public void cargoManagement(Player player, GoodsBlock[] cardReward){

        if(player.checkStorage() == false){
            System.out.println("Not enough space");
            return;
        }

        ArrayList<CargoHolds> playerCargos = player.getSpaceshipPlance().getCargoHolds();

        // indicizza i playerCargos
        // metti dentro al while le variabili cargoindex1 cargoindex2 goodindex1 goodindex2
        //metti metodo checkswap / implementalo in swap ( per dire che non puoi metter merci rosse in cargo normali
        // metti remove ( non puoi rimuovere dal cardReward)

        while("player is done" != null){

            if("player input is swap" == true){

            }

            if("player input is remove" == true){

            }

            if("player input is else" == true){
                System.out.println("player input is incorrect");
            }
        }







    }




}
