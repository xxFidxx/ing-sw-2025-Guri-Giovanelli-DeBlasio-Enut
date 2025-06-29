package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.network.data.EpidemicManagement;
import it.polimi.ingsw.controller.network.data.TileData;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.resources.GoodsContainer;
import it.polimi.ingsw.model.resources.TileSymbols;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.model.componentTiles.AlienColor.*;
import static it.polimi.ingsw.model.game.ColorType.*;

/**
 * The SpaceshipPlance class represents a system for managing and constructing a spaceship
 * with various components and subsystems. It provides functionality to initialize, modify,
 * and validate the structure and components of the spaceship, as well as manage cargo and
 * crew members. The class also supports grid manipulation, allowing for placement, removal,
 * and validation of components on a 2D plane. Additionally, it tracks attributes such as
 * astronauts, aliens, batteries, and exposed connectors.
 */

public class SpaceshipPlance {
    private final ComponentTile[][] components;
    private final ArrayList<ComponentTile> reserveSpot;
    private final ArrayList<CargoHolds> cargoHolds;
    private final ArrayList<Engine> engines;
    private final ArrayList<Cannon> cannons;
    private final ArrayList<Cabin> cabins;
    private final ArrayList<Cabin> interconnectedCabins;
    private final ArrayList<PowerCenter> powerCenters;
    private final boolean[][] visited;
    private int[][] shownComponents;
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
    private boolean isCorrect;


    public SpaceshipPlance() {
        this.components = new ComponentTile[5][7];
        this.reserveSpot = new ArrayList<>();
        this.visited = new boolean[5][7];
        this.shownComponents = new int[5][7];
        this.exposedConnectors = 0;
        this.shieldGenerators = new ArrayList<>();
        this.cannons = new ArrayList<>();
        this.cabins = new ArrayList<>();
        this.interconnectedCabins = new ArrayList<>();
        this.cargoHolds = new ArrayList<>();
        this.powerCenters = new ArrayList<>();
        this.engines = new ArrayList<>();
        this.nAstronauts = 0;
        this.nBrownAliens = 0;
        this.nPurpleAliens = 0;
        this.goodsContainers = new ArrayList<>();
        this.isCorrect = false;

        ConnectorType[] centralCabinConnectors = {
                ConnectorType.UNIVERSAL,   // Lato superiore
                ConnectorType.UNIVERSAL,   // Lato destro
                ConnectorType.UNIVERSAL,   // Lato inferiore
                ConnectorType.UNIVERSAL    // Lato sinistro
        };
        components[2][3] = new Cabin(centralCabinConnectors, true, 32);
        ComponentTile tile = components[2][3];
        tile.setWellConnected(true);
    }

    public void setGoodsContainers(ArrayList<GoodsContainer> goodsContainers) {
        this.goodsContainers = goodsContainers;
    }

    public boolean isCorrect(){
        return isCorrect;
    }

