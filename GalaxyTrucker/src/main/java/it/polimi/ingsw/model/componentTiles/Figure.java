package it.polimi.ingsw.model.componentTiles;

/**
 * The {@code Figure} class represents an abstract concept of a figure with a defined number of slots.
 * This class is meant to be extended by other classes that specify the behavior or properties of a particular type of figure.
 * <p>
 * It implements {@link java.io.Serializable} to allow objects of subclasses to be serialized and deserialized.
 */
public abstract class Figure implements java.io.Serializable {

    /**
     * The number of slots associated with this figure.
     */
    private int slots;

    /**
     * Constructs a new instance of {@code Figure} with the specified number of slots.
     *
     * @param slots the number of slots for this figure
     */
    public Figure(int slots) {
        this.slots = slots;
    }

    /**
     * Gets the number of slots associated with this figure.
     *
     * @return the number of slots
     */
    public int getSlots() {
        return slots;
    }

}