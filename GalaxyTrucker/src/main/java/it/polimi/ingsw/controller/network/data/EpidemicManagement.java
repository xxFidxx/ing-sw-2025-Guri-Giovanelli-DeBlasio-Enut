package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.Cabin;

import java.io.Serializable;
import java.util.ArrayList;

public class EpidemicManagement extends DataContainer implements Serializable {
    private ArrayList<Cabin> cabins;

    public EpidemicManagement(ArrayList<Cabin> cabins, int lostCrew) {
        this.cabins = cabins;
    }

    public ArrayList<Cabin> getCabins() {
        return cabins;
    }
}
