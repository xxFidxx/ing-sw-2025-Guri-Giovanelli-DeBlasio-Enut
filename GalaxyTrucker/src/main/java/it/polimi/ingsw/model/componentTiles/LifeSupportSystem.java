package it.polimi.ingsw.model.componentTiles;

/**
 * Represents the Life Support System tile in the game. This tile is a type of {@link ComponentTile}
 * and has an associated color to distinguish it from other tiles.
 */
public class LifeSupportSystem extends ComponentTile {
    /**
     * The color of the Life Support System, represented by {@link AlienColor}.
     */
    private final AlienColor color;

    /**
     * Constructs a new Life Support System with the specified color, connectors, and ID.
     *
     * @param color      The color assigned to the Life Support System (must not be null).
     * @param connectors An array of {@link ConnectorType} objects describing the connectors of the tile (must not be null).
     * @param id         The unique identifier for this tile.
     */
    public LifeSupportSystem(AlienColor color, ConnectorType[] connectors, int id) {
        super(connectors, id);
        this.color = color;
    }

    /**
     * Gets the color of this Life Support System tile.
     *
     * @return The {@link AlienColor} of the tile.
     */
    public AlienColor getColor() {
        return color;
    }

    /**
     * Returns the string representation of this Life Support System tile.
     * It includes the string representation of the parent class and the color of the tile.
     *
     * @return A {@link String} representing the Life Support System tile.
     */
    @Override
    public String toString() {
        return super.toString() + "color=" + color;
    }
}