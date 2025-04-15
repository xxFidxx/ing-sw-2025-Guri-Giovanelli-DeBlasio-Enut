package it.polimi.ingsw.resources;

import it.polimi.ingsw.bank.GoodsBlock;

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
