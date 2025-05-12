package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.bank.GoodsBlock;

import java.io.Serializable;

public class GoodsContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private GoodsBlock[] goods;
    private boolean isSpecial;
    public GoodsContainer(GoodsBlock[] goods, boolean isSpecial) {
        this.goods = goods;
        this.isSpecial = isSpecial;
    }

    public GoodsBlock[] getGoods() {
        return goods;
    }

    public boolean isSpecial() {
        return isSpecial;
    }
}
