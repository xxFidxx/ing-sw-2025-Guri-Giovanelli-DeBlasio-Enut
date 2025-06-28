package it.polimi.ingsw.model.componentTiles;

/**
 * Represents an Astronaut figure in the game. This figure is a type of {@link Figure}
 * and inherits its behavior while providing specific functionality.
 */
public class Astronaut extends Figure {

    /**
     * Constructs a new Astronaut with the specified number of slots.
     *
     * @param slots The number of slots associated with the astronaut.
     */
    public Astronaut(int slots) {
        super(slots);
    }

    /**
     * Returns a string representation of this Astronaut.
     *
     * @return A {@link String} identifying this figure as an "Astronaut".
     */
    @Override
    public String toString() {
        return "Astronaut";
    }
}