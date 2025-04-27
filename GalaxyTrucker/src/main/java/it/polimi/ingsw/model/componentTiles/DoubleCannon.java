package it.polimi.ingsw.model.componentTiles;

public class DoubleCannon extends Cannon{
    private boolean isCharged;

    public DoubleCannon(ConnectorType[] connectors,int id) {
        super(connectors, id);
        this.isCharged = false;
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

    public void setCharged(boolean isCharged) {
        this.isCharged = isCharged;
    }

    public void setPower(float power) {
        this.power = power;
    }
}
