package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.ControllerExceptions;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.resources.GoodsContainer;
import it.polimi.ingsw.model.resources.TileSymbols;

import java.util.*;

import static it.polimi.ingsw.model.componentTiles.AlienColor.*;
import static it.polimi.ingsw.model.game.ColorType.*;

public class SpaceshipPlance {
    private final ComponentTile[][] components;
    private final ArrayList<ComponentTile> reserveSpot;
    private final ArrayList<CargoHolds> cargoHolds;
    private final ArrayList<Engine> engines;
    private ArrayList<Cannon> cannons;
    private final ArrayList<Cabin> cabins;
    private final ArrayList<PowerCenter> powerCenters;
    private final boolean[][] visited;
    private final int[][] shownComponents;
    private final ArrayList<ShieldGenerator> shieldGenerators;
    private int nAstronauts;
    private int nBrownAliens;
    private int nPurpleAliens;
    private int nBatteries;
    private int exposedConnectors;
    private ArrayList<GoodsContainer> goodsContainers;
    private static final int ROWS = 5;
    private static final int COLS = 7;
    private static final int[] DIR_X = {0, 1, 0, -1};
    private static final int[] DIR_Y = {-1, 0, 1, 0};


    public SpaceshipPlance() {
        this.components = new ComponentTile[5][7];
        this.reserveSpot = new ArrayList<>();
        this.visited = new boolean[5][7];
        this.shownComponents = new int[5][7];
        this.exposedConnectors = 0;
        this.shieldGenerators = new ArrayList<>();
        this.cannons = new ArrayList<>();
        this.cabins = new ArrayList<>();
        this.cargoHolds = new ArrayList<>();
        this.powerCenters = new ArrayList<>();
        // ci creiamo un arraylist di engines per simulare open space card
        this.engines = new ArrayList<>(List.of(
                new DoubleEngine(new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE}, 1),
                new DoubleEngine(new ConnectorType[]{ConnectorType.DOUBLE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE}, 2),
                new Engine(new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.DOUBLE}, 3),
                new Engine(new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL}, 4),
                new Engine(new ConnectorType[]{ConnectorType.DOUBLE, ConnectorType.DOUBLE, ConnectorType.SINGLE, ConnectorType.UNIVERSAL}, 5)
        ));
        this.cannons = new ArrayList<>(List.of(
                new DoubleCannon(new ConnectorType[]{ConnectorType.CANNON, ConnectorType.DOUBLE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE}, 6),
                new DoubleCannon(new ConnectorType[]{ConnectorType.DOUBLE, ConnectorType.CANNON, ConnectorType.SINGLE, ConnectorType.DOUBLE}, 7),
                new DoubleCannon(new ConnectorType[]{ConnectorType.DOUBLE, ConnectorType.CANNON, ConnectorType.SINGLE, ConnectorType.DOUBLE}, 8),
                new Cannon(new ConnectorType[]{ConnectorType.CANNON, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.DOUBLE}, 9),
                new Cannon(new ConnectorType[]{ConnectorType.CANNON, ConnectorType.SINGLE, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL}, 10),
                new Cannon(new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.CANNON, ConnectorType.SINGLE, ConnectorType.UNIVERSAL}, 11),
                new Cannon(new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.CANNON, ConnectorType.SINGLE, ConnectorType.UNIVERSAL}, 12)
        ));
        this.nAstronauts = 0;
        this.nBrownAliens = 0;
        this.nPurpleAliens = 0;
        this.goodsContainers = new ArrayList<>();

        ConnectorType[] cannonConnectors = {
                ConnectorType.UNIVERSAL,   // Lato superiore
                ConnectorType.UNIVERSAL,   // Lato destro
                ConnectorType.UNIVERSAL,   // Lato inferiore
                ConnectorType.UNIVERSAL    // Lato sinistro
        };
        components[2][3] = new Cabin(cannonConnectors, true, 100);
        ComponentTile tile = components[2][3];
        tile.setWellConnected(true);
    }

    public void setGoodsContainers(ArrayList<GoodsContainer> goodsContainers) {
        this.goodsContainers = goodsContainers;
    }

    public int getBrownAliens() {
        return this.nBrownAliens;
    }

    public int getPurpleAliens() {
        return this.nPurpleAliens;
    }

    public int getnBatteries(){
        return nBatteries;
    }

    private boolean edgeCases(int y, int x) {
        if (y == 0) {
            return x == 0 || x == 1 || x == 3 || x == 5 || x == 6;
        } else if (y == 1) {
            return x == 0 || x == 6;
        } else if (y == 4) {
            return x == 3;
        }
        return false;
    }

    private void initVisited() {
        // Imposta visited a false
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                visited[i][j] = false;
            }
        }
    }

    private void initShownComponents() {
        // Imposta shownComponents a -1
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                shownComponents[i][j] = -1;
            }
        }
    }

    public void addReserveSpot(ComponentTile c) {
        this.reserveSpot.add(c);
    }

    public void updateLists() {
        cannons.clear();
        engines.clear();
        cabins.clear();
        cargoHolds.clear();
        shieldGenerators.clear();
        powerCenters.clear();
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                ComponentTile tile = components[y][x];

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
                            // I reset aliens of previuos check, maybe the lifesupport system has been destroyed so I need to do that to ensure the alien doesnt remain on the cabin
                            AlienColor[] colors = cab.getLifeSupportSystemColor();
                            colors[0] = null;
                            colors[1] = null;
                                for (int dir = 0; dir < 4; dir++) {
                                    int[] dirx = DIR_X;
                                    int[] diry = DIR_Y;
                                    int nx = x + dirx[dir];
                                    int ny = y + diry[dir];
                                    if (inBounds(nx, ny) && components[ny][nx] != null) {
                                        ComponentTile tile2 = components[ny][nx];
                                        if (tile2 instanceof LifeSupportSystem) {
                                            ConnectorType a = tile.getConnectors()[dir];
                                            ConnectorType b = components[ny][nx].getConnectors()[(dir + 2) % 4];

                                            if (isConnectionValid(a, b)) {
                                                // se cambio l'ordine dei colori nell'enum, cambierà anche questo. Basta usare sempre l'ordinal e sarà tutto coerente.
                                                // la cabina ha un array di AlienColors a 2 posti, metto a true la casella corrispondente al colore dell'alieno, faccio
                                                // cosi perchè nel caso ci siano 2 colori, sono sicuro che non vado a sovrascrivere la casella contenente già un colore
                                                colors[((LifeSupportSystem) tile2).getColor().ordinal()] = ((LifeSupportSystem) tile2).getColor();
                                            }
                                        }
                                    }
                                }
                                Figure[] figures = cab.getFigures();
                                // if a lifesupport has been removed, it means there is an alien on the cabin but there isnt anymore the corresponding color in the colors array
                                if(figures[0] != null && figures[0] instanceof Alien && colors[((Alien) figures[0]).getColor().ordinal()] == null)
                                    figures[0] = null;
                        }
                        case CargoHolds ch -> {
                            cargoHolds.add(ch);
                        }

                        case ShieldGenerator sg -> {
                            shieldGenerators.add(sg);
                        }

                        case PowerCenter pc -> {
                            powerCenters.add(pc);
                        }

                        default -> {
                        }
                    }
                }
            }
        }

        countFigures();
        countBatteries();
    }

    private boolean inBounds(int x, int y) {
        // Prima controlla i bound standard
        boolean standardBounds = (x >= 0 && x < COLS && y >= 0 && y < ROWS);

        // Poi verifica gli edge case specifici della forma
        return standardBounds && !edgeCases(y, x);
    }

    private void clearVisited() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                visited[y][x] = false;
            }
        }
    }

    private void removeUnvisited() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                if (!visited[y][x]) components[y][x] = null;
            }
        }
    }

    public boolean checkCorrectness() {
        initVisited();
        // prima guardo se la tile in 3 2 è null, se è null allora faccio un doppio for dove cerco la prima tile libera, perch vuol dire che sono nel caso in cui è stato rimosso
        // il centro, per cui non incappo in isole, perch nella fase iniziale si possono creare e li sicuramente il centro esiste
        ComponentTile tile = components[2][3];
        if (tile == null) {
            int xStart = 0;
            int yStart = 0;
            for (int y = 0; y < ROWS; y++) {
                for (int x = 0; x < COLS; x++) {
                    if (components[y][x] != null) {
                        xStart = x;
                        yStart = y;
                        break;
                    }
                }
            }
            dfsExploration(xStart, yStart);
        } else {
            // 1. Fase di esplorazione: trova tutte le tile connesse alla cabina centrale
            dfsExploration(3, 2); // Parte dalla posizione centrale
        }


        // 2. Rimozione immediata delle tile non connesse
        removeUnvisitedTiles();

        // 3. Fase di validazione: controlla le connessioni delle tile rimanenti
        return validateRemainingTiles();
    }

    private void dfsExploration(int x, int y) {
        if (!inBounds(x, y) || edgeCases(y, x) ||
                components[y][x] == null || visited[y][x]) {
            return;
        }

        visited[y][x] = true;

        int[] dirx = DIR_X;
        int[] diry = DIR_Y;
        ComponentTile tile = components[y][x];

        for (int dir = 0; dir < 4; dir++) {
            // Non seguire i connettori smooth
            if (tile.getConnectors()[dir] == ConnectorType.SMOOTH) continue;

            int nx = x + dirx[dir];
            int ny = y + diry[dir];
            dfsExploration(nx, ny);
        }
    }

    private void removeUnvisitedTiles() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                if (!visited[y][x]) {
                    if(components[y][x] != null) {
                        addReserveSpot(components[y][x]);
                        components[y][x] = null;
                    }
                }
            }
        }
    }

    private boolean validateRemainingTiles() {
        boolean allValid = true;

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                ComponentTile tile = components[y][x];
                if (tile != null) {
                    boolean valid = isTileValid(x, y);
                    tile.setWellConnected(valid);
                    if (!valid) allValid = false;
                }
            }
        }

        return allValid;
    }

    private boolean isTileValid(int x, int y) {
        ComponentTile tile = components[y][x];

        // Controllo componenti speciali
        if (tile instanceof Engine && !isEngineValid(y, x)) return false;
        if (tile instanceof Cannon && !isCannonValid(y, x)) return false;


        int[] dirx = DIR_X;
        int[] diry = DIR_Y;  // dy: NORD, EST, SUD, OVEST

        for (int dir = 0; dir < 4; dir++) {
            int nx = x + dirx[dir];
            int ny = y + diry[dir];

            if (!inBounds(nx, ny) || components[ny][nx] == null) continue;

            ConnectorType a = tile.getConnectors()[dir];
            ConnectorType b = components[ny][nx].getConnectors()[(dir + 2) % 4];

            if (!isConnectionValid(a, b)) return false;
        }

        return true;
    }

    private boolean isConnectionValid(ComponentTile tile, int direction, int nx, int ny) {
        ConnectorType a = tile.getConnectors()[direction];

        // Caso bordo o tile nulla
        if (!inBounds(nx, ny)) {
            return a == ConnectorType.SMOOTH ||
                    a == ConnectorType.ENGINE ||
                    a == ConnectorType.CANNON;
        }

        ComponentTile adjTile = components[ny][nx];
        if (adjTile == null) return false;

        ConnectorType b = adjTile.getConnectors()[(direction + 2) % 4];

        // Logica di connessione
        if (a == ConnectorType.SMOOTH) return b == ConnectorType.SMOOTH;
        if (b == ConnectorType.SMOOTH) return false;

        // Casi speciali
        if (a == ConnectorType.ENGINE || a == ConnectorType.CANNON) return false;
        if (b == ConnectorType.ENGINE || b == ConnectorType.CANNON) return false;

        // Match esatto o universale
        return a == b ||
                a == ConnectorType.UNIVERSAL ||
                b == ConnectorType.UNIVERSAL;
    }

    private boolean isTileWellConnected(int x, int y) {
        ComponentTile tile = components[y][x];
        if (tile == null) return true;

        // 1. Controllo orientamento componenti speciali
        if (tile instanceof Engine && !isEngineValid(y, x)) return false;
        if (tile instanceof Cannon && !isCannonValid(y, x)) return false;

        // 2. Controllo connessioni con tile adiacenti
        int[] dx = DIR_X;
        int[] dy = DIR_Y;

        for (int dir = 0; dir < 4; dir++) {
            int nx = x + dx[dir];
            int ny = y + dy[dir];

            if (!inBounds(nx, ny) || components[ny][nx] == null) {
                continue; // Ignora connettori esposti (non invalidano)
            }

            ConnectorType a = tile.getConnectors()[dir];
            ConnectorType b = components[ny][nx].getConnectors()[(dir + 2) % 4];

            if (!isConnectionValid(a, b)) {
                return false;
            }
        }
        return true;
    }

    private boolean isConnectionValid(ConnectorType a, ConnectorType b) {
        //System.out.println(a.toString() + " " + b.toString());
        boolean prova = true;
        // Regole base di connessione (ignora i casi esposti)
        if (a == ConnectorType.SMOOTH || b == ConnectorType.SMOOTH) {
            prova = (a == b); // Entrambi smooth
            //System.out.println(prova);
            return prova;
        }

        if (a == ConnectorType.ENGINE || b == ConnectorType.ENGINE) {
            //System.out.println(false);
            return false; // Motori non si connettono
        }

        if (a == ConnectorType.CANNON || b == ConnectorType.CANNON) {
            //System.out.println(false);
            return false; // Cannoni non si connettono
        }

        // Regole per connettori normali
        prova = (a == ConnectorType.UNIVERSAL ||
                b == ConnectorType.UNIVERSAL ||
                a == b);
        //System.out.println(prova);
        return prova;
    }

    private boolean isEngineValid(int y, int x) {
        // Controlla solo orientamento e spazio dietro
        return components[y][x].getConnectors()[2] == ConnectorType.ENGINE &&
                (y + 1 >= 4 || components[y + 1][x] == null);
    }

    private boolean isCannonValid(int y, int x) {
        // Controlla solo orientamento e spazio davanti
        return components[y][x].getConnectors()[0] == ConnectorType.CANNON &&
                (y - 1 < 0 || components[y - 1][x] == null);
    }


    public boolean checkNewTile(int x, int y) {
        if (edgeCases(y, x) || !inBounds(x, y) || components[y][x] == null) {
            return false;
        }

        ComponentTile tile = components[y][x];
        boolean isValid = true;

        // Controlla tutti e 4 i connettori
        for (int dir = 0; dir < 4; dir++) {
            int adjX = x + DIR_X[dir];
            int adjY = y + DIR_Y[dir];

            // Caso 1: Controllo speciale per ENGINE/CANNON
            if (tile instanceof Engine && dir == 2) { // SOUTH
                if (y + 1 < ROWS && components[y + 1][x] != null) {
                    isValid = false;
                }
                continue;
            }

            if (tile instanceof Cannon && dir == 0) { // NORTH
                if (y - 1 >= 0 && components[y - 1][x] != null) {
                    isValid = false;
                }
                continue;
            }

            // Caso 2: Connessione con tile adiacente
            if (inBounds(adjX, adjY) && !edgeCases(adjY, adjX)) {
                ComponentTile adjTile = components[adjY][adjX];

                if (adjTile != null) {
                    ConnectorType current = tile.getConnectors()[dir];
                    ConnectorType adjacent = adjTile.getConnectors()[(dir + 2) % 4];

                    if (!isConnectionValid(current, adjacent)) {
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }


    public int remove(int x, int y) {
        initShownComponents();
        initVisited();
        if (components[y][x] == null)
            throw new SpaceShipPlanceException("No tile in the index you provided, please retry");

        addReserveSpot(components[y][x]);
        components[y][x] = null;

        int[] dirx = DIR_X;
        int[] diry = DIR_Y;
        int partsFound = 0;

        // Parte da tutte e 4 le direzioni con iterazioni diverse
        for (int iteration = 0; iteration < 4; iteration++) {
            int nx = x + dirx[iteration];
            int ny = y + diry[iteration];

            if (inBounds(nx, ny) && !edgeCases(ny, nx) && components[ny][nx] != null) {
                partsFound += dfsRemove(nx, ny, iteration);
            }
        }

        return partsFound;
    }

    private int dfsRemove(int x, int y, int iteration) {
        if (!inBounds(x, y) || edgeCases(y, x) || visited[y][x] || components[y][x] == null) {
            return 0;
        }

        visited[y][x] = true;
        shownComponents[y][x] = iteration;
        int count = 1;

        int[] dirx = DIR_X;
        int[] diry = DIR_Y;
        ComponentTile tile = components[y][x];

        for (int dir = 0; dir < 4; dir++) {
            // Non seguire i connettori smooth
            if (tile.getConnectors()[dir] == ConnectorType.SMOOTH) continue;

            int nx = x + dirx[dir];
            int ny = y + diry[dir];
            dfsRemove(nx, ny, iteration);
        }

        return count;
    }

    public void selectPart(int iteration) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (shownComponents[i][j] != iteration) {
                    components[i][j] = null;
                }
            }
        }
    }


    public void countFigures() {
        nAstronauts = 0;
        nBrownAliens = 0;
        nPurpleAliens = 0;
        for (Cabin cabin : cabins) {
            Figure[] figures = cabin.getFigures();
            for (Figure figure : figures){
                if(figure != null){
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
        System.out.println("Astronauts: " + nAstronauts + "Brown aliens: " + nBrownAliens + "Purple aliens: " + nPurpleAliens);
    }

    public boolean checkStorage() {
        return !cargoHolds.isEmpty();
    }

    public void handleSwap(int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) throws CargoManagementException {

        // il +1 è dato dal fatto che noi in posizione 0 simuliamo avere i rewards
        if (cargoIndex1 >= 0 && cargoIndex1 < goodsContainers.size() && cargoIndex2 >= 0 && cargoIndex2 < goodsContainers.size()) {

            GoodsContainer cargo1 = goodsContainers.get(cargoIndex1);
            GoodsContainer cargo2 = goodsContainers.get(cargoIndex2);

            if (goodIndex1 >= 0 && goodIndex1 < cargo1.getGoods().length && goodIndex2 >= 0 && goodIndex2 < cargo2.getGoods().length) {
                GoodsBlock good1 = cargo1.getGoods()[goodIndex1];
                GoodsBlock good2 = cargo2.getGoods()[goodIndex2];


                if (checkSpecialGoods(cargo1, cargo2, good1, good2))
                    swapGoods(cargo1, cargo2, goodIndex1, goodIndex2);
                else
                    throw new CargoManagementException("Can't put a Red block in grey cargo");
            } else {
                throw new CargoManagementException("At least one goods index is outbound");
            }
        } else {
            throw new CargoManagementException("At least one cargo index is outbound");
        }
    }

    public void handleRemove(int cargoIndex, int goodIndex) throws CargoManagementException {

        if (cargoIndex >= 0 && cargoIndex < goodsContainers.size()) {
            GoodsContainer cargo1 = goodsContainers.get(cargoIndex);
            if (goodIndex >= 0 && goodIndex < cargo1.getGoods().length) {
                removeGoods(cargo1, goodIndex);
            } else
                throw new CargoManagementException("goods index is outbound");
        } else
            throw new CargoManagementException("cargo index is outbound");
    }

    public void handleAdd(GoodsBlock[] cardReward, int cargoIndex, int goodIndex, int rewardIndex) throws CargoManagementException {

        if (cargoIndex >= 0 && cargoIndex < goodsContainers.size()) {
            GoodsContainer cargo1 = goodsContainers.get(cargoIndex);
            if (goodIndex >= 0 && goodIndex < cargo1.getGoods().length && rewardIndex >= 0 && rewardIndex < cardReward.length) {
                GoodsBlock good1 = cargo1.getGoods()[goodIndex];
                GoodsBlock good2 = cardReward[rewardIndex];
                if (good1 == null) {
                    if (checkSpecialGoods(cargo1, good2))
                        addGoods(cargo1, cardReward, goodIndex, rewardIndex);
                    else
                        throw new CargoManagementException("Can't put a Red block in grey cargo");
                } else {
                    throw new CargoManagementException("You can't add a good on a busy spot");
                }
            } else
                throw new CargoManagementException("At least one goods index is outbound");
        } else
            throw new CargoManagementException("cargo index is outbound");
    }

    private void swapGoods(GoodsContainer cargo1, GoodsContainer cargo2, int j1, int j2) {

        GoodsBlock[] goods1 = cargo1.getGoods();
        GoodsBlock[] goods2 = cargo2.getGoods();

        GoodsBlock temp = goods1[j1];
        goods1[j1] = goods2[j2];
        goods2[j2] = temp;

    }

    private boolean checkSpecialGoods(GoodsContainer cargo1, GoodsContainer cargo2, GoodsBlock good1, GoodsBlock good2) {
        if ((((good1 != null) && good1.getType() == RED && !cargo2.isSpecial())) || (((good2 != null) && good2.getType() == RED && !cargo1.isSpecial())))
            return false;
        return true;

    }

    private boolean checkSpecialGoods(GoodsContainer cargo, GoodsBlock good) {
        if ((good.getType() == RED && !cargo.isSpecial()))
            return false;
        return true;
    }

    private void removeGoods(GoodsContainer cargo1, int j1) {
        cargo1.getGoods()[j1] = null;
    }

    private void addGoods(GoodsContainer cargo1, GoodsBlock[] cardReward, int j1, int k) {
        cargo1.getGoods()[j1] = cardReward[k];
        cardReward[k] = null;
    }

    public void looseGoods(int lostOther) {
        int actualLost = 0;
        if (getStorage() < lostOther)
            actualLost = getStorage();
        else
            actualLost = lostOther;

        ArrayList<GoodsContainer> playerCargos = goodsContainers;

        for (int i = 0; i < actualLost; i++) {
            int i1 = 0; // indice cargo
            int j1 = 0; // indice good
            if (i1 >= 0 && i1 < playerCargos.size()) {
                GoodsContainer cargo1 = playerCargos.get(i1);
                if (j1 >= 0 && j1 < cargo1.getGoods().length) {
                    removeGoods(cargo1, j1);
                } else
                    System.out.println("goods index is outbound");
            } else
                System.out.println("cargo index is outbound");
        }
    }

    public ArrayList<CargoHolds> getCargoHolds() {
        return cargoHolds;
    }

    public ArrayList<PowerCenter> getPowerCenters() {
        return powerCenters;
    }

    public ComponentTile[][] getComponents() {
        return components;
    }

    public ArrayList<Engine> getEngines() {
        return engines;
    }

    public ArrayList<Cannon> getCannons() {
        return cannons;
    }

    public ArrayList<Cabin> getCabins() {
        return cabins;
    }

    public ArrayList<ShieldGenerator> getShields() {
        return shieldGenerators;
    }

    private void countBatteries(){
        nBatteries=0;
        for(PowerCenter powerCenter : powerCenters){
            for(boolean b: powerCenter.getBatteries())
                if(b)
                    nBatteries++;
        }
    }

    public int getStorage() {
        int total = 0;
        for (CargoHolds c : cargoHolds) {
            for (GoodsBlock block : c.getGoods()) {
                if (block != null)
                    total++;
            }
        }
        return total;
    }

    public int countExposedConnectors() {

        exposedConnectors = 0;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 6; x++) {
                if (inBounds(x, y) && components[y][x] != null) {
                    ComponentTile tile = components[y][x];

                        int[] dirx = DIR_X;
                        int[] diry = DIR_Y;

                        ConnectorType[] connectors = tile.getConnectors();
                        for (int i = 0; i < connectors.length; i++) {
                            if (connectors[i] != ConnectorType.SMOOTH && connectors[i] != ConnectorType.CANNON && connectors[i] != ConnectorType.ENGINE) {
                                int x2 = x + dirx[i];
                                int y2 = y + diry[i];
                                ComponentTile tile2 = components[y2][x2];
                                if (tile2 == null)
                                    exposedConnectors++;
                            }
                        }
                }
            }
        }

        return exposedConnectors;
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
        int x = 0, y = 0;
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

        for (int i = 0; i < max_lenght || hit == null; i++) {
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

        if (components[y][x] != null) {
            reserveSpot.add(hit);
            remove(x, y);
        }
    }

    public int checkProtection(Direction direction, int position) {
        int max_lenght = 7;
        // casella da cui partire
        int x = 0, y = 0;
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

        for (int i = 0; i < max_lenght || hit == null; i++) {
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

        if (components[y][x] == null) {
            return -1; // se non veniamo colpiti
        }
        if (cannons.contains((Cannon) hit)) {
            int dirP = direction.ordinal();
            int dirDD = getCannonDirection((Cannon) hit);
            if (dirP == dirDD) {
                if (hit instanceof DoubleCannon) {
                    return 2; // se abbiamo un doppio cannone
                }
                return 1; // se abbiamo un cannone singolo
            }
        }
        return 0; // se non abbiamo un cannone
    }

    public int getCannonDirection(Cannon cannon) {
        ConnectorType[] cannonConnectors = cannon.getConnectors();
        for (int i = 0; i < cannonConnectors.length; i++) {
            if (cannonConnectors[i] == ConnectorType.CANNON)
                return i;
        }
        throw new IllegalStateException("Not a cannon connector in a cannon tile");
    }

    public boolean getCannonActivation(Direction direction, int position) {
//        if (checkProtection(direction, position) == true) {
//            boolean active = askActivateCannon();
//            if (active) return true;
//        }

        return false;
    }

    private boolean askActivateCannon() {
        return true;
    }

    public ArrayList<Cabin> getConnectedCabins() {
        ArrayList<Cabin> connectedCabins = new ArrayList<>();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 6; x++) {
                ComponentTile tile = components[y][x];
                if (tile instanceof Cabin) {

                    int[] dirx = DIR_X;
                    int[] diry = DIR_Y;

                    ConnectorType[] connectors = tile.getConnectors();
                    for (int i = 0; i < connectors.length; i++) {
                        // questo approccio funziona perché do per scontato che la nave sia ben connessa a sto punto
                        // Se da quel lato c'è un connettore singolo/doppio/universale vuol dire che è collegato a un'altra casella. Controllo che sia anch'essa una cabina
                        if (connectors[i] != ConnectorType.SMOOTH && connectors[i] != ConnectorType.CANNON && connectors[i] != ConnectorType.ENGINE) {
                            int x2 = x + dirx[i];
                            int y2 = y + diry[i];
                            ComponentTile tile2 = components[y2][x2];
                            if (tile2 instanceof Cabin){
                                if (!connectedCabins.contains(tile2)) {
                                    connectedCabins.add((Cabin) tile2);
                                }
                            }

                        }
                    }
                    if (!connectedCabins.contains(tile)) {
                        connectedCabins.add((Cabin) tile);
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

    public boolean askPlaceTile() {
        return true;
    }

    public void placeTileReserveSpot(ComponentTile tile) {
        if (reserveSpot.size() < 2)
            reserveSpot.add(tile);
        else
            System.out.println("Full reserve spot");
    }

    public void placeTileComponents(ComponentTile tile, int x, int y) throws SpaceShipPlanceException {
        if (x < 0 || x > 6 || y < 0 || y > 4 || edgeCases(y, x))
            throw new SpaceShipPlanceException("Outbound index");
        else if (components[y][x] != null) {
            throw new SpaceShipPlanceException("Already busy spot");
        }
        components[y][x] = tile;
        tile.setWellConnected(checkNewTile(x, y));
    }

    public void placeReserveToComponents(ComponentTile tile, int x, int y) {
        placeTileComponents(tile, x, y);
        reserveSpot.remove(tile);
    }


    public boolean checkExposedConnector(Direction direction, int position) {
        int max_lenght = 7;
        // casella da cui partire
        int x = 0, y = 0;
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

        for (int i = 0; i < max_lenght || hit == null; i++) {
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

        ArrayList<ConnectorType> disallowedConnectors = new ArrayList<>(Arrays.asList(ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.UNIVERSAL));

        if ((hit != null) && (disallowedConnectors.contains(hit.getConnectors()[direction.ordinal()]))) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        String result = "";

        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < components[0].length; j++) {
                result += components[i][j] + ", ";
            }
            result += "\n";
        }

        return result;
    }

    public String tileGridToString() {
        int rows = this.components.length;
        int cols = this.components[0].length;
        StringBuilder result = new StringBuilder();

        result.append('\n');

        for (int tileRow = 0; tileRow < rows; tileRow++) {
            for (int line = 0; line < 3; line++) {
                for (int tileCol = 0; tileCol < cols; tileCol++) {
                    char[][] tileChars = tileCrafter(this.components[tileRow][tileCol]);
                    if (edgeCases(tileRow, tileCol)) {
                        result.append("   ");
                    } else {
                        for (int c = 0; c < 3; c++) {
                            result.append(tileChars[line][c]);
                        }
                    }
                }
                result.append('\n'); // End of one horizontal line across tile row
            }
        }

        return result.toString();
    }


    private char[][] tileCrafter(ComponentTile tile) {
        char[][] lines = {
                {'┌', '-', '┐'},
                {'|', ' ', '|'},
                {'└', '-', '┘'}
        };

        if (tile == null) return lines;

        // centro
        char center = TileSymbols.ASCII_TILE_SYMBOLS.get(tiletoString(tile));
        lines[1][1] = center;

        // connettori
        ConnectorType[] connectors = tile.getConnectors();
        lines[0][1] = connectorToChar(connectors[0]);
        lines[1][2] = connectorToChar(connectors[1]);
        lines[2][1] = connectorToChar(connectors[2]);
        lines[1][0] = connectorToChar(connectors[3]);

        // scudo
        if (tile instanceof ShieldGenerator) {
            boolean[] protection = ((ShieldGenerator) tile).getProtection();
            if (protection[0] && protection[1]) {
                lines[0][2] = 'S';
            } else if (protection[1] && protection[2]) {
                lines[2][2] = 'S';
            } else if (protection[2] && protection[3]) {
                lines[2][0] = 'S';
            } else {
                lines[0][0] = 'S';
            }
        }

        return lines;
    }

    public String tileGridToStringAdjustments() {
        int rows = this.components.length;
        int cols = this.components[0].length;
        StringBuilder result = new StringBuilder();

        result.append('\n');

        for (int tileRow = 0; tileRow < rows; tileRow++) {
            for (int line = 0; line < 3; line++) {
                for (int tileCol = 0; tileCol < cols; tileCol++) {
                    char[][] tileChars = tileCrafterAdjustments(this.components[tileRow][tileCol]);
                    if (edgeCases(tileRow, tileCol)) {
                        result.append("   ");
                    } else {
                        for (int c = 0; c < 3; c++) {
                            result.append(tileChars[line][c]);
                        }
                    }
                }
                result.append('\n'); // End of one horizontal line across tile row
            }
        }

        return result.toString();
    }

    private char[][] tileCrafterAdjustments(ComponentTile tile) {
        char[][] lines = {
                {'┌', '-', '┐'},
                {'|', ' ', '|'},
                {'└', '-', '┘'}
        };

        if (tile == null) return lines;

        // centro
        char center;
        if (tile.isWellConnected())
            center = TileSymbols.ASCII_TILE_SYMBOLS.get(tiletoString(tile));
        else
            center = 'X';

        lines[1][1] = center;

        // connettori
        ConnectorType[] connectors = tile.getConnectors();
        lines[0][1] = connectorToChar(connectors[0]);
        lines[1][2] = connectorToChar(connectors[1]);
        lines[2][1] = connectorToChar(connectors[2]);
        lines[1][0] = connectorToChar(connectors[3]);

        // scudo
        if (tile instanceof ShieldGenerator) {
            boolean[] protection = ((ShieldGenerator) tile).getProtection();
            if (protection[0] && protection[1]) {
                lines[0][2] = 'S';
            } else if (protection[1] && protection[2]) {
                lines[2][2] = 'S';
            } else if (protection[2] && protection[3]) {
                lines[2][0] = 'S';
            } else {
                lines[0][0] = 'S';
            }
        }

        return lines;
    }

    private char connectorToChar(ConnectorType ct) {
        switch (ct) {
            case UNIVERSAL -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("universal");
            }
            case SINGLE -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("single");
            }
            case DOUBLE -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("double");
            }
            case SMOOTH -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("smooth");
            }
            case CANNON -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("cannon");
            }
            case ENGINE -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("engine");
            }
            default -> {
                return '?';
            }
        }
    }

    private String tiletoString(ComponentTile tile) {
        if (tile != null) {
            switch (tile) {
                case DoubleCannon dc -> {
                    return "DoubleCannon";
                }

                case Cannon c -> {
                    return "Cannon";
                }

                case DoubleEngine de -> {
                    return "DoubleEngine";
                }
                case Engine e -> {
                    return "Engine";
                }
                case Cabin cab -> {
                    return "Cabin";
                }
                case CargoHolds ch -> {
                    return "CargoHolds";
                }

                case ShieldGenerator sg -> {
                    return "ShieldGenerator";
                }

                case LifeSupportSystem lfs -> {
                    return "LifeSupportSystem";
                }

                case PowerCenter pc -> {
                    return "PowerCenter";
                }

                case StructuralModule sm -> {
                    return "StructuralModule";
                }

                default -> {
                    return "not Catched in tiletoString";
                }
            }
        }
        return null;
    }

    public String reserveSpotToString() {
        StringBuilder result = new StringBuilder();

        result.append('\n');

        // For each of the 3 lines in a 3×3 tile
        for (int line = 0; line < 3; line++) {
            for (ComponentTile tile : this.reserveSpot) {
                char[][] tileChars = tileCrafter(tile);
                for (int c = 0; c < 3; c++) {
                    result.append(tileChars[line][c]);
                }
            }
            result.append('\n');
        }

        return result.toString();
    }

    public ArrayList<ComponentTile> getReserveSpot() {
        return reserveSpot;
    }


    public String tileGridToStringParts() {
        int rows = this.shownComponents.length;
        int cols = this.shownComponents[0].length;
        StringBuilder result = new StringBuilder();

        result.append('\n');

        for (int tileRow = 0; tileRow < rows; tileRow++) {
            for (int line = 0; line < 3; line++) {
                for (int tileCol = 0; tileCol < cols; tileCol++) {
                    char[][] tileChars = tileCrafterParts(this.shownComponents[tileRow][tileCol]);
                    if (edgeCases(tileRow, tileCol)) {
                        result.append("   ");
                    } else {
                        for (int c = 0; c < 3; c++) {
                            result.append(tileChars[line][c]);
                        }
                    }
                }
                result.append('\n'); // End of one horizontal line across tile row
            }
        }

        return result.toString();
    }


    private char[][] tileCrafterParts(int tile) {
        char[][] lines = {
                {'┌', '-', '┐'},
                {'|', ' ', '|'},
                {'└', '-', '┘'}
        };

        if (tile == -1) return lines;

        // centro
        char center = (char) (tile + '0');
        lines[1][1] = center;

        return lines;
    }

    public String tileGridToStringTile(ComponentTile tileToShow) {
        int rows = this.components.length;
        int cols = this.components[0].length;
        StringBuilder result = new StringBuilder();

        result.append('\n');

        for (int tileRow = 0; tileRow < rows; tileRow++) {
            for (int line = 0; line < 5; line++) {  // Ora 5 linee per tile
                for (int tileCol = 0; tileCol < cols; tileCol++) {
                    char[][] tileChars = tileCrafterbyTile(this.components[tileRow][tileCol], tileToShow);
                    if (edgeCases(tileRow, tileCol)) {
                        result.append("     ");  // 5 spazi per mantenere l'allineamento
                    } else {
                        for (int c = 0; c < 5; c++) {
                            result.append(tileChars[line][c]);
                        }
                    }
                }
                result.append('\n');
            }
        }

        return result.toString();
    }

    private char[][] tileCrafterbyTile(ComponentTile tile, ComponentTile tileToShow) {
        char[][] lines = {
                {'┌', '-', '-', '-', '┐'},  // Riga superiore
                {'|', ' ', ' ', ' ', '|'},   // Riga centrale superiore
                {'|', ' ', ' ', ' ', '|'},   // Riga centrale (ID qui)
                {'|', ' ', ' ', ' ', '|'},   // Riga centrale inferiore
                {'└', '-', '-', '-', '┘'}    // Riga inferiore
        };

        if (tile == null) return lines;

        // Centro (ID a 2-3 cifre)
        String idStr = String.valueOf(tile.getId());
        int centerRow = 2;  // Riga centrale per l'ID

        if (tile.getClass().equals(tileToShow.getClass())) {
            // Allinea l'ID al centro (2-3 cifre)
            int startPos = (5 - idStr.length()) / 2;  // Calcola la posizione di inizio
            for (int i = 0; i < idStr.length(); i++) {
                lines[centerRow][startPos + i] = idStr.charAt(i);
            }
        } else {
            // Altri casi (simbolo singolo)
            char center = TileSymbols.ASCII_TILE_SYMBOLS.get(tiletoString(tile));
            lines[centerRow][2] = center;  // Posizione centrale per simboli
        }

        // Connettori (aggiornati per la griglia 5x5)
        ConnectorType[] connectors = tile.getConnectors();
        lines[0][2] = connectorToChar(connectors[0]);  // Alto
        lines[2][4] = connectorToChar(connectors[1]);  // Destra
        lines[4][2] = connectorToChar(connectors[2]);  // Basso
        lines[2][0] = connectorToChar(connectors[3]);  // Sinistra

        // Scudo (esempio, adatta alla nuova griglia)
        if (tile instanceof ShieldGenerator) {
            boolean[] protection = ((ShieldGenerator) tile).getProtection();
            if (protection[0] && protection[1]) lines[0][4] = 'S';
            else if (protection[1] && protection[2]) lines[4][4] = 'S';
            else if (protection[2] && protection[3]) lines[4][0] = 'S';
            else lines[0][0] = 'S';
        }

        return lines;
    }

    public boolean removeMVGood(int cargoIndex, int goodIndex) {
        ArrayList<CargoHolds> playerCargo = getCargoHolds();
        ArrayList<GoodsBlock> playerGoods = new ArrayList<>();
        for (CargoHolds cargo : playerCargo) {
            GoodsBlock[] goods = cargo.getGoods();
            Collections.addAll(playerGoods, goods);
        }
        playerGoods.sort(Comparator.comparingDouble(GoodsBlock::getValue).reversed());

        if (cargoIndex >= 0 && cargoIndex < goodsContainers.size()) {
            GoodsContainer cargo1 = goodsContainers.get(cargoIndex);
            if (goodIndex >= 0 && goodIndex < cargo1.getGoods().length) {
                if(cargo1.getGoods()[goodIndex].getValue() >= playerGoods.getFirst().getValue() ){
                    removeGoods(cargo1, goodIndex);
                    return true;
                }else
                    return false;
            } else
                throw new CargoManagementException("goods index is outbound");
        } else
            throw new CargoManagementException("cargo index is outbound");
    }
}
