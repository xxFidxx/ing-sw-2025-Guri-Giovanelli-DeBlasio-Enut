package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.DoubleCannon;

import java.io.Serializable;
import java.util.ArrayList;

public class DoubleCannonList extends DataContainer implements Serializable {
    private ArrayList<DoubleCannon> doubleCannons;

    public DoubleCannonList(ArrayList<DoubleCannon> doubleCannons) {
        this.doubleCannons = doubleCannons;
    }

    public ArrayList<DoubleCannon> getDoubleCannons() {
        return doubleCannons;
    }
}
