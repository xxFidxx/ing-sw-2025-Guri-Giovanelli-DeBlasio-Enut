package it.polimi.ingsw.componentTiles;

public abstract class ComponentTile {
    private final ConnectorType[] connectors;
    protected Direction direction;

    public ComponentTile(ConnectorType[] connectors,Direction direction){
        this.connectors = connectors;
        this.direction = direction;
    }
    public ConnectorType[] getConnectors() {
        return connectors;
    }

    public Direction getDirection() {
        return direction;
    }



}
