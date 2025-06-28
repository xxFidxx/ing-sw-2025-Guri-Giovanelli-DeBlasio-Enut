package it.polimi.ingsw.model.componentTiles;

/**
 * The {@code DoubleEngine} class represents a specialized type of engine
 * that doubles the power of a standard engine and can be charged.
 * This class extends the base {@link Engine} class.
 */
public class DoubleEngine extends Engine {
    /**
     * Indicates whether the double engine is charged.
     */
    private boolean isCharged;

    /**
     * Constructs a new {@code DoubleEngine} with the specified connectors and ID.
     *
     * @param connectors an array of {@link ConnectorType} used to define the engine's connections
     * @param id         the unique identifier of the double engine
     */
    public DoubleEngine(ConnectorType[] connectors, int id) {
        super(connectors, id);
        this.isCharged = false;
    }

    /**
     * Checks whether the engine is charged or not.
     *
     * @return {@code true} if the engine is charged, {@code false} otherwise
     */
    public boolean isCharged() {
        return isCharged;
    }

    /**
     * Gets the power of the engine. The power is calculated by doubling
     * the base power value of the engine.
     *
     * @return the doubled power value of the engine
     */
    public int getPower() {
        return power * 2;
    }

    /**
     * Sets whether the engine is charged or not.
     *
     * @param charged {@code true} to set the engine as charged, or {@code false} otherwise
     */
    public void setCharged(boolean charged) {
        isCharged = charged;
    }
}