package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.CosmicCredit;
import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.componentTiles.*;

import java.util.ArrayList;
import java.util.List;

public class SpaceshipPlance {
    private ComponentTile[][] components;
    private ArrayList<ComponentTile> reserveSpot;
    private ArrayList<CargoHolds> cargoHolds;
    private ArrayList<Engine> engines;
    private ArrayList<Cannon> cannons;
    private ArrayList<Cabin> cabins;
    private boolean[][] visited;
    private ArrayList<ShieldGenerator> shieldGenerators;

    public SpaceshipPlance(ComponentTile[][] components, ArrayList<ComponentTile> reserveSpot) {
        this.components = components;
        this.reserveSpot = reserveSpot;
        this.visited = new boolean[4][6];
    }

    public void updateLists(){
        for(int i=0; i< 4; i++){
            for(int j = 0; j< 6; j++){
                ComponentTile tile = components[i][j];
                switch (tile) {
                    case Cannon c -> {
                        cannons.add(c);
                    }

                    case Engine e -> {
                        engines.add(e);

                    }case Cabin cab -> {
                        cabins.add(cab);


                    }case CargoHolds ch -> {
                        cargoHolds.add(ch);
                    }

                    case ShieldGenerator sg -> {
                        shieldGenerators.add(sg);
                    }

                    default ->{

                    }
                }
            }
        }
    }

    public boolean checkCorrectness() {
        dfs(2, 3); // per ora parto dal centro
        return true;
    }

    // posso anche fare una precomputazione mentre monto i pezzi
    private void dfs(int x, int y){
        // per ciascuna casella controllo prima che sia connessa bene da tutte le parti
        // poi chiamo ricorsivamente su tutte le altre direzioni
        // se devo fare il check con una cesella già visitata, la ignoro perchè ha gia fatto lei
        // controllo cannoni, se ha caselle davanti gli metto setWell connected a false e poi pure visited a true se il component != null
        // controllo engine

        // visita tutti le tile in profondità e dice se sono connesse bene
        if (x < 0 || x >= 4 || y < 0 || y >= 6 ||
                components[x][y] == null || visited[x][y]) {
            return;
        }

        ComponentTile tile = components[x][y];
        visited[x][y] = true;

        //sopra destra sotto sinistra
        int[] dirx ={0, 1, 0, -1};
        int[] diry ={1, 0, -1, 0};

        // caso deafualt, nel costruttore abbiamo WellConnected a true di deafult
        if(tile.isWellConnected()){
            ConnectorType[] connectors = tile.getConnectors();

            for(int i = 0; i < 4; i++){
                int x2 = x + dirx[i];
                int y2 = y + diry[i];

                ComponentTile tile2 = components[x2][y2];

                //controllo se il connettore è un engine puntato verso una direzione diversa da sud
                if(i!=2 && connectors[i] == ConnectorType.ENGINE)
                    tile.setWellConnected(false);

                if(tile2 !=null){
                    ConnectorType[] connectors2 = tile2.getConnectors();
                        // per ogni connettore confronto quello della cella adiacente opposto
                        if( tile.isWellConnected() && (!checkConnection(connectors[i], connectors2[(i+2)%4]))){
                            tile.setWellConnected(false);
                    }
                        dfs(x2, y2);
                }
            }
            // se entri nel ramo else vuol dire che sei nel range di un cannone o di un motore che ti ha settato a false senza visitarti oppure sei collegato
        }else{
            for(int i = 0; i < 4; i++){
                int x2 = x + dirx[i];
                int y2 = y + diry[i];

                // sappiamo che questa tile non è ben connessa, ma continuamo a visitare perchè dobbiamo visitare tutte le tiles
                ComponentTile tile2 = components[x2][y2];
                if(tile2 !=null){
                    dfs(x2, y2);
                }
            }
        }


    }

    private boolean checkConnection(ConnectorType connector, ConnectorType connector2) {


        if(connector != ConnectorType.CANNON && connector2 != ConnectorType.CANNON)
            return true;

        if(connector != ConnectorType.ENGINE && connector2 != ConnectorType.ENGINE)
            return true;

        return (connector == ConnectorType.UNIVERSAL || connector2 == ConnectorType.UNIVERSAL
                || (connector == connector2 && (connector == ConnectorType.SINGLE || connector == ConnectorType.DOUBLE)));


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
        // cammini partendo dalla casella indicata verso il centro
        // appena trovi un componente lo rimuovi
        // aggiungere prima i check per la posizione
        int max_lenght = 7;
        // casella da cui partire
        int x=0, y=0;
        switch (direction) {
            case NORTH:
                x = position - 4;
                y = 0;
                max_lenght = 5;
                break;
            case EAST:
                x = 6;
                y = position - 5;
                break;
            case SOUTH:
                x = position - 4;
                y = 4;
                max_lenght = 5;
                break;
            case WEST:
                x = 0;
                y = position - 5;
                break;
        }

        ComponentTile hit = components[y][x];

        for(int i = 0; i<max_lenght || hit == null; i++){
            switch (direction) {
                case NORTH:
                    y += 1;
                    break;
                case EAST:
                    x -= 1;
                    break;
                case SOUTH:
                    y -= 1;
                    break;
                case WEST:
                    x += 1;
                    break;
            }
            hit = components[y][x];
        }

        if (hit != null) {
            reserveSpot.add(hit);
            components[y][x] = null;
        }

        // check correctness diverso da quello iniziale
        // qua al posto che segnalare,tolgo tutto quello che non va bene

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

    public ArrayList<Cabin> getConnectedCabins() {
        // restituisce una lista di tutte le cabine che sono interconnesse ad almeno un'altra cabina
        return cabins;
    }
}
