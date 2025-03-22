package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.adventureCards.AdventureCard;
import it.polimi.ingsw.componentTiles.CargoHolds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static it.polimi.ingsw.game.ColorType.RED;

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
        Dice dice1 = new Dice();
        Dice dice2 = new Dice();
        return dice1.thr() + dice2.thr();
    }

    /**
     * @param player
     * @param cardReward
     */

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
            int i1 =0; // cargo index
            int i2 =-1;
            int j1=0; // good index
            int j2=0;
            int k = 0; //card reward's good index
            if("player input is swap" == true){
                CargoHolds cargo1 =playerCargos.get(i1);
                CargoHolds cargo2 =playerCargos.get(i2);

                GoodsBlock good1 = cargo1.getGoods()[j1];
                GoodsBlock good2 = cargo2.getGoods()[j2];


                /** check of**/
                if((good1.getType() == RED && !cargo2.isSpecial()) || (good2.getType() == RED && !cargo1.isSpecial())){
                    System.out.println("Can't put a Red block in grey cargo");

                }else{
                    GoodsBlock tmp = good1;
                    good1 = good2;
                    good2 = tmp;

                }


            }

            if("player input is remove" == true){
                GoodsBlock good1 = playerCargos.get(i1).getGoods()[j1];
                good1 = null;
            }

            if("player input is add" == true){
                CargoHolds cargo1 =playerCargos.get(i1);
                GoodsBlock good1 = cargo1.getGoods()[j1];
                GoodsBlock good2 = cardReward[k];

                if(good1 != null){
                    System.out.println("You can't add on a busy spot");
                }else{
                    if(good2.getType() == RED && !cargo1.isSpecial()){
                        System.out.println("Can't put a Red block in grey cargo");
                    }else{
                        good1 = good2;
                        good2 = null;
                    }
                }
            }

            if("player input is else" == true){
                System.out.println("player input is incorrect");
            }
        }







    }




}
