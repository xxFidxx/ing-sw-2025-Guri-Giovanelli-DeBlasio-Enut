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

    public Game(Player[] player, Timer timer, Dice[] dices, Flightplance plance) {
        this.players = players;
        this.timer = timer;
        this.dices = dices;
        this.plance = plance;
    }

    public void Startgame() {
    }

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
        for (int i = tmp.length - 1; i >= 0; i--) {
            if (card.checkCondition())
                if (tmp[i].getResponse())
                    return tmp[i];
        }
        return null;
    }

    public int throwDices() {
        Dice dice1 = new Dice();
        Dice dice2 = new Dice();
        return dice1.thr() + dice2.thr();
    }

    /**
     * @param player
     * @param cardReward
     */

    public void cargoManagement(Player player, GoodsBlock[] cardReward) {

        if (!player.checkStorage()) {
            System.out.println("Not enough space");
            return;
        }

        ArrayList<CargoHolds> playerCargos = player.getSpaceshipPlance().getCargoHolds();

        // indicizza i playerCargos
        // metti dentro al while le variabili cargoindex1 cargoindex2 goodindex1 goodindex2
        //metti metodo checkswap / implementalo in swap ( per dire che non puoi metter merci rosse in cargo normali
        // metti remove ( non puoi rimuovere dal cardReward)

        while ("player is done" == false) {
            int i1 = 0; // cargo index
            int i2 = -1;
            int j1 = 0; // good index
            int j2 = 0;
            int k = 0; //card reward's good index

            if ("player input is swap" == true) {


                if (i1 >= 0 && i1 < playerCargos.size() && i2 >= 0 && i2 < playerCargos.size()) {

                    CargoHolds cargo1 = playerCargos.get(i1);
                    CargoHolds cargo2 = playerCargos.get(i2);

                    if (j1 >= 0 && j1 < cargo1.getGoods().length && j2 >= 0 && j2 < cargo2.getGoods().length) {
                        GoodsBlock good1 = cargo1.getGoods()[j1];
                        GoodsBlock good2 = cargo2.getGoods()[j2];


                        if (checkSpecialGoods(cargo1,cargo2,good1,good2))
                            swapGoods(cargo1, cargo2, j1, j2);
                    } else {
                        System.out.println("At least one goods index is outbound");
                    }


                } else {
                    System.out.println("At least one cargo index is outbound");

                }
            } else if ("player input is remove" == true) {
                if (i1 >= 0 && i1 < playerCargos.size()) {
                    CargoHolds cargo1 = playerCargos.get(i1);
                    if(j1 >= 0 && j1 < cargo1.getGoods().length) {
                        removeGoods(cargo1, j1);
                    }else
                        System.out.println("goods index is outbound");
                } else
                    System.out.println("cargo index is outbound");

            } else if ("player input is add" == true) {
                if(i1 >= 0 && i1 < playerCargos.size()) {
                    CargoHolds cargo1 = playerCargos.get(i1);
                    if(j1 >= 0 && j1 < cargo1.getGoods().length && k>=0 && k < cardReward.length) {
                        GoodsBlock good1 = cargo1.getGoods()[j1];
                        GoodsBlock good2 = cardReward[k];
                        if (good1 == null) {
                            if (checkSpecialGoods(cargo1,good2))
                                addGoods(cargo1,cardReward,j1,k);
                        } else {
                            System.out.println("You can't add on a busy spot");

                        }
                    }else
                        System.out.println("At least one goods index is outbound");
                }else
                    System.out.println("cargo index is outbound");
            } else
                System.out.println("player input is incorrect");
        }
    }






    private void swapGoods(CargoHolds cargo1, CargoHolds cargo2, int j1, int j2) {

        GoodsBlock[] goods1 = cargo1.getGoods();
        GoodsBlock[] goods2 = cargo2.getGoods();

        GoodsBlock temp = goods1[j1];
        goods1[j1] = goods2[j2];
        goods2[j2] = temp;

    }

    private boolean checkSpecialGoods(CargoHolds cargo1, CargoHolds cargo2, GoodsBlock good1, GoodsBlock good2) {

        if ((good1.getType() == RED && !cargo2.isSpecial()) || (good2.getType() == RED && !cargo1.isSpecial())) {
            System.out.println("Can't put a Red block in grey cargo");
            return true;

        }
        return false;

    }

    private boolean checkSpecialGoods(CargoHolds cargo, GoodsBlock good) {

        if ((good.getType() == RED && !cargo.isSpecial())) {
            System.out.println("Can't put a Red block in grey cargo");
            return true;

        }
        return false;

    }

    private void removeGoods(CargoHolds cargo1, int j1) {

        cargo1.getGoods()[j1] = null;

    }

    private void addGoods(CargoHolds cargo1,GoodsBlock[] cardReward,int j1, int k) {

        cargo1.getGoods()[j1] = cardReward[k];
        cardReward[k] = null;

    }

}

