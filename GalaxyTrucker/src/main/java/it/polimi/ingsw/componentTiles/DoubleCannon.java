package it.polimi.ingsw.componentTiles;

public class DoubleCannon extends Cannon{
    private boolean isCharged;

    public DoubleCannon(ConnectorType[] connectors,Direction direction,float power,boolean isCharged) {
        super(connectors, direction,power);
        this.isCharged = isCharged;
    }

    public boolean isCharged() {
        return isCharged;
    }
}
