package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.bank.BatteryToken;

import java.util.Arrays;

/**
 * Represents a Power Center tile in the game. This tile is a subtype of {@link ComponentTile}
 * and contains a specified number of battery slots that can hold power.
 */
public class PowerCenter extends ComponentTile {
    /**
     * An array representing the battery slots in the Power Center.
     * Each slot can either be full (true) or empty (false).
     */
    private final boolean[] batteries;

    /**
     * Constructs a new Power Center tile with the specified connector types, capacity for batteries, and ID.
     *
     * @param connectors An array of {@link ConnectorType} specifying the connectors of the tile (must not be null).
     * @param capacity   The number of battery slots in this Power Center.
     * @param id         The unique identifier for this tile.
     */
    public PowerCenter(ConnectorType[] connectors, int capacity, int id) {
        super(connectors, id);
        batteries = new boolean[capacity];
        Arrays.fill(batteries, true);
    }

    /**
     * Returns the array representing the battery slots in the Power Center.
     *
     * @return A {@code boolean[]} where each element indicates whether a battery slot is full (true) or empty (false).
     */
    public boolean[] getBatteries() {
        return batteries;
    }

    /**
     * Returns the string representation of this Power Center tile.
     * It includes the parent's string representation along with the tile's ID and the status of its battery slots.
     *
     * @return A {@link String} representing the Power Center tile.
     */
    @Override
    public String toString() {
        return super.toString() + "id: " + getId() + "batteries= " + Arrays.toString(batteries);
    }
}