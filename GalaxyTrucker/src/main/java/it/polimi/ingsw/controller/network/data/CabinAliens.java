package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.Cabin;

import java.io.Serializable;
import java.util.ArrayList;

public class CabinAliens extends DataContainer implements Serializable {
    private Cabin cabin;
    private boolean brown;
    private boolean purple;
    public CabinAliens(Cabin cabin, boolean brown, boolean purple) {
        this.cabin = cabin;
        this.brown = brown;
        this.purple = purple;
    }
    public Cabin getCabin() {
        return cabin;
    }

    public boolean isPurple() {
        return purple;
    }

    public boolean isBrown() {
        return brown;
    }
}
