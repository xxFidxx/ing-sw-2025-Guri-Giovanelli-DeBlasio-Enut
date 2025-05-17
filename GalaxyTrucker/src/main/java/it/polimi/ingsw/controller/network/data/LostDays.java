package it.polimi.ingsw.controller.network.data;

public class LostDays extends DataContainer{
    private int ld;

    public LostDays(int ld) {
        this.ld = ld;
    }

    public int getLd() {
        return ld;
    }
}

