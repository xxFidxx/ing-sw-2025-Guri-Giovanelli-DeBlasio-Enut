package it.polimi.ingsw.model.componentTiles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Cabin extends ComponentTile implements Serializable {
    private final boolean isCentral;
    private final AlienColor[] lifeSupportSystemColors;
    private final Figure[] figures;

    public Cabin(ConnectorType[] connectors, boolean isCentral, int id) {
        super(connectors,id);
        this.isCentral = isCentral;
        figures = new Figure[2];
        for(int i = 0; i < figures.length; i++){
            figures[i] = new Astronaut(i);
        }
        // I assign the length of the enum, so if we add or remove colors it works anyway
        lifeSupportSystemColors = new AlienColor[AlienColor.values().length];
    }

    public boolean isCentral() {
        return isCentral;
    }

    public AlienColor[] getLifeSupportSystemColor() {
        return lifeSupportSystemColors;
    }

    public Figure[] getFigures() {
        return figures;
    }

    @Override
    public String toString() {
        return super.toString() + "isCentral=" + isCentral + " id: " + getId() + Arrays.toString(lifeSupportSystemColors);
    }
}
