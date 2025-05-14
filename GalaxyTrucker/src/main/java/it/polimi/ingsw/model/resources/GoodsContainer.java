package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.bank.GoodsBlock;

import java.io.Serializable;

public class GoodsContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private GoodsBlock[] goods;
    private boolean isSpecial;
    int id;
    public GoodsContainer(GoodsBlock[] goods, boolean isSpecial, int id) {
        this.goods = goods;
        this.isSpecial = isSpecial;
        this.id = id;
    }

    public GoodsBlock[] getGoods() {
        return goods;
    }

    public int getId() {
        return id;
    }

    public boolean isSpecial() {
        return isSpecial;
    }
}
