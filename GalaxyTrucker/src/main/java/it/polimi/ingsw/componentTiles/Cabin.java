package it.polimi.ingsw.componentTiles;

import java.util.ArrayList;

public class Cabin extends ComponentTile{
    private boolean isCentral;
    private LifeSupportSystem[] lifeSupportSystemColor;
    private Figure[] figures;;

    public Cabin(ConnectorType[] connectors,Direction direction, LifeSupportSystem[] lifeSupportSystemColor, ArrayList<Figure> crew) {
        super(connectors);
        this.lifeSupportSystemColor = lifeSupportSystemColor;
        figures = new Figure[1];
    }

    public boolean isCentral() {
        return isCentral;
    }

    public LifeSupportSystem[] getLifeSupportSystemColor() {
        return lifeSupportSystemColor;
    }

    public LifeSupportSystem[] setLifeSupportSystemColor(int i, ){

    }

    public Figure[] getFigures() {
        return figures;
    }
}
