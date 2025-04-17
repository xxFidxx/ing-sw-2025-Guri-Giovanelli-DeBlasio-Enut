package it.polimi.ingsw.game;

import it.polimi.ingsw.bank.GoodsBlock;
import it.polimi.ingsw.componentTiles.*;

import java.util.ArrayList;

import static it.polimi.ingsw.componentTiles.AlienColor.*;

public class SpaceshipPlance {
    private ComponentTile[][] components;
    private ArrayList<ComponentTile> reserveSpot;
    private ArrayList<CargoHolds> cargoHolds;
    private ArrayList<Engine> engines;
    private ArrayList<Cannon> cannons;
    private ArrayList<Cabin> cabins;
    private boolean[][] visited;
    private int[][] shownComponents;
    private ArrayList<ShieldGenerator> shieldGenerators;
    private int nAstronauts;
    private int nBrownAliens;
    private int nPurpleAliens;
    private int exposedConnectors;




    public SpaceshipPlance(ComponentTile[][] components, ArrayList<ComponentTile> reserveSpot) {
        this.components = components;
        this.reserveSpot = reserveSpot;
        this.visited = new boolean[4][6];
        this.shownComponents = new int[4][6];
        this.exposedConnectors = 0;
        this.shieldGenerators = new ArrayList<>();
        this.cannons = new ArrayList<>();
        this.cabins = new ArrayList<>();
        this.engines = new ArrayList<>();
        this.nAstronauts = 0;
        this.nBrownAliens = 0;
        this.nPurpleAliens = 0;
    }

