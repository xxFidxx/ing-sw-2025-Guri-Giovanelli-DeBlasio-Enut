package it.polimi.ingsw.componentTiles;

public class DoubleEngine extends Engine {
    private boolean isCharged;


    public DoubleEngine(ConnectorType[] connectors,Direction direction, int power,boolean isCharged) {
        super(connectors, direction,power);
        this.isCharged = isCharged;
    }

    public boolean isCharged() {
        return isCharged;
    }

    public int getPower() {

        return power*2;

    }
}
