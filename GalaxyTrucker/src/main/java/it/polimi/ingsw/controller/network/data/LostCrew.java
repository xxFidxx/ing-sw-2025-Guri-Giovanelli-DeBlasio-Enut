package it.polimi.ingsw.controller.network.data;

public class LostCrew extends DataContainer {
    private int lc;

    public LostCrew(int lc) {
        this.lc = lc;
    }

    public int getLc() {
        return lc;
    }
}
