package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.CosmicCredit;
import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.componentTiles.ComponentTile;

import java.util.List;

public class SpaceshipPlance {
    private ComponentTile[][] components;
    private ComponentTile[] reserveSpot;
    private

    public SpaceshipPlance(ComponentTile[][] components, ComponentTile[] reserveSpot) {
    this.components = components;
    this.reserveSpot = reserveSpot;
    }

    public boolean checkCorrectness(){
        return true;
    }

    public int countAstronauts(){
        return 0;
    }

    public int countAliens(){
        return 0;
    }

    public void loadGoodsBlocks(GoodsBlock[] goodsBlocks){

    }

}
