package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;

public class TileData implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int rotation;

    public TileData(int id, int rotation) {
        this.id = id;
        this.rotation = rotation;
    }

    public int getId() {
        return id;
    }

    public int getRotation() {
        return rotation;
    }
}
