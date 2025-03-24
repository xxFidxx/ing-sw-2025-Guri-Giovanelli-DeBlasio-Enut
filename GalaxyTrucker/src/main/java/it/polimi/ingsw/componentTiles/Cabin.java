package it.polimi.ingsw.componentTiles;

import java.util.ArrayList;

public class Cabin extends ComponentTile{
    private boolean isCentral;
    private LifeSupportSystem[] lifeSupportSystemColor;
    private ArrayList<Figure> crew;

    public Cabin(ConnectorType[] connectors,Direction direction, LifeSupportSystem[] lifeSupportSystemColor, ArrayList<Figure> crew) {
        super(connectors);
        this.lifeSupportSystemColor = lifeSupportSystemColor;
        this.crew = crew;
    }

    public boolean isCentral() {
        return isCentral;
    }

    public LifeSupportSystem[] getLifeSupportSystemColor() {
        return lifeSupportSystemColor;
    }

    public ArrayList<Figure> getCrew() {
        return crew;
    }
}
