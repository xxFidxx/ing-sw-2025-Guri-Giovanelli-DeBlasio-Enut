/**
 * Represents a specialized cannon component with enhanced functionality.
 * The DoubleCannon extends the base Cannon functionality by adding
 * a "charged" state and adjusting the power calculation.
 */
package it.polimi.ingsw.model.componentTiles;

import java.io.Serializable;

public class DoubleCannon extends Cannon {
    /**
     * Indicates whether the DoubleCannon is charged.
     */
    private boolean isCharged;

    /**
     * Constructs a DoubleCannon with the given connectors and identifier.
     *
     * @param connectors an array of {@link ConnectorType} elements representing the connectors of the cannon.
     * @param id the unique identifier of the cannon.
     */
    public DoubleCannon(ConnectorType[] connectors, int id) {
        super(connectors, id);
        this.isCharged = false;
    }

    /**
     * Checks whether the DoubleCannon is currently charged.
     *
     * @return {@code true} if the cannon is charged, {@code false} otherwise.
     */
    public boolean isCharged() {
        return isCharged;
    }

    /**
     * Calculates and retrieves the power of the cannon.
     * When the first connector is of type {@link ConnectorType#CANNON},
     * the power is doubled; otherwise, it returns the base power.
     *
     * @return the calculated power of the cannon.
     */
    public float getPower() {
        if (connectors[0] == ConnectorType.CANNON)
            return 2 * power;
        else
            return power;
    }

    /**
     * Sets whether the DoubleCannon is charged.
     *
     * @param isCharged {@code true} to charge the cannon, {@code false} to discharge it.
     */
    public void setCharged(boolean isCharged) {
        this.isCharged = isCharged;
    }

    /**
     * Sets the base power of the cannon.
     *
     * @param power the new base power of the cannon.
     */
    public void setPower(float power) {
        this.power = power;
    }
}