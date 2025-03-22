package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.CosmicCredit;
import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.componentTiles.Cannon;
import it.polimi.ingsw.componentTiles.CargoHolds;
import it.polimi.ingsw.componentTiles.ComponentTile;
import it.polimi.ingsw.componentTiles.Engine;

import java.util.ArrayList;
import java.util.List;

public class SpaceshipPlance {
    private ComponentTile[][] components;
    private ComponentTile[] reserveSpot;
    private ArrayList<CargoHolds> cargoHolds;
    private ArrayList<Engine> engines;
    private ArrayList<Cannon> cannons;

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

    public ArrayList<CargoHolds> getCargoHolds(){
        return cargoHolds;
    }

    public ComponentTile[][] getComponents(){
        return components;
    }

    public ArrayList<Engine> getEngines(){
        return engines;
    }

    public ArrayList<Cannon> getCannons(){
        return cannons;
    }
}
