package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.CosmicCredit;
import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.componentTiles.*;

import java.util.ArrayList;
import java.util.List;

public class SpaceshipPlance {
    private ComponentTile[][] components;
    private ComponentTile[] reserveSpot;
    private ArrayList<CargoHolds> cargoHolds;
    private ArrayList<Engine> engines;
    private ArrayList<Cannon> cannons;
    private ArrayList<Cabin> cabins;
    private boolean[][] visited;
    private ArrayList<ShieldGenerator> shieldGenerators;

    public SpaceshipPlance(ComponentTile[][] components, ComponentTile[] reserveSpot,boolean[][] visited) {
    this.components = components;
    this.reserveSpot = reserveSpot;
    this.visited = new boolean[6][4];
    }

    public boolean checkCorrectness() {

        dfs(2, 3); // per ora parto dal centro
        return true;
    }

    private void dfs(int x, int y){
        visited[x][y] = true;
        ComponentTile tile = components[x][y];
        if(tile != null) {
            ConnectorType[] connectors = tile.getConnectors();
            tile.setWellConnected(true);

            //sopra destra sotto sinistra
            int[] dirx ={0, 1, 0, -1};
            int[] diry ={1, 0, -1, 0};

            for(int i = 0; i < 4; i++){
                int x2 = x + dirx[i];
                int y2 = y + diry[i];

                ComponentTile tile2 = components[x2][y2];
                if(tile2 !=null){
                    ConnectorType[] connectors2 = tile2.getConnectors();
                    for(int j = 0; j < connectors.length && tile.isWellConnected(); j++){
                        if(connectors2[j] != connectors[j]){
                            tile.setWellConnected(false);
                        }
                    }
                }
            }
        }

        return;

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

    public ArrayList<Cabin> getCabins() {
        return cabins;
    }

    public int checkStorage(){
        return 0;
    }

    public int countExposedConnectors(){
        return 0;
    }


    public boolean checkExposedConnector(int position) {
        return false;
    }

    public boolean getShieldActivation(Direction direction) {
        for (ShieldGenerator shieldGenerator : shieldGenerators) {
            if (shieldGenerator.checkProtection(direction) == true) {
                boolean active = askActivateShield(shieldGenerator);
                if (active) return true;
            }
        }
        return false;
    }

    private boolean askActivateShield(ShieldGenerator shieldGenerator) {
        return true;
    }

    public void takeHit(Direction direction, int position) {
        //rimuovi componente
    }

    public boolean getCannonActivation(Direction direction, int position) {
        for (Cannon cannon : cannons) {
            if (cannon.checkProtection(direction, position) == true) {
                boolean active = askActivateCannon(cannon);
                if (active) return true;
            }
        }
        return false;
    }

    private boolean askActivateCannon(Cannon cannon) {
        return true;
    }
}
