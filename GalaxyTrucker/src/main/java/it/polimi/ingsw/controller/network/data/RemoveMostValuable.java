package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.resources.GoodsContainer;

import java.io.Serializable;
import java.util.ArrayList;

public class RemoveMostValuable extends DataContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private int nGoods;
    private ArrayList<GoodsContainer> cargos;
    private int batteriesToRemove;

    public RemoveMostValuable(int nGoods, ArrayList<GoodsContainer> cargos, int batteriesToRemove) {
        this.nGoods = nGoods;
        this.cargos = cargos;
        this.batteriesToRemove = batteriesToRemove;
    }

    public int getNGoods() {
        return nGoods;
    }

    public ArrayList<GoodsContainer> getCargos() {
        return cargos;
    }

    public int getBatteriesToRemove() {
        return batteriesToRemove;
    }
}
