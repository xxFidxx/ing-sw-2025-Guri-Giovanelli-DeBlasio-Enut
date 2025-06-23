package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.componentTiles.ConnectorType;

import java.io.Serializable;

public class PickedTile extends DataContainer implements Serializable {
    ConnectorType[] connectors;
    String description;

    public PickedTile(String description) {
        super();
        this.description = description;
    }

    public ConnectorType[] getConnectors(){
        return connectors;
    }

    public String getDescription() {
        return description;
    }
}
