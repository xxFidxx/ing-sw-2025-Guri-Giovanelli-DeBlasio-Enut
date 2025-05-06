package it.polimi.ingsw.model.componentTiles;

import java.util.Arrays;

public class ShieldGenerator extends ComponentTile {

    private boolean[] protection; // ex: [false, false, true, true]

    public ShieldGenerator(ConnectorType[] connectors,boolean[] protection, int id) {
        super(connectors,id);
        this.protection = protection;
    }

    public void rotateClockwise() {
        super.rotateClockwise();

        boolean last = protection[3];

        for (int i = 3; i > 0; i--) {
            protection[i] = protection[i-1];
        }

        protection[0] = last;
    }

    public void rotateCounterClockwise() {
        super.rotateCounterClockwise();
        boolean first = protection[0];

        for (int i = 0; i < 3; i++) {
            protection[i] = protection[i+1];
        }

        protection[3] = first;
    }

    // da vedere la logica cosa fa
     public boolean checkProtection(Direction direction) {
        return protection[direction.ordinal()];
     }

    public boolean[] getProtection() {
        return protection;
    }

    @Override
    public String toString() {
        return super.toString() + "protection=" + Arrays.toString(protection);
    }
}
