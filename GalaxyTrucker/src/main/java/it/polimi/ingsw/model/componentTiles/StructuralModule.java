package it.polimi.ingsw.model.componentTiles;

/**
 * Represents a Structural Module in the game. This is a specific type of {@link ComponentTile}
 * used to build the structure of the game environment, inheriting its functionality from the parent class.
 */
public class StructuralModule extends ComponentTile {

    /**
     * Constructs a new Structural Module with the specified connectors and ID.
     *
     * @param connectors An array of {@link ConnectorType} specifying the connectors for this module.
     * @param id         The unique identifier for this module.
     */
    public StructuralModule(ConnectorType[] connectors, int id) {
        super(connectors, id);
    }
}