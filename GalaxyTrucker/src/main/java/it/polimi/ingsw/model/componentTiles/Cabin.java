package it.polimi.ingsw.model.componentTiles;

import java.util.ArrayList;

public class Cabin extends ComponentTile{
    private boolean isCentral;
    private LifeSupportSystem[] lifeSupportSystemColor;
    private Figure[] figures;;

    public Cabin(ConnectorType[] connectors, boolean isCentral, int id) {
        super(connectors,id);
        this.isCentral = isCentral;
        figures = new Figure[2];
    }

    public boolean isCentral() {
        return isCentral;
    }

    public LifeSupportSystem[] getLifeSupportSystemColor() {
        return lifeSupportSystemColor;
    }

    /* public LifeSupportSystem[] setLifeSupportSystemColor(int i, ){

    } */

    public Figure[] getFigures() {
        return figures;
    }

    @Override
    public String toString() {
        return super.toString() + "isCentral=" + isCentral;
    }
}
