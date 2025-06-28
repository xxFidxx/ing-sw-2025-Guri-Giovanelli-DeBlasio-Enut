package it.polimi.ingsw.model.componentTiles;

import java.util.Arrays;

/**
 * Represents a Shield Generator tile in the game. This tile is a subtype of {@link ComponentTile}
 * and provides protection in specific directions based on its configuration.
 */
public class ShieldGenerator extends ComponentTile {

    /**
     * An array that represents the protection status of this Shield Generator in four directions.
     * Each element corresponds to a specific direction, where `true` indicates protection is active
     * and `false` indicates no protection.
     * Example: [false, false, true, true]
     */
    private boolean[] protection;

    /**
     * Constructs a Shield Generator with the specified connectors, protection configuration, and ID.
     *
     * @param connectors An array of {@link ConnectorType} that specifies the connectors of the tile (must not be null).
     * @param protection An array of booleans that determines the initial protection status for each direction.
     * @param id         The unique identifier for this Shield Generator.
     */
    public ShieldGenerator(ConnectorType[] connectors, boolean[] protection, int id) {
        super(connectors, id);
        this.protection = protection;
    }

    /**
     * Rotates the Shield Generator and its protection settings clockwise.
     * This shifts the protection configuration, effectively changing which directions are protected.
     */
    public void rotateClockwise() {
        super.rotateClockwise();

        boolean last = protection[3];
        for (int i = 3; i > 0; i--) {
            protection[i] = protection[i - 1];
        }
        protection[0] = last;
    }

    /**
     * Rotates the Shield Generator and its protection settings counterclockwise.
     * This shifts the protection configuration in the opposite direction,
     * effectively changing which directions are protected.
     */
    public void rotateCounterClockwise() {
        super.rotateCounterClockwise();

        boolean first = protection[0];
        for (int i = 0; i < 3; i++) {
            protection[i] = protection[i + 1];
        }
        protection[3] = first;
    }

    /**
     * Checks whether protection is active in a specified direction.
     *
     * @param direction The direction to check, represented by {@link Direction}.
     *                  The direction must match an index in the {@code protection} array.
     * @return {@code true} if protection is active in the specified direction, {@code false} otherwise.
     */
    public boolean checkProtection(Direction direction) {
        return protection[direction.ordinal()];
    }

    /**
     * Returns the protection status for all directions.
     *
     * @return A {@code boolean[]} representing the protection state in the four directions.
     */
    public boolean[] getProtection() {
        return protection;
    }

    /**
     * Returns a string representation of this Shield Generator.
     * It combines the parent's string representation with the current protection status.
     *
     * @return A {@link String} representing the Shield Generator and its protection configuration.
     */
    @Override
    public String toString() {
        return super.toString() + "protection=" + Arrays.toString(protection);
    }
}