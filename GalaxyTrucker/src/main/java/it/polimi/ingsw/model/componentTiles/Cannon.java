package it.polimi.ingsw.model.componentTiles;

import java.io.Serializable;

/**
 * The {@code Cannon} class represents a specific type of {@link ComponentTile}
 * that has an additional characteristic: power. This power can vary based on the type
 * of connector in the first position of the array of connectors.
 *
 * <p>The {@code Cannon} class extends {@link ComponentTile} and provides specific
 * logic for accessing and modifying the power of the cannon.</p>
 */
public class Cannon extends ComponentTile {

    /**
     * The power of the cannon. By default, it is set to 1.0.
     */
    protected float power;

    /**
     * Constructs a {@code Cannon} object with the specified connectors and ID.
     *
     * @param connectors An array of {@link ConnectorType} objects that represent the connectors of the cannon.
     * @param id The unique identifier for this cannon object.
     */
    public Cannon(ConnectorType[] connectors, int id) {
        super(connectors, id);
        this.power = 1.0F;
    }

    /**
     * Returns the power of the cannon, which can vary based on the type of connector
     * in the first position of the {@code connectors} array.
     *
     * <p>If the first connector is of type {@link ConnectorType#CANNON}, the cannon
     * returns its full power. Otherwise, it returns half of its power.</p>
     *
     * @return The effective power of the cannon as a float value.
     */
    public float getPower() {
        if (connectors[0] == ConnectorType.CANNON) {
            return power;
        } else {
            return (float) (power * 0.5);
        }
    }

    /**
     * Sets a new power value for the cannon.
     *
     * @param power The new power value to be assigned.
     */
    public void setPower(float power) {
        this.power = power;
    }
}