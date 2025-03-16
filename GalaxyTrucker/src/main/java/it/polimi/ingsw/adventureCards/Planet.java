package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.Bank.GoodsBlock;

import java.util.List;

public class Planet {
    private List<GoodsBlock> goods;
    boolean isBusy;

    public Planet(List<GoodsBlock> goods, boolean isBusy) {
        this.goods = goods;
        this.isBusy = isBusy;
    }

    public List<GoodsBlock> getReward(){
        return goods;
    }

    public boolean isBusy(){
        return isBusy;
    }
}
