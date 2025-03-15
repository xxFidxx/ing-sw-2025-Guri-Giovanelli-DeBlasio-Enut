package it.polimi.ingsw.componentTiles;

public abstract class Figure {
    private int slots;

    public Figure(int slots) {
        this.slots = slots;
    }

    public int getSlots() {
        return slots;
    }

}
