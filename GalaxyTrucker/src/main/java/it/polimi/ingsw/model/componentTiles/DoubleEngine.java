package it.polimi.ingsw.model.componentTiles;

public class DoubleEngine extends Engine {
    private boolean isCharged;


    public DoubleEngine(ConnectorType[] connectors,String id, int power,boolean isCharged) {
        super(connectors,id,power);
        this.isCharged = isCharged;
    }

    public boolean isCharged() {
        return isCharged;
    }

    public int getPower() {

        return power*2;

    }
}
