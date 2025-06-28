package it.polimi.ingsw.model.componentTiles;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The {@code Cabin} class represents a specific type of {@link ComponentTile} in the game.
 * It contains additional properties such as whether the cabin is central,
 * the life support system colors, and the figures present in the cabin.
 * It extends the {@link ComponentTile} class and implements {@link Serializable} for object serialization.
 */
public class Cabin extends ComponentTile implements Serializable {

    /**
     * Indicates whether the cabin is central.
     */
    private final boolean isCentral;

    /**
     * Array containing colors for the life support system.
     */
    private final AlienColor[] lifeSupportSystemColors;

    /**
     * Array containing the figures present in the cabin.
     * By default, it is populated with {@link Astronaut} instances.
     */
    private final Figure[] figures;

    /**
     * Constructs a new {@code Cabin} instance.
     *
     * @param connectors an array of {@link ConnectorType} representing the connections of the cabin.
     * @param isCentral  a boolean indicating whether the cabin is central or not.
     * @param id         an integer identifying the cabin.
     */
    public Cabin(ConnectorType[] connectors, boolean isCentral, int id) {
        super(connectors, id);
        this.isCentral = isCentral;
        figures = new Figure[2];
        for (int i = 0; i < figures.length; i++) {
            figures[i] = new Astronaut(i);
        }
        // Initialize the array for life support system colors based on the number of enum values.
        lifeSupportSystemColors = new AlienColor[AlienColor.values().length];
    }

    /**
     * Checks if the cabin is central.
     *
     * @return {@code true} if the cabin is central; {@code false} otherwise.
     */
    public boolean isCentral() {
        return isCentral;
    }

    /**
     * Retrieves the array of life support system colors.
     *
     * @return an array of {@link AlienColor} representing the life support system colors.
     */
    public AlienColor[] getLifeSupportSystemColor() {
        return lifeSupportSystemColors;
    }

    /**
     * Retrieves the figures present in the cabin.
     *
     * @return an array of {@link Figure} containing the figures in the cabin.
     */
    public Figure[] getFigures() {
        return figures;
    }

    /**
     * Returns a string representation of the cabin.
     * Includes information about whether the cabin is central, its ID, and the contained figures.
     *
     * @return a {@code String} representation of this object.
     */
    @Override
    public String toString() {
        return super.toString() + "isCentral=" + isCentral + " id: " + getId() + Arrays.toString(figures);
    }
}