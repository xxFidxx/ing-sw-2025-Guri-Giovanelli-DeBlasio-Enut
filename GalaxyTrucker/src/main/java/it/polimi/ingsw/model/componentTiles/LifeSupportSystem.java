package it.polimi.ingsw.model.componentTiles;

public class LifeSupportSystem extends ComponentTile{
    private final AlienColor color;


    public LifeSupportSystem(AlienColor color, ConnectorType[] connectors, String id) {
        super(connectors,id);
        this.color = color;
    }
}
