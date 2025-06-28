package it.polimi.ingsw.model.componentTiles;

/**
 * Represents an Alien figure in the game. This figure has an associated color and
 * a number of slots inherited from the {@link Figure} class.
 */
public class Alien extends Figure {
    /**
     * The color of the alien, represented by {@link AlienColor}.
     */
    private AlienColor color;

    /**
     * Constructs a new Alien with the specified number of slots and color.
     *
     * @param slots The number of slots associated with the alien.
     * @param color The color of the alien (must not be null).
     */
    public Alien(int slots, AlienColor color) {
        super(slots);
        this.color = color;
    }

    /**
     * Gets the color of this alien.
     *
     * @return The {@link AlienColor} of the alien.
     */
    public AlienColor getColor() {
        return color;
    }

    /**
     * Returns a string representation of this Alien.
     * It includes the color of the alien.
     *
     * @return A {@link String} describing the alien.
     */
    @Override
    public String toString() {
        return "Alien{" +
                "color=" + color +
                '}';
    }
}