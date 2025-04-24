package it.polimi.ingsw.controller.network.data;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class PickableTiles extends DataContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    ArrayList<String> tiles;

    public PickableTiles(ArrayList<String> tiles){
        this.tiles = tiles;
    }
    public ArrayList<String> getTiles(){
        return tiles;
    }
}
