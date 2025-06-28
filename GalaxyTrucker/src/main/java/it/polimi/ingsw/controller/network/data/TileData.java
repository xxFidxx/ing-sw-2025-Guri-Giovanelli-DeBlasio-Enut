package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;

public class TileData implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int rotation;
    private boolean wellConnected;
    private int part;

    public TileData(int id, int rotation) {
        this.id = id;
        this.rotation = rotation;
        this.wellConnected = true;
    }

    public TileData(int id, int rotation, boolean wellConnected, int part) {
        this.id = id;
        this.rotation = rotation;
        this.wellConnected = wellConnected;
        this.part = part;

    }

    public int getId() {
        return id;
    }

    public int getRotation() {
        return rotation;
    }

    public boolean isWellConnected() {
        return wellConnected;
    }

    public int getPart() {
        return part;
    }
}
