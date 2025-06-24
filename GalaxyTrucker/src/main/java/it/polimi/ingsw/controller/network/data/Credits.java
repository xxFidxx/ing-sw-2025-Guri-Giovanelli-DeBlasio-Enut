package it.polimi.ingsw.controller.network.data;

public class Credits extends DataContainer {
    private int credits;

    public Credits(int c) {
        this.credits = c;
    }

    public int getCredits() {
        return credits;
    }
}
