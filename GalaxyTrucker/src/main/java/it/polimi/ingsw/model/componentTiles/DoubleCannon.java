package it.polimi.ingsw.model.componentTiles;

public class DoubleCannon extends Cannon{
    private boolean isCharged;

    public DoubleCannon(ConnectorType[] connectors,String id,float power,boolean isCharged) {
        super(connectors, id, power);
        this.isCharged = isCharged;
    }

    public boolean isCharged() {
        return isCharged;
    }

    public float getPower() {
        if(isCharged) {
            if ( connectors[0] == ConnectorType.CANNON )
                return 2 * power;
            else
                return (power);
        }
        return 0;
    }
}
