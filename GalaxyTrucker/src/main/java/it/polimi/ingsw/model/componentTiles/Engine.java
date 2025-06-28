package it.polimi.ingsw.model.componentTiles;

/**
 * Represents an engine component in a system,
 * which extends the {@link ComponentTile} class, and includes additional
 * functionality related to power.
 */
public class Engine extends ComponentTile {

    /**
     * Represents the power level of the engine.
     * The default value is initialized to 1.
     */
    protected int power;

    /**
     * Constructs a new {@code Engine} instance with the specified connectors and ID.
     *
     * @param connectors an array of {@code ConnectorType} that defines the connectors of this engine
     * @param id the unique identifier for this instance
     */
    public Engine(ConnectorType[] connectors, int id) {
        super(connectors, id);
        this.power = 1;
    }

    /**
     * Returns the power level of the engine.
     *
     * @return the current power level
     */
    public int getPower() {
        return power;
    }
}