package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.bank.GoodsBlock;

import java.io.Serializable;
import java.util.Arrays;

public class Planet implements Serializable{
    private static final long serialVersionUID = 1L;
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

    @Override
    public String toString() {
        return "Planet{" +
                "goods=" + Arrays.toString(goods) +
                '}';
    }
    public void setBusy(boolean busy) {
        isBusy = busy;

    }
}