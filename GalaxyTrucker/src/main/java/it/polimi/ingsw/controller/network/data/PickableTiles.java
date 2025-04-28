package it.polimi.ingsw.controller.network.data;
import java.io.Serial;
import java.io.Serializable;

public class PickableTiles extends DataContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    Integer[] tiles;

    public PickableTiles(Integer[] tiles){
        this.tiles = tiles;
    }
    public Integer[] getTilesId(){
        return tiles;
    }
}
