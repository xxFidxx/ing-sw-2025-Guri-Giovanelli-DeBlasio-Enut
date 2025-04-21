package it.polimi.ingsw.model.componentTiles;

public class LifeSupportSystem extends ComponentTile{
    private AlienColor color;


    public LifeSupportSystem(AlienColor color, ConnectorType[] connectors, Direction direction) {
        super(connectors);
        this.color = color;
    }
}
