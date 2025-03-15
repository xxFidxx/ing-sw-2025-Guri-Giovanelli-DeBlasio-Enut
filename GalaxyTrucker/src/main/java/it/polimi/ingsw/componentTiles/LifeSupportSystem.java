package it.polimi.ingsw.componentTiles;

public class LifeSupportSystem extends ComponentTile{
    private AlienColor color;
    private ConnectorType[] connectors;
    private Direction direction;

    LifeSupportSystem(AlienColor color, ConnectorType[] connectors, Direction direction) {
        super(connectors,direction);
        this.color = color;
    }


}
