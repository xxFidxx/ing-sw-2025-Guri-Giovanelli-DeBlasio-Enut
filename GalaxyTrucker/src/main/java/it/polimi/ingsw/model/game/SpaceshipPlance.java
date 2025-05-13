package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.resources.GoodsContainer;
import it.polimi.ingsw.model.resources.TileSymbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.model.componentTiles.AlienColor.*;
import static it.polimi.ingsw.model.game.ColorType.*;

public class SpaceshipPlance {
    private final ComponentTile[][] components;
    private final ArrayList<ComponentTile> reserveSpot;
    private final ArrayList<CargoHolds> cargoHolds;
    private final ArrayList<Engine> engines;
    private ArrayList<Cannon> cannons;
    private final ArrayList<Cabin> cabins;
    private boolean[][] visited;
    private int[][] shownComponents;
    private final ArrayList<ShieldGenerator> shieldGenerators;
    private int nAstronauts;
    private int nBrownAliens;
    private int nPurpleAliens;
    private int exposedConnectors;
    private ArrayList<GoodsContainer> goodsContainers;


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
        components[2][3] = new Cabin(cannonConnectors, true, -1);
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
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                visited[i][j] = false;
            }
        }
    }

    private void initShownComponents() {
        // Imposta shownComponents a -1
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                shownComponents[i][j] = -1;
            }
        }
    }

    public void addReserveSpot(ComponentTile c) {
        this.reserveSpot.add(c);
    }

    public void updateLists() {
        //cannons.clear();
        //engines.clear();
        cabins.clear();
        cargoHolds.clear();
        shieldGenerators.clear();
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

    private void removeIslands() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                if (!visited[i][j]) {
                    components[i][j] = null;
                }
            }
        }
    }

    public void selectPart(int iteration) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                if (shownComponents[i][j] != iteration) {
                    components[i][j] = null;
                }
            }
        }
    }

    public boolean checkCorrectness() {
        initVisited();
        dfsCorrectness(3, 2); // per ora parto dal centro
        removeIslands();
        boolean correct = true;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                ComponentTile tile = components[i][j];
                if (tile != null) {
                    if (!tile.isWellConnected()) {
                        correct = false;
                        break;
                    }
                }
            }
        }
        return correct;
    }

    private void dfsCorrectness(int x, int y) {

        // visita tutti le tile in profondità e dice se sono connesse bene
        if (x < 0 || x >= 6 || y < 0 || y >= 4 ||
                components[y][x] == null || visited[y][x]) {
            System.out.println("x < 0 || x >= 6 || y < 0 || y >= 4 || edgeCases(y, x) ||\n" +
                    "                components[y][x] == null || visited[y][x])");
            return;
        }

        ComponentTile tile = components[y][x];
        visited[y][x] = true;

        //sopra destra sotto sinistra
        int[] dirx = {0, 1, 0, -1};
        int[] diry = {-1, 0, 1, 0};

        // caso default, nel costruttore abbiamo WellConnected a true di default
        ConnectorType[] connectors = tile.getConnectors();

        for (int i = 0; i < 4; i++) {
            int x2 = x + dirx[i];
            int y2 = y + diry[i];

            if (connectors[i] != ConnectorType.SMOOTH) {
                ComponentTile tile2 = components[y2][x2];

                //controllo se il connettore è un engine puntato verso una direzione diversa da sud
                if (i != 2 && connectors[i] == ConnectorType.ENGINE)
                    tile.setWellConnected(false);

                if (tile2 != null) {
                    ConnectorType[] connectors2 = tile2.getConnectors();

                    if (i == 2 && connectors[i] == ConnectorType.ENGINE) {
                        tile.setWellConnected(false);
                        tile2.setWellConnected(false);
                    }


                    if (connectors[i] == ConnectorType.CANNON)
                        tile2.setWellConnected(false);
                    // per ogni connettore confronto quello della cella adiacente opposto, se è gia a false perchè è engine non entro
                    if (tile.isWellConnected() && (checkConnection(connectors[i], connectors2[(i + 2) % 4]))) {
                        tile.setWellConnected(false);
                    }
                    dfsCorrectness(x2, y2);
                }
                else
                    System.out.println("else (tile2 != null) {");
            }else
                System.out.println("else (connectors[i] != ConnectorType.SMOOTH) {");
        }
    }


    public int remove(int x, int y) {
        initShownComponents();
        initVisited();
        components[y][x] = null;
        int realIterations = 0;
        int[] dirx = {0, 1, 0, -1};
        int[] diry = {1, 0, -1, 0};

        // fai partire il dfs dalle 4 caselle adiacenti al pezzo rimosso
        for (int i = 0; i < 4; i++) {
            realIterations += dfsRemove(x + dirx[i], y + diry[i], i);
        }

        return realIterations;
    }


    private int dfsRemove(int x, int y, int iteration) {

        if (x < 0 || x > 6 || y < 0 || y > 4 ||
                components[y][x] == null || visited[y][x]) {
            return 0;
        }

        ComponentTile tile = components[y][x];
        // per ogni casella mi segno se è stata visitata e quale iterazione (quindi troncone) appartiene
        visited[y][x] = true;
        shownComponents[y][x] = iteration;

        //sopra destra sotto sinistra
        int[] dirx = {0, 1, 0, -1};
        int[] diry = {1, 0, -1, 0};

        // caso default, nel costruttore abbiamo WellConnected a true di default
        ConnectorType[] connectors = tile.getConnectors();

        for (int i = 0; i < 4; i++) {
            int x2 = x + dirx[i];
            int y2 = y + diry[i];

            if (connectors[i] != ConnectorType.SMOOTH) {
                ComponentTile tile2 = components[y2][x2];


                if (tile2 != null) {
                    ConnectorType[] connectors2 = tile2.getConnectors();
                    if ((checkConnection(connectors[i], connectors2[(i + 2) % 4]))) {
                        dfsRemove(x2, y2, iteration);
                    }

                }
            }
        }
        return 1;
    }


    private boolean checkConnection(ConnectorType connector, ConnectorType connector2) {


        if (connector == ConnectorType.CANNON || connector2 == ConnectorType.CANNON)
            return false;

        if (connector == ConnectorType.ENGINE || connector2 == ConnectorType.ENGINE)
            return false;

        // se c'è un universale ora è sicuramente true perchè abbiamo già controllato cannoni ed engine
        if (connector == ConnectorType.UNIVERSAL && connector2 != ConnectorType.SMOOTH){
            System.out.println("if (connector == ConnectorType.UNIVERSAL && connector2 != ConnectorType.SMOOTH)");
            return true;
        }

        if (connector == ConnectorType.SINGLE && connector2 == ConnectorType.SINGLE){
            System.out.println("if (connector == ConnectorType.SINGLE && connector2 != ConnectorType.SINGLE)");
            return true;
        }

        if (connector == ConnectorType.DOUBLE && connector2 == ConnectorType.DOUBLE){
            System.out.println("if (connector == ConnectorType.DOUBLE && connector2 != ConnectorType.DOUBLE)");
            return true;
        }


        return false;
    }


    public void countFigures() {
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
                ComponentTile tile = components[y][x];

                if (tile != null) {

                    int[] dirx = {0, 1, 0, -1};
                    int[] diry = {1, 0, -1, 0};

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

        if (hit != null) {
            reserveSpot.add(hit);
            remove(x, y);
        }
    }

    public boolean checkProtection(Direction direction, int position) {
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

        if ((hit != null) && (cannons.contains(hit))) {
            return true;
        }

        return false;
    }

    public boolean getCannonActivation(Direction direction, int position) {
        if (checkProtection(direction, position) == true) {
            boolean active = askActivateCannon();
            if (active) return true;
        }

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

                    int[] dirx = {0, 1, 0, -1};
                    int[] diry = {1, 0, -1, 0};

                    ConnectorType[] connectors = tile.getConnectors();
                    for (int i = 0; i < connectors.length; i++) {
                        // Se da quel lato c'è un connettore singolo/doppio/universale vuol dire che è collegato a un'altra casella. Controllo che sia anch'essa una nave
                        if (connectors[i] != ConnectorType.SMOOTH && connectors[i] != ConnectorType.CANNON && connectors[i] != ConnectorType.ENGINE) {
                            int x2 = x + dirx[i];
                            int y2 = y + diry[i];
                            ComponentTile tile2 = components[y2][x2];
                            if (tile2 instanceof Cabin)
                                connectedCabins.add((Cabin) tile2);
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
        if (x < 0 || x >= 6 || y < 0 || y >= 4 || edgeCases(y, x))
            throw new SpaceShipPlanceException("Outbound index");
        else if (components[y][x] != null) {
            throw new SpaceShipPlanceException("Already busy spot");
        }
        components[y][x] = tile;
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
}
