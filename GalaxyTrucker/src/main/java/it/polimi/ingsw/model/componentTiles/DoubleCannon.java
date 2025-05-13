package it.polimi.ingsw.model.componentTiles;

import java.io.Serializable;

public class DoubleCannon extends Cannon implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean isCharged;

    public DoubleCannon(ConnectorType[] connectors,int id) {
        super(connectors, id);
        this.isCharged = false;
    }

    public boolean isCharged() {
        return isCharged;
    }

    public float getPower() {
        if ( connectors[0] == ConnectorType.CANNON )
            return 2 * power;
        else
            return (power);
    }

    public void setCharged(boolean isCharged) {
        this.isCharged = isCharged;
    }

    public void setPower(float power) {
        this.power = power;
    }
}
