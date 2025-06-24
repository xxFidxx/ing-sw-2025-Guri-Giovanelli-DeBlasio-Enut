package it.polimi.ingsw.controller.network.data;


import java.io.Serializable;

public class DataString extends DataContainer implements Serializable {
    String text;
    TileData[][] tileIds;

    public DataString(String text) {
        super();
        this.text = text;
        this.tileIds = null;
    }


    public DataString(String text, TileData[][] tileIds) {
        super();
        this.text = text;
        this.tileIds = tileIds;
    }

    public String getText(){
        return text;
    }

    public TileData[][] getTileIds() {
        return tileIds;
    }

}
