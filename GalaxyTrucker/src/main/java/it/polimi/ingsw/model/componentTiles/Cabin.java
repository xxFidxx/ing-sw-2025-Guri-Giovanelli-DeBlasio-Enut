package it.polimi.ingsw.model.componentTiles;

import java.util.ArrayList;

public class Cabin extends ComponentTile{
    private boolean isCentral;
    private LifeSupportSystem[] lifeSupportSystemColor;
    private Figure[] figures;;

    public Cabin(ConnectorType[] connectors,int id) {
        super(connectors,id);
        figures = new Figure[1];
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
}
