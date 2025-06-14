package it.polimi.ingsw.controller.network.data;
import it.polimi.ingsw.model.componentTiles.ComponentTile;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class PickableTiles extends DataContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    Integer[] tiles;
    ArrayList<ComponentTile> reservedTiles;

    public PickableTiles(Integer[] tiles, ArrayList<ComponentTile> reservedTiles) {
        this.tiles = tiles;
        this.reservedTiles = reservedTiles;
    }

    public Integer[] getTilesId(){
        return tiles;
    }
    public ArrayList<ComponentTile> getReservedTiles(){return reservedTiles;}
}
