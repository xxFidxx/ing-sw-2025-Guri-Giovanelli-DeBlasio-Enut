package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;

public class PickedTile extends DataContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tileid;

    public PickedTile(String tileid) {
        super();
        this.tileid = tileid;
    }

    public String getTileid() {
        return tileid;
    }
}
