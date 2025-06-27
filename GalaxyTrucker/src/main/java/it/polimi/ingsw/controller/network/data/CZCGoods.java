package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.bank.GoodsBlock;

import java.io.Serial;
import java.io.Serializable;

public class CZCGoods extends DataContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private int level;
    int lostDays;
    int lostGoods;


    public CZCGoods(String name, int level, int lostDays, int lostGoods) {
        super();
        this.name = name;
        this.level = level;
        this.lostDays = lostDays;
        this.lostGoods = lostGoods;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getLostDays() { return lostDays; }

    public int getLostGoods() { return lostGoods; }

}
