package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.Bank.GoodsBlock;

import java.util.List;

public class Planet {
    private GoodsBlock[] goods;
    boolean isBusy;

    public Planet(GoodsBlock[] goods, boolean isBusy) {
        this.goods = goods;
        this.isBusy = isBusy;
    }

    public GoodsBlock[] getReward(){
        return goods;
    }

    public boolean isBusy(){
        return isBusy;
    }
}
