package it.polimi.ingsw.componentTiles;

public class Cabin extends ComponentTile{
    private boolean isCentral;
    private LifeSupportSystem[] lifeSupportSystemColor;
    private Figure[] crew;

    public Cabin(ConnectorType[] connectors,Direction direction, LifeSupportSystem[] lifeSupportSystemColor, Figure[] crew) {
        super(connectors, direction);
        this.lifeSupportSystemColor = lifeSupportSystemColor;
        this.crew = crew;
    }

    public boolean isCentral() {
        return isCentral;
    }

    public LifeSupportSystem[] getLifeSupportSystemColor() {
        return lifeSupportSystemColor;
    }

    public Figure[] getCrew() {
        return crew;
    }
}
