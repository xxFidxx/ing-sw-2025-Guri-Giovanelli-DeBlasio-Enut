package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.resources.GoodsContainer;

import java.io.Serializable;
import java.util.ArrayList;

public class Cargos extends DataContainer implements Serializable {
    private ArrayList<GoodsContainer> cargos;

    public Cargos(ArrayList<GoodsContainer> cargos) {
        this.cargos = cargos;
    }

    public ArrayList<GoodsContainer> getCargos() {
        return cargos;
    }

}