    private void initVisited(){
        // Imposta visited a false
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                visited[i][j] = false;
            }
        }
    }

    private void initShownComponents(){
        // Imposta shownComponents a -1
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                shownComponents[i][j] = -1;
            }
        }
    }

    private void updateLists() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                ComponentTile tile = components[i][j];

                if (tile != null) {
                    switch (tile) {
                        case Cannon c -> {
                            cannons.add(c);
                        }

                        case Engine e -> {
                            engines.add(e);

                        }
                        case Cabin cab -> {
                            cabins.add(cab);

                        }
                        case CargoHolds ch -> {
                            cargoHolds.add(ch);
                        }

                        case ShieldGenerator sg -> {
                            shieldGenerators.add(sg);
                        }

                        default -> {
                        }
                    }
                }
            }
        }
    }

    private void removeIslands(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                if(!visited[i][j]){
                    components[i][j] = null;
                }
            }
        }
    }

    public void selectPart(int iteration){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                if(shownComponents[i][j] != iteration){
                    components[i][j] = null;
                }
            }
        }
    }

    public boolean checkCorrectness() {
        initVisited();
        dfsCorrectness(2, 3); // per ora parto dal centro
        return true;
    }

    private void dfsCorrectness(int x, int y){

        // visita tutti le tile in profondità e dice se sono connesse bene
        if (x < 0 || x >= 6 || y < 0 || y >= 4 ||
                components[y][x] == null || visited[y][x]) {
            return;
        }

        ComponentTile tile = components[y][x];
        visited[y][x] = true;

        //sopra destra sotto sinistra
        int[] dirx ={0, 1, 0, -1};
        int[] diry ={1, 0, -1, 0};

        // caso deafualt, nel costruttore abbiamo WellConnected a true di deafult
        ConnectorType[] connectors = tile.getConnectors();

        for(int i = 0; i < 4; i++){
            int x2 = x + dirx[i];
            int y2 = y + diry[i];

            if(connectors[i] != ConnectorType.SMOOTH){
                ComponentTile tile2 = components[y2][x2];

                //controllo se il connettore è un engine puntato verso una direzione diversa da sud
                if(i!=2 && connectors[i] == ConnectorType.ENGINE)
                    tile.setWellConnected(false);

                if(tile2 !=null){
                    ConnectorType[] connectors2 = tile2.getConnectors();

                    if(i == 2 && connectors[i] == ConnectorType.ENGINE){
                        tile.setWellConnected(false);
                        tile2.setWellConnected(false);
                    }


                    if(connectors[i] == ConnectorType.CANNON)
                        tile2.setWellConnected(false);
                    // per ogni connettore confronto quello della cella adiacente opposto, se è gia a false perchè è engine non entro
                    if(tile.isWellConnected() && (!checkConnection(connectors[i], connectors2[(i+2)%4]))){
                        tile.setWellConnected(false);
                    }
                    dfsCorrectness(x2, y2);
                }
            }
        }
    }


    public void remove(int x,int y){
        initShownComponents();
        initVisited();
        components[y][x] = null;

        int[] dirx ={0, 1, 0, -1};
        int[] diry ={1, 0, -1, 0};

        // fai partire il dfs dalle 4 caselle adiacenti al pezzo rimosso
        for(int i=0; i< 4; i++){
            dfsRemove(x + dirx[i] ,y + diry[i],i);
        }
    }


    private void dfsRemove(int x, int y, int iteration){

        if (x < 0 || x >= 6 || y < 0 || y >= 4 ||
                components[y][x] == null || visited[y][x]) {
            return;
        }

        ComponentTile tile = components[y][x];
        // per ogni casella mi segno se è stata visitata e  quale iterazione (quindi troncone) appartiene
        visited[y][x] = true;
        shownComponents[y][x] = iteration;

        //sopra destra sotto sinistra
        int[] dirx ={0, 1, 0, -1};
        int[] diry ={1, 0, -1, 0};

        // caso deafualt, nel costruttore abbiamo WellConnected a true di deafult
        ConnectorType[] connectors = tile.getConnectors();

        for(int i = 0; i < 4; i++){
            int x2 = x + dirx[i];
            int y2 = y + diry[i];

            if(connectors[i] != ConnectorType.SMOOTH){
                ComponentTile tile2 = components[y2][x2];


                if(tile2 !=null){
                    ConnectorType[] connectors2 = tile2.getConnectors();
                    if((checkConnection(connectors[i], connectors2[(i+2)%4]))){
                        dfsRemove(x2, y2,iteration);
                    }

                }
            }
        }
    }


    private boolean checkConnection(ConnectorType connector, ConnectorType connector2) {


        if(connector == ConnectorType.CANNON || connector2 == ConnectorType.CANNON)
            return false;

        if(connector == ConnectorType.ENGINE || connector2 == ConnectorType.ENGINE)
            return false;

        // se c'è un universale ora è sicuramente true perchè abbiamo già controllato cannoni ed engine
        if(connector == ConnectorType.UNIVERSAL && connector2 != ConnectorType.SMOOTH)
            return true;

        if(connector == ConnectorType.SINGLE && connector2 == ConnectorType.SINGLE)
            return true;

        if(connector == ConnectorType.DOUBLE && connector2 == ConnectorType.DOUBLE)
            return true;


        return false;
    }


    public void countFigures(){
        for (Cabin cabin : cabins) {
            Figure[] figures = cabin.getFigures();
            for (Figure figure : figures) {
                if (figure instanceof Astronaut)
                    nAstronauts++;
                else {
                    Alien alien = (Alien) figure;
                    if (alien.getColor() == BROWN)
                        nBrownAliens++;
                    else if (alien.getColor() == PURPLE)
                        nPurpleAliens++;
                }
            }
        }
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
        int total = 0;
        for(CargoHolds c : cargoHolds){
            for(GoodsBlock block : c.getGoods()){
                if(block!=null)
                    total++;
            }
        }
        return total;
    }

    public int countExposedConnectors(){

        exposedConnectors = 0;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 6; x++) {
                ComponentTile tile = components[y][x];

                if (tile != null) {

                    int[] dirx ={0, 1, 0, -1};
                    int[] diry ={1, 0, -1, 0};

                    ConnectorType[] connectors = tile.getConnectors();
                    for(int i = 0; i < connectors.length; i++){
                        if(connectors[i] != ConnectorType.SMOOTH && connectors[i] != ConnectorType.CANNON && connectors[i] != ConnectorType.ENGINE){
                            int x2 = x + dirx[i];
                            int y2 = y + diry[i];
                            ComponentTile tile2 = components[y2][x2];
                            if(tile2 == null)
                                exposedConnectors++;
                        }
                    }
                }
            }
        }

        return exposedConnectors;
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

        if(hit != null){
            reserveSpot.add(hit);
            remove(x,y);
        }
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
        ArrayList<Cabin> connectedCabins = new ArrayList<>();
        for (int y = 0; y < 4; y++){
            for (int x = 0; x < 6; x++) {
                ComponentTile tile = components[y][x];
                if (tile instanceof Cabin) {

                    int[] dirx = {0, 1, 0, -1};
                    int[] diry = {1, 0, -1, 0};

                    ConnectorType[] connectors = tile.getConnectors();
                    for(int i = 0; i < connectors.length; i++){
                        // se da quel lato c'è un connettore singolo/doppio/universale vuol dire che è collegato a un'altra casella. Controllo che sia anch'essa una nave
                        if(connectors[i] != ConnectorType.SMOOTH && connectors[i] != ConnectorType.CANNON && connectors[i] != ConnectorType.ENGINE){
                            int x2 = x + dirx[i];
                            int y2 = y + diry[i];
                            ComponentTile tile2 = components[y2][x2];
                            if (tile2 instanceof Cabin)
                                connectedCabins.add((Cabin)tile2);
                        }
                    }
                }
            }
        }
        return connectedCabins;
    }

    public int getnAstronauts() {
        return nAstronauts;
    }

    public int getnBrownAliens() {
        return nBrownAliens;
    }


    public int getnPurpleAliens() {
        return nPurpleAliens;
    }

    public boolean askPlaceTile(){
        return true;
    }

    public void placeTileReserveSpot(ComponentTile tile){
        if(reserveSpot.size() < 2)
            reserveSpot.add(tile);
        else
            System.out.println("Full reserve spot");
    }

    public void placeTileComponents(ComponentTile tile, int x, int y) {
        if(x < 0 || x >= 6 || y < 0 || y >= 4)
            System.out.println("Out of bounds");
        else if(components[y][x] != null){
            System.out.println("Spot already taken");
        }
        components[y][x] = tile;
    }

    public void placeReserveToComponents(ComponentTile tile, int x, int y){
        placeTileComponents(tile, x, y);
        reserveSpot.remove(tile);
    }
}
