package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.ComponentTile;

import java.util.ArrayList;

public class PickableTiles extends DataContainer{
    ArrayList<String> tiles;

    public PickableTiles(ArrayList<String> tiles){
        this.tiles = tiles;
    }


    public void visit(PickableTiles pickableTiles){
        ArrayList<String> tiles = pickableTiles.getTiles();
        System.out.println("\n List of tiles:\n");
        for (int i= 0; i<tiles.size(); i++) {
            System.out.println(i + ": [" + tiles.get(i) + "]\n");
        }
    }

    public ArrayList<String> getTiles(){
        return tiles;
    }
}
