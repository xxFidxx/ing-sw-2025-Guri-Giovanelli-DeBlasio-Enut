package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.ConnectorType;

import java.io.Serializable;

public class PickedTile extends DataContainer implements Serializable {
    String name;
    ConnectorType[] connectors;

    public PickedTile(String name, ConnectorType [] connectors) {
        super();
        this.name = name;
        this.connectors = connectors;
    }

    public String getName(){
        return name;
    }

    public ConnectorType[] getConnectors(){
        return connectors;
    }

}
