package it.polimi.ingsw.componentTiles;

import java.awt.*;

public abstract class ComponentTile {
    private final ConnectorType[] connectors;
    protected Direction direction;
    private ComponentTile[] connectedComponents;

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
