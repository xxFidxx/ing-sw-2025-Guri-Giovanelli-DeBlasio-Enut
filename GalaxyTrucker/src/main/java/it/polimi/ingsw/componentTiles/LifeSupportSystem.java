package it.polimi.ingsw.componentTiles;

public class LifeSupportSystem extends ComponentTile{
    private AlienColor color;

    public LifeSupportSystem(AlienColor color, ConnectorType[] connectors, Direction direction) {
        super(connectors,direction);
        this.color = color;
    }
}