    public ArrayList<GoodsContainer> getGoodsContainers() {
        return goodsContainers;
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

    public int[][] getShownComponents() {
        return shownComponents;
    }

    public void setShownComponents(int[][] grid) {
        this.shownComponents = grid;
    }

    public void setComponent(int y, int x, ComponentTile tile) {
        components[y][x] = tile;
    }
    public ComponentTile getComponent(int y, int x) {
        return components[y][x];
    }

    public boolean[][] getVisited() {
        return visited;
    }

    public static int getCOLS() {
        return COLS;
    }

    public static int getROWS() {
        return ROWS;
    }


    /**
     * Checks specific edge case conditions for the given coordinates (y, x).
     * The conditions are based on predetermined values for y and corresponding potential x values.
     *
     * @param y the row index to check
     * @param x the column index to check
     * @return true if the given (y, x) coordinates satisfy the edge case conditions, false otherwise
     */
    public boolean edgeCases(int y, int x) {
        if (y == 0) {
            return x == 0 || x == 1 || x == 3 || x == 5 || x == 6;
        } else if (y == 1) {
            return x == 0 || x == 6;
        } else if (y == 4) {
            return x == 3;
        }
        return false;
    }

    /**
     * Initializes the `visited` matrix by setting all its elements to `false`.
     * This method iterates through each cell of the matrix and resets its value,
     * typically used to prepare for operations that require tracking visitation state.
     */
    public void initVisited() {
        // Imposta visited a false
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                visited[i][j] = false;
            }
        }
    }

    /**
     * Initializes the `shownComponents` matrix by setting all its elements to `-1`.
     * This method iterates over every cell within the matrix defined by the dimensions `ROWS` x `COLS`,
     * resetting each value to `-1`.
     *
     * This method is typically used to prepare the components for a default or uninitialized state
     * before conducting further operations on them.
     */
    public void initShownComponents() {
        // Imposta shownComponents a -1
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                shownComponents[i][j] = -1;
            }
        }
    }

    /**
     * Adds a ComponentTile to the reserve spot list.
     *
     * @param c the ComponentTile to be added to the reserve spot
     */
    public void addReserveSpot(ComponentTile c) {
        this.reserveSpot.add(c);
    }



    /**
     * Updates the state of multiple component lists within the spaceship.
     *
     * This method clears existing lists of various ship components (e.g., cannons,
     * engines, cabins, cargo holds, shield generators, and power centers), and
     * rebuilds them by iterating through the ship's grid of components. It categorizes
     * each tile into its respective list based on its type.
     *
     * During the process, specific operations are performed for certain component
     * types:
     * - For cabins, life support connections and crew situations are analyzed.
     *   Adjustments are made to alien figures depending on the presence or removal
     *   of life support systems, ensuring data consistency.
     * - Interconnected cabins are identified and updated if they meet specific
     *   conditions.
     * - Life support colors are checked and reset as necessary to maintain correct
     *   associations with cabins.
     *
     * Once all lists are rebuilt and the required operations are performed, methods
     * for counting figures and batteries are invoked to ensure that all dependent
     * systems are updated accordingly.
     *
     * This method is designed to accommodate dynamic updates, ensuring that the
     * internal state of the spaceship accurately reflects the current arrangement
     * and condition of its components.
     */
    public void updateLists() {
        cannons.clear();
        engines.clear();
        cabins.clear();
        cargoHolds.clear();
        shieldGenerators.clear();
        powerCenters.clear();
        interconnectedCabins.clear();
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                ComponentTile tile = components[y][x];

                if (tile != null) {
                    switch (tile) {
                        case Cannon c -> cannons.add(c);

                        case Engine e -> engines.add(e);
                        case Cabin cab -> {
                            cabins.add(cab);
                            // I reset aliens of previous check, maybe the lifesupport system has been destroyed so I need to do that to ensure the alien doesnt remain on the cabin
                            AlienColor[] colors = cab.getLifeSupportSystemColor();
                            Figure[] figures = cab.getFigures();
                            colors[0] = null;
                            colors[1] = null;
                                for (int dir = 0; dir < 4; dir++) {
                                    int nx = x + DIR_X[dir];
                                    int ny = y + DIR_Y[dir];
                                    if (inBounds(nx, ny) && components[ny][nx] != null) {
                                        ComponentTile tile2 = components[ny][nx];
                                        if (tile2 instanceof LifeSupportSystem || tile2 instanceof Cabin) {
                                            ConnectorType a = tile.getConnectors()[dir];
                                            ConnectorType b = components[ny][nx].getConnectors()[(dir + 2) % 4];

                                            if (isConnectionValid(a, b)) {
                                                // se cambio l'ordine dei colori nell'enum, cambierà anche questo. Basta usare sempre l'ordinal e sarà tutto coerente.
                                                // la cabina ha un array di AlienColors a 2 posti, metto a true la casella corrispondente al colore dell'alieno, faccio
                                                // cosi perchè nel caso ci siano 2 colori, sono sicuro che non vado a sovrascrivere la casella contenente già un colore
                                                if(tile2 instanceof LifeSupportSystem)
                                                    colors[((LifeSupportSystem) tile2).getColor().ordinal()] = ((LifeSupportSystem) tile2).getColor();
                                                else if(figures[0] != null || figures[1] != null){
                                                    // if a cabin has 0 crew, I don't need to use it epidemic card
                                                    if(!interconnectedCabins.contains(cab))
                                                        interconnectedCabins.add(cab);
                                                }
                                            }
                                        }
                                    }
                                }
                                // if a lifesupport has been removed, it means there is an alien on the cabin but there isn't anymore the corresponding color in the colors array
                                // this could happen because I put every time colors setted to null, and if there is no more link between cab and life support, the corresponding color is null
                                if(figures[0] != null && figures[0] instanceof Alien && colors[((Alien) figures[0]).getColor().ordinal()] == null)
                                    figures[0] = null;
                        }
                        case CargoHolds ch -> cargoHolds.add(ch);

                        case ShieldGenerator sg -> shieldGenerators.add(sg);

                        case PowerCenter pc -> powerCenters.add(pc);

                        default -> {
                        }
                    }
                }
            }
        }

        countFigures();
        countBatteries();
    }

    /**
     * Determines if the specified coordinates (x, y) are within the valid bounds of the grid
     * and do not fall under predefined edge case conditions.
     *
     * @param x the column index to check
     * @param y the row index to check
     * @return true if the coordinates are both within the standard bounds and do not meet edge case criteria, false otherwise
     */
    public boolean inBounds(int x, int y) {
        // Prima controlla i bound standard
        boolean standardBounds = (x >= 0 && x < COLS && y >= 0 && y < ROWS);

        // Poi verifica gli edge case specifici della forma
        return standardBounds && !edgeCases(y, x);
    }

    /**
     * Checks the correctness of the current configuration of components on the grid.
     *
     * This method performs a series of operations to ensure the validity of the grid:
     * 1. Initializes the `visited` matrix to track examined components.
     * 2. Determines the starting tile for validation:
     *    - If the central tile (3, 2) is null, searches for the first non-null tile as the starting point.
     *    - Otherwise, starts exploration from the central tile.
     * 3. Conducts a depth-first search (DFS) exploration to mark all connected and valid tiles.
     * 4. Removes any tiles that are not connected to the valid cluster.
     * 5. Verifies the remaining tiles to confirm that all configurations meet the spaceship's requirements.
     *
     * @return true if the grid configuration is valid after verification, false otherwise
     */
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
        isCorrect = validateRemainingTiles();
        return isCorrect;
    }

    /**
     * Explores the grid using a depth-first search (DFS) algorithm starting at the given coordinates.
     * The method marks tiles as visited, ensuring that only unvisited, in-bounds, and valid tiles are processed.
     * Smooth connectors are ignored during the exploration process.
     *
     * @param x the column index of the starting tile
     * @param y the row index of the starting tile
     */
    public void dfsExploration(int x, int y) {
        if (!inBounds(x, y) || components[y][x] == null || visited[y][x]) {
            return;
        }

        visited[y][x] = true;

        ComponentTile tile = components[y][x];

        for (int dir = 0; dir < 4; dir++) {
            // Non seguire i connettori smooth
            if (tile.getConnectors()[dir] == ConnectorType.SMOOTH) continue;

            int nx = x + DIR_X[dir];
            int ny = y + DIR_Y[dir];
            dfsExploration(nx, ny);
        }
    }

    /**
     * Removes unvisited tiles from the spaceship grid and moves them to the reserve spot.
     *
     * This method iterates over the components grid, checking each tile to determine
     * whether it has been marked as visited. If a tile is not visited and it is not null,
     * it is added to the reserve spot list using the `addReserveSpot` method and is then
     * removed from the grid by setting its position to null.
     *
     * Iteration is performed row by row through the grid, with the dimensions determined
     * by the constants `ROWS` and `COLS`.
     */
    public void removeUnvisitedTiles() {
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

    /**
     * Validates the remaining tiles in the spaceship grid and updates their connection status.
     *
     * This method iterates through each component tile in the grid, checking its validity
     * using the `isTileValid` method. If a tile is valid, its connection status is set to
     * true; otherwise, it is set to false. Additionally, the method tracks whether all tiles
     * in the grid are valid.
     *
     * @return true if all tiles in the grid are valid after validation, false if at least one tile is invalid
     */
    public boolean validateRemainingTiles() {
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

    /**
     * Checks if a tile at the specified coordinates is valid based on its type, connections,
     * and placement rules within the grid structure.
     *
     * @param x the x-coordinate of the tile to be validated
     * @param y the y-coordinate of the tile to be validated
     * @return true if the tile is valid according to the defined rules, false otherwise
     */
    public boolean isTileValid(int x, int y) {
        ComponentTile tile = components[y][x];

        // Controllo componenti speciali
        if (tile instanceof Engine && !isEngineValid(y, x)) return false;
        if (tile instanceof Cannon && !isCannonValid(y, x)) return false;


        // dy: NORD, EST, SUD, OVEST

        for (int dir = 0; dir < 4; dir++) {
            int nx = x + DIR_X[dir];
            int ny = y + DIR_Y[dir];

            if (!inBounds(nx, ny) || components[ny][nx] == null) continue;

            ConnectorType a = tile.getConnectors()[dir];
            ConnectorType b = components[ny][nx].getConnectors()[(dir + 2) % 4];

            if (!isConnectionValid(a, b)) return false;
        }

        return true;
    }

    /**
     * Determines whether two connector types can form a valid connection
     * based on predefined connection rules.
     *
     * @param a the first connector type to be checked
     * @param b the second connector type to be checked
     * @return true if the connection between the two connector types is valid,
     *         otherwise returns false
     */
    public boolean isConnectionValid(ConnectorType a, ConnectorType b) {
        //System.out.println(a.toString() + " " + b.toString());
        boolean prova;
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

    /**
     * Checks if the engine at the given coordinates is valid.
     * This method validates the orientation and ensures that there is either
     * space or a void behind the engine.
     *
     * @param y The vertical coordinate of the engine.
     * @param x The horizontal coordinate of the engine.
     * @return true if the engine is valid according to the specified rules; false otherwise.
     */
    public boolean isEngineValid(int y, int x) {
        // Controlla solo orientamento e spazio dietro oppure se lo spazio dietro è nel vuoto
        return components[y][x].getConnectors()[2] == ConnectorType.ENGINE && (!inBounds(x,y+1) || components[y + 1][x] == null);
    }

    /**
     * Determines if a cannon located at the specified position is valid.
     * A cannon is considered valid if the direction it is facing is either
     * outside the bounds of the ship or pointing to an empty cell.
     *
     * @param y the row coordinate in the grid
     * @param x the column coordinate in the grid
     * @return true if the cannon is valid based on the direction it is facing,
     *         false otherwise
     */
    public boolean isCannonValid(int y, int x) {
        ConnectorType[] connectors = components[y][x].getConnectors();

        for (int direction = 0; direction < 4; direction++) {
            if (connectors[direction] == ConnectorType.CANNON) {
                int newY = y, newX = x;

                switch (direction) {
                    case 0: newY = y - 1; break; // nord
                    case 1: newX = x + 1; break; // est
                    case 2: newY = y + 1; break; // sud
                    case 3: newX = x - 1; break; // ovest
                }

                // Verifica che la casella nella direzione del cannone sia vuota o fuori dalla navicella
                return !inBounds(newX, newY) || components[newY][newX] == null;
            }
        }

        return false;
    }


    /**
     * Checks whether the tile at the specified position is valid based on various conditions.
     * This method verifies edge cases, bounds, specific rules for certain component types
     * (e.g., Engine or Cannon), and the validity of connections to adjacent tiles.
     *
     * @param x the x-coordinate of the tile to check
     * @param y the y-coordinate of the tile to check
     * @return true if the tile satisfies all the conditions for being valid; false otherwise
     */
    public boolean checkNewTile(int x, int y) {
        if (edgeCases(y, x) || !inBounds(x, y) || components[y][x] == null) {
            return false;
        }

        ComponentTile tile = components[y][x];
        boolean isValid = true;

        // Caso 1: Controllo speciale per ENGINE/CANNON
        if (tile instanceof Engine) { // SOUTH
            isValid = isEngineValid(y,x);
        }

        if (tile instanceof Cannon) { // NORTH
            isValid = isCannonValid(y,x);
        }

        // Controlla tutti e 4 i connettori
        for (int dir = 0; dir < 4 && isValid; dir++) {
            int adjX = x + DIR_X[dir];
            int adjY = y + DIR_Y[dir];

            // Caso 2: Connessione con tile adiacente
            if (inBounds(adjX, adjY)) {
                ComponentTile adjTile = components[adjY][adjX];

                if (adjTile != null) {
                    ConnectorType current = tile.getConnectors()[dir];
                    ConnectorType adjacent = adjTile.getConnectors()[(dir + 2) % 4];

                    if (!isConnectionValid(current, adjacent)){
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }


    /**
     * Removes a component from the specified coordinates and processes its adjacent components.
     *
     * This method checks if there is a valid component at the given coordinates,
     * removes it, and traverses its neighboring components based on specific conditions.
     *
     * @param x the x-coordinate of the component to be removed
     * @param y the y-coordinate of the component to be removed
     * @return the number of parts found and processed through the removal operation
     * @throws SpaceShipPlanceException if there is no tile at the specified coordinates
     */
    public int remove(int x, int y) {
        initShownComponents();
        initVisited();
        if (components[y][x] == null)
            throw new SpaceShipPlanceException("No tile in the index you provided, please retry");

        addReserveSpot(components[y][x]);
        components[y][x] = null;

        int partsFound = 0;

        // Parte da tutte e 4 le direzioni con iterazioni diverse
        for (int iteration = 0; iteration < 4; iteration++) {
            int nx = x + DIR_X[iteration];
            int ny = y + DIR_Y[iteration];

            if (inBounds(nx, ny) && !edgeCases(ny, nx) && components[ny][nx] != null) {
                partsFound += dfsRemove(nx, ny, iteration);
            }
        }

        return partsFound;
    }

    /**
     * Executes a depth-first search (DFS) to remove and count components from a grid
     * starting at a specified position. Marks visited components and tracks their
     * iteration in an auxiliary matrix.
     *
     * @param x the x-coordinate of the starting position
     * @param y the y-coordinate of the starting position
     * @param iteration the current iteration identifier to mark the processed components
     * @return the count of components removed starting from the given position
     */
    private int dfsRemove(int x, int y, int iteration) {
        if (!inBounds(x, y) || visited[y][x] || components[y][x] == null) {
            return 0;
        }

        visited[y][x] = true;
        shownComponents[y][x] = iteration;
        int count = 1;

        ComponentTile tile = components[y][x];

        for (int dir = 0; dir < 4; dir++) {
            // Non seguire i connettori smooth
            if (tile.getConnectors()[dir] == ConnectorType.SMOOTH) continue;

            int nx = x + DIR_X[dir];
            int ny = y + DIR_Y[dir];
            dfsRemove(nx, ny, iteration);
        }

        return count;
    }

    /**
     * Selects and retains the components that match the specified iteration value.
     * All other components are set to null.
     *
     * @param iteration the iteration value used to filter and select components
     */
    public void selectPart(int iteration) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (shownComponents[i][j] != iteration) {
                    components[i][j] = null;
                }
            }
        }
    }


    /**
     * Counts the number of specific types of figures (Astronauts, Brown Aliens, and Purple Aliens)
     * present in a collection of cabins and updates the corresponding counters.
     *
     * This method iterates through all cabins, retrieves their figures, and determines the type
     * of each figure. The method distinguishes between Astronauts and Aliens. For Aliens, it further
     * checks their color to classify them as either Brown Aliens or Purple Aliens.
     *
     * The counts of the identified figures are printed to the console in the following format:
     * "Astronauts: <number> Brown aliens: <number> Purple aliens: <number>"
     *
     * Modifies:
     * - nAstronauts: Counter for the number of Astronauts found.
     * - nBrownAliens: Counter for the number of Brown Aliens found.
     * - nPurpleAliens: Counter for the number of Purple Aliens found.
     */
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

    /**
     * Checks whether the storage (cargo holds) is not empty.
     *
     * @return true if the cargo holds are not empty, false otherwise
     */
    public boolean checkStorage() {
        return !cargoHolds.isEmpty();
    }

    /**
     * Handles the swapping of goods between two cargo containers.
     * Validates the constraints of cargo and goods indexes and ensures special rules for goods are maintained.
     *
     * @param cargoIndex1 The index of the first cargo container involved in the swap.
     * @param cargoIndex2 The index of the second cargo container involved in the swap.
     * @param goodIndex1 The index of the goods in the first cargo container to be swapped.
     * @param goodIndex2 The index of the goods in the second cargo container to be swapped.
     * @throws CargoManagementException if any index is out of bounds or the swap violates special constraints.
     */
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

    /**
     * Handles the removal of a specific good from a specified cargo container.
     *
     * @param cargoIndex the index of the cargo container in the list
     * @param goodIndex the index of the good to be removed within the specified cargo container
     * @throws CargoManagementException if the cargoIndex is out of bounds or if the goodIndex is out of bounds
     */
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

    /**
     * Handles the addition of a goods block from card rewards to a specific cargo's goods container.
     * Performs checks to ensure the indices are within bounds and validates the addition based
     * on the type of goods and existing constraints in the cargo.
     *
     * @param cardReward Array of goods blocks available as rewards.
     * @param cargoIndex Index of the cargo container in the goods container list.
     * @param goodIndex Index within the goods array in the specified cargo container where the
     *                  reward goods block is to be added.
     * @param rewardIndex Index of the goods block in the card rewards array to be added.
     * @throws CargoManagementException if any of the following conditions occur:
     *                                  - The specified cargo index is out of bounds.
     *                                  - The specified goods index is out of bounds.
     *                                  - The reward goods index is out of bounds.
     *                                  - A good already exists at the specified location in the cargo.
     *                                  - The addition violates special goods constraints.
     */
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

    /**
     * Swaps goods between two goods containers at specified indices.
     *
     * @param cargo1 the first goods container involved in the swap
     * @param cargo2 the second goods container involved in the swap
     * @param j1 the index of the goods block in the first goods container to be swapped
     * @param j2 the index of the goods block in the second goods container to be swapped
     */
    public void swapGoods(GoodsContainer cargo1, GoodsContainer cargo2, int j1, int j2) {

        GoodsBlock[] goods1 = cargo1.getGoods();
        GoodsBlock[] goods2 = cargo2.getGoods();

        GoodsBlock temp = goods1[j1];
        goods1[j1] = goods2[j2];
        goods2[j2] = temp;

    }

    /**
     * Checks the conditions for special goods based on the provided GoodsContainer
     * and GoodsBlock objects.
     *
     * @param cargo1 the first GoodsContainer to be checked
     * @param cargo2 the second GoodsContainer to be checked
     * @param good1 the first GoodsBlock to be checked, may be null
     * @param good2 the second GoodsBlock to be checked, may be null
     * @return true if conditions for special goods are satisfied, otherwise false
     */
    private boolean checkSpecialGoods(GoodsContainer cargo1, GoodsContainer cargo2, GoodsBlock good1, GoodsBlock good2) {
        if ((((good1 != null) && good1.getType() == RED && !cargo2.isSpecial())) || (((good2 != null) && good2.getType() == RED && !cargo1.isSpecial())))
            return false;
        return true;

    }

    /**
     * Checks if the specified goods block is allowed in the given cargo container based on its type
     * and the special status of the cargo container.
     *
     * @param cargo the container that holds goods, which may have a special status
     * @param good the goods block to be checked for compatibility with the cargo container
     * @return true if the goods block is allowed in the cargo container, false otherwise
     */
    private boolean checkSpecialGoods(GoodsContainer cargo, GoodsBlock good) {
        if ((good.getType() == RED && !cargo.isSpecial()))
            return false;
        return true;
    }

    /**
     * Removes a good from the specified goods container at the specified index.
     *
     * @param cargo1 the goods container from which the good will be removed
     * @param j1 the index of the good to be removed within the goods container
     */
    private void removeGoods(GoodsContainer cargo1, int j1) {
        cargo1.getGoods()[j1] = null;
    }

    /**
     * Adds a goods item from the given card reward array to the specified position in the goods container.
     * Updates the card reward array by setting the used item to null.
     *
     * @param cargo1     The container where the goods will be added.
     * @param cardReward The array of goods blocks serving as the source for the goods.
     * @param j1         The index in the goods container where the goods will be added.
     * @param k          The index in the card reward array that specifies which goods block to add.
     */
    private void addGoods(GoodsContainer cargo1, GoodsBlock[] cardReward, int j1, int k) {
        cargo1.getGoods()[j1] = cardReward[k];
        cardReward[k] = null;
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

    public ArrayList<Cabin> getInterconnectedCabins() {
        return interconnectedCabins;
    }

    public ArrayList<ShieldGenerator> getShields() {
        return shieldGenerators;
    }

    /**
     * Counts the total number of active batteries across all power centers.
     *
     * This method iterates through a collection of power centers and checks
     * each battery's status within them. If a battery is active, it increments
     * the count of active batteries. The method uses the `getBatteries` method
     * of each power center to check the statuses of individual batteries.
     *
     * The results are printed to standard output for each power center
     * encountered during the iteration.
     */
    public void countBatteries(){
        nBatteries=0;
        for(PowerCenter powerCenter : powerCenters){
            System.out.println(powerCenter);
            for(boolean b: powerCenter.getBatteries())
                if(b)
                    nBatteries++;
        }
    }

    /**
     * Counts the total number of exposed connectors in a grid of components.
     * A connector is considered exposed if it does not belong to the types SMOOTH, CANNON, or ENGINE
     * and either has no adjacent tile in the specified direction or is out of bounds.
     *
     * @return the total number of exposed connectors in the grid.
     */
    public int countExposedConnectors() {

        exposedConnectors = 0;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 6; x++) {
                if (inBounds(x, y) && components[y][x] != null) {
                    ComponentTile tile = components[y][x];

                    ConnectorType[] connectors = tile.getConnectors();
                        for (int i = 0; i < connectors.length; i++) {
                            if (connectors[i] != ConnectorType.SMOOTH && connectors[i] != ConnectorType.CANNON && connectors[i] != ConnectorType.ENGINE) {
                                int x2 = x + DIR_X[i];
                                int y2 = y + DIR_Y[i];
                                if(inBounds(x2, y2)) {
                                    ComponentTile tile2 = components[y2][x2];
                                    if (tile2 == null)
                                        exposedConnectors++;
                                } else {
                                    exposedConnectors++;
                                }
                            }
                        }
                }
            }
        }

        return exposedConnectors;
    }

    /**
     * Checks the protection level of a given position in a specific direction.
     *
     * The method determines if a position in a given direction is protected
     * by a cannon or a double cannon. The check proceeds in the specified direction
     * until either a protection object is found or the bounds of the grid are exceeded.
     *
     * @param direction the direction to check for protection (e.g., NORTH, EAST, SOUTH, WEST)
     * @param position the starting position in the grid from which the check begins
     * @return an integer representing the protection level:
     *         -1 if no protection object is present within bounds,
     *         0 if no cannon is detected at the found position,
     *         1 if a single cannon is found at the position,
     *         2 if a double cannon is found at the position
     */
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

        ComponentTile hit = null;

        if(inBounds(x,y))
            hit = components[y][x];


        for (int i = 0; i < max_lenght && hit == null; i++) {
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
            if(inBounds(x,y)) {
                hit = components[y][x];
                System.out.println("checkProtection: hit " + hit);
            }
        }

        if (hit == null) {
            return -1; // se non veniamo colpiti
        }
        System.out.println("checkProtection: hit " + hit);

        if(hit instanceof Cannon && cannons.contains(hit)){
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

    /**
     * Determines the direction of the cannon connector within the given cannon object.
     *
     * @param cannon the cannon object whose connector direction is to be determined
     * @return the index of the cannon connector within the cannon's connectors array
     * @throws IllegalStateException if no cannon connector is found in the cannon's connectors
     */
    public int getCannonDirection(Cannon cannon) {
        ConnectorType[] cannonConnectors = cannon.getConnectors();
        for (int i = 0; i < cannonConnectors.length; i++) {
            if (cannonConnectors[i] == ConnectorType.CANNON)
                return i;
        }
        throw new IllegalStateException("Not a cannon connector in a cannon tile");
    }


    public ArrayList<Cabin> getConnectedCabins() {
        ArrayList<Cabin> connectedCabins = new ArrayList<>();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 6; x++) {
                ComponentTile tile = components[y][x];
                if (tile instanceof Cabin) {

                    ConnectorType[] connectors = tile.getConnectors();
                    for (int i = 0; i < connectors.length; i++) {
                        // questo approccio funziona perché do per scontato che la nave sia ben connessa a sto punto
                        // Se da quel lato c'è un connettore singolo/doppio/universale vuol dire che è collegato a un'altra casella. Controllo che sia anch'essa una cabina
                        if (connectors[i] != ConnectorType.SMOOTH && connectors[i] != ConnectorType.CANNON && connectors[i] != ConnectorType.ENGINE) {
                            int x2 = x + DIR_X[i];
                            int y2 = y + DIR_Y[i];
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

    public int getCrew(){return nAstronauts + nBrownAliens + nPurpleAliens; }

    public int getnAstronauts() {
        return nAstronauts;
    }



    /**
     * Places a tile component at the specified coordinates on the spaceship grid.
     *
     * @param tile the tile component to be placed on the grid.
     * @param x the x-coordinate where the tile will be placed (0 to 6 inclusive).
     * @param y the y-coordinate where the tile will be placed (0 to 4 inclusive).
     * @throws SpaceShipPlanceException if the specified coordinates are out of bounds,
     *                                  if the spot is already occupied,
     *                                  or if placement fails due to an edge case.
     */
    public void placeTileComponents(ComponentTile tile, int x, int y) throws SpaceShipPlanceException {
        if (x < 0 || x > 6 || y < 0 || y > 4 || edgeCases(y, x))
            throw new SpaceShipPlanceException("Outbound index");
        else if (components[y][x] != null) {
            throw new SpaceShipPlanceException("Already busy spot");
        }
        components[y][x] = tile;
        tile.setWellConnected(checkNewTile(x, y));
    }


    /**
     * Checks if there is an exposed connector in the specified direction starting at the given position.
     * The method iterates through the components in the specified direction to check for a connector that
     * matches disallowed connector types (SINGLE, DOUBLE, or UNIVERSAL).
     *
     * @param direction the direction to check for the exposed connector (e.g., NORTH, EAST, SOUTH, or WEST)
     * @param position the starting position from which to begin the search
     * @return true if an exposed connector of a disallowed type is found; false otherwise
     */
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
        ComponentTile hit = null;

        if(inBounds(x,y))
            hit = components[y][x];

        for (int i = 0; i < max_lenght && hit == null; i++) {
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
            if(inBounds(x,y))
                hit = components[y][x];
        }

        ArrayList<ConnectorType> disallowedConnectors = new ArrayList<>(Arrays.asList(ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.UNIVERSAL));

        if ((hit != null) && (disallowedConnectors.contains(hit.getConnectors()[direction.ordinal()]))) {
            return true;
        }

        return false;
    }

    /**
     * Retrieves a 2D array of TileData objects representing the tiles
     * from the current state of the components and reserve spots.
     *
     * The array contains:
     * - Tiles from the component grid.
     * - Tiles from the reserve spots, if available, added at specific positions.
     *
     * If a tile is null, a default TileData object is created with an ID of -1 and a rotation of 0.
     *
     * @return A 2D array of TileData objects representing the tiles and their properties.
     */
    public TileData[][] getTileIds() {
        TileData[][] result = new TileData[5][7];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                ComponentTile tile = components[i][j];
                int sc = shownComponents[i][j];
                result[i][j] = (tile != null) ? new TileData(tile.getId(), tile.getRotation(), tile.isWellConnected(), sc) : new TileData(-1, 0);
            }
        }

        for (int i=0; (i < this.reserveSpot.size()) && (i < 2); i++) {
            ComponentTile c = this.reserveSpot.get(i);
            result[0][5+i] = (c != null) ? new TileData(c.getId(), c.getRotation()) : new TileData(-1, 0);
        }

        return result;
    }

    /**
     * Converts the components array into a readable string representation,
     * where each row of the array is separated by a newline character and
     * elements in each row are separated by commas.
     *
     * @return A string representation of the components array.
     */
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

    /**
     * Constructs a string representation of a grid of tiles, formatted row by row
     * with specific tile characteristics represented visually. The grid structure
     * includes numeric row and column headers for guidance and incorporates
     * blank spaces for edge cases where no tile is present.
     *
     * @return A formatted string representation of the tile grid.
     */
    public String tileGridToString() {
        int rows = this.components.length;
        int cols = this.components[0].length;
        StringBuilder result = new StringBuilder();

        result.append("\n   ");

        for (int tileCol = 0; tileCol < cols; tileCol++) {
            result.append(tileCol).append("  ");
        }
        result.append("\n");

        for (int tileRow = 0; tileRow < rows; tileRow++) {
            for (int line = 0; line < 3; line++) {
                if (line == 1) {
                    result.append(tileRow).append(" ");
                } else {
                    result.append("  ");
                }

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
                result.append('\n');
            }
        }

        return result.toString();
    }



    /**
     * Generates a 2D character array representation of a tile, illustrating its structure,
     * connectors, center symbol, and any applicable shields.
     *
     * @param tile The ComponentTile object representing the tile to be rendered.
     *             If null, a default empty tile representation with no connections or shields is returned.
     * @return A 2D character array depicting the tile, where each character represents a specific
     *         part of the tile (e.g., borders, connectors, center symbol, or shields).
     */
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

    /**
     * Converts the internal grid of tiles into a formatted string representation with adjustments
     * for grid layouts and specific tile edge cases.
     * The resulting string includes grid indices and tile components formatted in a multi-line structure.
     *
     * @return A string representation of the tile grid with adjustments, including grid indices,
     *         formatted rows and columns, and handling for applicable edge cases.
     */
    public String tileGridToStringAdjustments() {
        int rows = this.components.length;
        int cols = this.components[0].length;
        StringBuilder result = new StringBuilder();

        result.append("\n   ");


        for (int tileCol = 0; tileCol < cols; tileCol++) {
            result.append(tileCol).append("  ");
        }
        result.append("\n");

        for (int tileRow = 0; tileRow < rows; tileRow++) {
            for (int line = 0; line < 3; line++) {
                if (line == 1) {
                    result.append(tileRow).append(" ");
                } else {
                    result.append("  ");
                }

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
                result.append('\n');
            }
        }

        return result.toString();
    }

    /**
     * Adjusts the visual representation of a tile based on its properties and characteristics.
     *
     * @param tile the tile component to be adjusted; if null, a default grid representation is returned.
     * @return a 2D character array representing the adjusted visual appearance of the tile. The array shows
     *         the tile borders, connectors, central symbol, and any additional features such as shield representation
     *         for specific types of tiles.
     */
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

    /**
     * Converts the given ConnectorType to its corresponding character symbol.
     *
     * @param ct the ConnectorType to convert
     * @return the character representation of the specified ConnectorType,
     *         or '?' if the type is not recognized
     */
    public char connectorToChar(ConnectorType ct) {
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

    /**
     * Converts a given ComponentTile object into its corresponding string representation.
     *
     * @param tile the ComponentTile object to be converted to a string. Can be null.
     * @return a string representation of the given ComponentTile. If the tile does not match any known type,
     *         the method returns "not Catched in tiletoString". Returns null if the input tile is null.
     */
    public String tiletoString(ComponentTile tile) {
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

    /**
     * Converts the reserveSpot tiles into a string representation, formatting them in a stylized 3×3 grid layout.
     * Each tile in the reserveSpot is processed to generate its character representation, and the formatted result
     * is concatenated line by line to form the final string output.
     *
     * @return A string representation of the reserveSpot tiles in a structured grid format.
     */
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

    /**
     * Checks if the interconnected cabins list is empty.
     *
     * This method iterates through the interconnectedCabins collection,
     * printing each cabin for debugging purposes, and determines
     * whether the list is empty.
     *
     * @return true if the interconnectedCabins list is empty, false otherwise
     */
    public boolean checkInterconnectedCabinsEmpty(){

        System.out.println("sono entrato in checkInterconnectedCabinsEmpty");

        for(Cabin cabin: interconnectedCabins)
            System.out.println(cabin);

        return interconnectedCabins.isEmpty();
    }

    /**
     * Removes the specified cabin from the list of interconnected cabins.
     *
     * @param cab the Cabin object to be removed from the interconnectedCabins list
     */
    public void removeInterconnectedCabin(Cabin cab){
         interconnectedCabins.remove(cab);
    }


    /**
     * Converts a grid of tiles represented by shownComponents into a string,
     * displaying the structured layout in multiple rows and columns.
     * Each tile in the grid is represented by a 3x3 set of characters.
     * Blank spaces are added for edge cases where a tile is not to be displayed.
     *
     * @return A formatted string representation of the tile grid,
     *         with each tile rendered in multiple lines, separated by new lines.
     */
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


    /**
     * Creates a 2D character array representing a tile with a crafted part.
     *
     * @param tile an integer representing the tile value to be added to the center of the tile.
     *             If the value is -1, no value is added to the tile center.
     * @return a 2D character array representing the crafted tile. The tile includes a border and optionally
     *         a character at its center based on the input value.
     */
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

    /**
     * Converts the grid of components into a string representation for a specific tile.
     * The method generates a formatted string where the given tile is highlighted
     * within a grid representation of the components.
     *
     * @param tileToShow The specific tile to be represented in the string format.
     * @return A string representation of the grid with the specified tile highlighted,
     *         maintaining proper alignment and formatting.
     */
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

    /**
     * Generates a 2D character representation of a given tile and overlays it with specific details if it matches
     * the specified tile type to display. The representation includes borders, an ID or symbol, and connectors.
     *
     * @param tile the tile for which the character representation is to be created. If null, a default border is returned.
     * @param tileToShow the tile type to compare with the given tile to decide the representation details.
     * @return a 5x5 2D character array representing the tile, including borders, connectors, and additional details.
     */
    public char[][] tileCrafterbyTile(ComponentTile tile, ComponentTile tileToShow) {
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

    /**
     * Removes a GoodsBlock from a specified CargoHolds within the player's cargo if certain conditions are met.
     * The removal is based on the value of the GoodsBlock at the specified indices compared to the value of
     * the most valuable GoodsBlock owned by the player.
     *
     * @param cargoIndex The index of the CargoHolds container in the player's cargo.
     * @param goodIndex The index of the GoodsBlock within the specified CargoHolds.
     * @return {@code true} if the GoodsBlock is successfully removed; {@code false} if the removal conditions are not met.
     * @throws CargoManagementException if the CargoHolds or GoodsBlock index is out of bounds, or if attempting to remove a null GoodsBlock.
     */
    public boolean removeMVGood(int cargoIndex, int goodIndex) {
        ArrayList<CargoHolds> playerCargo = getCargoHolds();
        ArrayList<GoodsBlock> playerGoods = new ArrayList<>();
        for (CargoHolds cargo : playerCargo) {
            GoodsBlock[] goods = cargo.getGoods();
            Collections.addAll(playerGoods, goods);
        }

        playerGoods = playerGoods.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(GoodsBlock::getValue).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        if (cargoIndex >= 0 && cargoIndex < goodsContainers.size()) {
            GoodsContainer cargo1 = goodsContainers.get(cargoIndex);
            if (goodIndex >= 0 && goodIndex < cargo1.getGoods().length) {
                GoodsBlock goodsBlock = cargo1.getGoods()[goodIndex];
                if(goodsBlock != null){
                    if(cargo1.getGoods()[goodIndex].getValue() >= playerGoods.getFirst().getValue() ){
                        removeGoods(cargo1, goodIndex);
                        return true;
                    }else
                        return false;
                }else
                    throw new CargoManagementException("you cant remove an empty spot");
            } else
                throw new CargoManagementException("goods index is outbound");
        } else
            throw new CargoManagementException("cargo index is outbound");
    }

    /**
     * Counts the total number of goods across all player's cargo holds.
     *
     * @return the total number of goods present in all cargo holds
     */
    public int countGoods(){
        int i = 0;
        ArrayList<CargoHolds> playerCargo = getCargoHolds();
        for (CargoHolds cargo : playerCargo) {
            GoodsBlock[] goods = cargo.getGoods();
            for(GoodsBlock good : goods){
                if(good != null)
                    i++;
            }
        }
        return i;
    }
}
