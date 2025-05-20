package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.resources.GoodsContainer;

import java.io.Serializable;
import java.util.ArrayList;

public class RemoveMostValuable extends DataContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private int nGoods;
    private ArrayList<GoodsContainer> cargos;
    public RemoveMostValuable(int nGoods, ArrayList<GoodsContainer> cargos) {
        this.nGoods = nGoods;
        this.cargos = cargos;
    }

    public int getNGoods() {
        return nGoods;
    }

    public ArrayList<GoodsContainer> getCargos() {
        return cargos;
    }
}
