package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.componentTiles.ConnectorType;

import java.io.Serializable;

public class PickedTile extends DataContainer implements Serializable {
    ConnectorType[] connectors;
    String description;
    int rotation;
    int id;

    public PickedTile(String description, int rotation, int id) {
        super();
        this.description = description;
        this.rotation = rotation;
        this.id = id;
    }

    public ConnectorType[] getConnectors(){
        return connectors;
    }

    public String getDescription() {
        return description;
    }

    public int getRotation() {
        return rotation;
    }

    public int getId() {
        return id;
    }
}
