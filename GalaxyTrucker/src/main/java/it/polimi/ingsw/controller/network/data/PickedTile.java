package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;

public class PickedTile extends DataContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tileName;

    public PickedTile(String tileName) {
        super();
        this.tileName = tileName;
    }

    public String getTileName() {
        return tileName;
    }
}
