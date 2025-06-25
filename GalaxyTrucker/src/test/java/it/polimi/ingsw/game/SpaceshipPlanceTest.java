package it.polimi.ingsw.game;
import static org.junit.Assert.*;

import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.Cabin;
import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.componentTiles.ConnectorType;
import it.polimi.ingsw.model.componentTiles.Engine;
import it.polimi.ingsw.model.game.CargoManagementException;
import it.polimi.ingsw.model.game.ColorType;
import it.polimi.ingsw.model.game.SpaceshipPlance;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.resources.GoodsContainer;
import it.polimi.ingsw.model.resources.TileSymbols;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.*;




public class SpaceshipPlanceTest {
    private SpaceshipPlance spaceship;

    @Before
    public void setUp() {
        spaceship = new SpaceshipPlance();

        ComponentTile[][] grid = spaceship.getComponents();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                grid[y][x] = null;
            }
        }
    }

    @Test
    public void testInitVisitedSetsAllToFalse() {
        //SpaceshipPlance spaceship = new SpaceshipPlance();
        boolean[][] visited = spaceship.getVisited();

        // Prepopola con true per sicurezza
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[0].length; j++) {
                visited[i][j] = true;
            }
        }

        // Inizializza
        spaceship.initVisited();

        // Verifica che tutto sia false
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[0].length; j++) {
                assertFalse("visited[" + i + "][" + j + "] should be false", visited[i][j]);
            }
        }
    }

    @Test
    public void testInBounds_ValidCoordinates_ReturnsTrue() {
        //SpaceshipPlance spaceship = new SpaceshipPlance();
        // Coordinate valide e non edge case
        assertTrue(spaceship.inBounds(2, 2)); // centro
        assertTrue(spaceship.inBounds(1, 1));
        assertTrue(spaceship.inBounds(5, 3));
    }

    @Test
    public void testInBounds_OutOfBoundsCoordinates_ReturnsFalse() {
        //SpaceshipPlance spaceship = new SpaceshipPlance();
        // Fuori dai limiti
        assertFalse(spaceship.inBounds(-1, 2));
        assertFalse(spaceship.inBounds(2, -1));
        assertFalse(spaceship.inBounds(7, 2));
        assertFalse(spaceship.inBounds(3, 5));
    }

    @Test
    public void testInBounds_EdgeCaseCoordinates_ReturnsFalse() {
        //SpaceshipPlance spaceship = new SpaceshipPlance();

        // edgeCases da y == 0
        assertFalse(spaceship.inBounds(0, 0));
        assertFalse(spaceship.inBounds(1, 0));
        assertFalse(spaceship.inBounds(3, 0));
        assertFalse(spaceship.inBounds(5, 0));
        assertFalse(spaceship.inBounds(6, 0));

        // edgeCases da y == 1
        assertFalse(spaceship.inBounds(0, 1));
        assertFalse(spaceship.inBounds(6, 1));

        // edgeCases da y == 4
        assertFalse(spaceship.inBounds(3, 4));
    }

    @Test
    public void testInBounds_BorderButValidCoordinates_ReturnsTrue() {
        //SpaceshipPlance spaceship = new SpaceshipPlance();

        // Angoli o bordi validi
        assertTrue(spaceship.inBounds(2, 0)); // y=0 ma non edge
        assertTrue(spaceship.inBounds(1, 2));
        assertTrue(spaceship.inBounds(6, 2));
        assertTrue(spaceship.inBounds(0, 2));
        assertTrue(spaceship.inBounds(2, 4)); // y=4 ma non edge
    }

    @Test
    public void testUpdateLists_AddsCorrectComponents() {
        //SpaceshipPlance spaceship = new SpaceshipPlance();
        // Prepara alcuni componenti
        Cannon cannon = new Cannon(new ConnectorType[]{ConnectorType.CANNON, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH},  101);
        Engine engine = new DoubleEngine(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.ENGINE, ConnectorType.SMOOTH}, 102);
        Cabin cabin = new Cabin(new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL}, true, 103);
        CargoHolds cargoHold = new CargoHolds(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH}, 104, false, 2);
        ShieldGenerator shieldGen = new ShieldGenerator(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH}, new boolean[]{true, true, false, false}, 105);
        PowerCenter powerCenter = new PowerCenter(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH}, 2, 106);

        // Posiziona i componenti nella griglia
        spaceship.getComponents()[2][2] = cannon;
        spaceship.getComponents()[2][3] = engine;
        spaceship.getComponents()[2][4] = cabin;
        spaceship.getComponents()[1][3] = cargoHold;
        spaceship.getComponents()[3][3] = shieldGen;
        spaceship.getComponents()[3][4] = powerCenter;

        // Chiama updateLists
        spaceship.updateLists();

        // Verifica
        assertEquals(1, spaceship.getCannons().size());
        assertEquals(1, spaceship.getEngines().size());
        assertEquals(1, spaceship.getCabins().size());
        assertEquals(1, spaceship.getCargoHolds().size());
        assertEquals(1, spaceship.getShields().size());
        assertEquals(1, spaceship.getPowerCenters().size());

        assertTrue(spaceship.getCannons().contains(cannon));
        assertTrue(spaceship.getEngines().contains(engine));
        assertTrue(spaceship.getCabins().contains(cabin));
        assertTrue(spaceship.getCargoHolds().contains(cargoHold));
        assertTrue(spaceship.getShields().contains(shieldGen));
        assertTrue(spaceship.getPowerCenters().contains(powerCenter));
    }

    @Test
    public void testIsEngineValid_CorrectConnectorAndEmptyBehind() {
        // Engine con connettore corretto in basso (indice 2)
        ComponentTile engine = new DoubleEngine(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.ENGINE, ConnectorType.SMOOTH}, 100);

        spaceship.getComponents()[2][3] = engine;
        // Nessun componente dietro (y+1)
        spaceship.getComponents()[3][3] = null;

        assertTrue(spaceship.isEngineValid(2, 3));
    }

    @Test
    public void testIsEngineValid_CorrectConnectorButBlockedBehind() {
        ComponentTile engine = new DoubleEngine(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.ENGINE, ConnectorType.SMOOTH}, 101);

        ComponentTile blockingTile = new DoubleEngine(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH}, 102);

        spaceship.getComponents()[2][3] = engine;
        spaceship.getComponents()[3][3] = blockingTile; // spazio dietro occupato

        assertFalse(spaceship.isEngineValid(2, 3));
    }

    @Test
    public void testIsEngineValid_WrongConnector() {
        ComponentTile engine = new DoubleEngine(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH}, 103); // NO ENGINES

        spaceship.getComponents()[2][3] = engine;
        spaceship.getComponents()[3][3] = null;

        assertFalse(spaceship.isEngineValid(2, 3));
    }

    @Test
    public void testIsEngineValid_BackOutOfBounds() {
        ComponentTile engine = new DoubleEngine(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.ENGINE, ConnectorType.SMOOTH}, 104);

        // posizioniamo il motore sull'ultima riga (4)
        spaceship.getComponents()[4][3] = engine;

        // y+1 sarebbe 5 → fuori dai limiti → valido
        assertTrue(spaceship.isEngineValid(4, 3));
    }

    @Test
    public void testIsCannonValid_FacingVoid() {
        // Crea un cannone che spara verso l'alto (nord)
        ConnectorType[] connectors = {
                ConnectorType.CANNON,   // nord
                ConnectorType.SMOOTH,   // est
                ConnectorType.SMOOTH,   // sud
                ConnectorType.SMOOTH    // ovest
        };
        Cannon cannon = new Cannon(connectors, 1);

        // Posiziona il cannone nella seconda riga, prima colonna (posizione [1][0])
        // la casella [0][0] è un edge case -> vuota, quindi dovrebbe essere valido
        spaceship.getComponents()[1][0] = cannon;

        assertTrue(spaceship.isCannonValid(1, 0));
    }

    @Test
    public void testIsCannonValid_Blocked() {
        // Crea un cannone che spara verso il basso (sud)
        ConnectorType[] connectors = {
                ConnectorType.SMOOTH,   // nord
                ConnectorType.SMOOTH,   // est
                ConnectorType.CANNON,   // sud
                ConnectorType.SMOOTH    // ovest
        };
        Cannon cannon = new Cannon(connectors, 2);

        // Posiziona il cannone a [2][3], e un componente "ostacolo" davanti a [3][3]
        spaceship.getComponents()[2][3] = cannon;
        spaceship.getComponents()[3][3] = new Engine(new ConnectorType[] {
                ConnectorType.SMOOTH, ConnectorType.SMOOTH,
                ConnectorType.SMOOTH, ConnectorType.SMOOTH
        }, 3);

        assertFalse(spaceship.isCannonValid(2, 3));
    }

    @Test
    public void testSmoothToSmooth() {
        assertTrue(spaceship.isConnectionValid(ConnectorType.SMOOTH, ConnectorType.SMOOTH));
    }

    @Test
    public void testSmoothToUniversal() {
        assertFalse(spaceship.isConnectionValid(ConnectorType.SMOOTH, ConnectorType.UNIVERSAL));
    }

    @Test
    public void testUniversalToUniversal() {
        assertTrue(spaceship.isConnectionValid(ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL));
    }

    @Test
    public void testUniversalToDouble() {
        assertTrue(spaceship.isConnectionValid(ConnectorType.UNIVERSAL, ConnectorType.DOUBLE));
    }

    @Test
    public void testDoubleToDouble() {
        assertTrue(spaceship.isConnectionValid(ConnectorType.DOUBLE, ConnectorType.DOUBLE));
    }

    @Test
    public void testCannonToUniversal() {
        assertFalse(spaceship.isConnectionValid(ConnectorType.CANNON, ConnectorType.UNIVERSAL));
    }

    @Test
    public void testEngineToDouble() {
        assertFalse(spaceship.isConnectionValid(ConnectorType.ENGINE, ConnectorType.DOUBLE));
    }

    @Test
    public void testUniversalToUniversalSymmetric() {
        assertTrue(spaceship.isConnectionValid(ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL));
        assertTrue(spaceship.isConnectionValid(ConnectorType.DOUBLE, ConnectorType.UNIVERSAL));
    }

    @Test
    public void testSmoothToCannon() {
        assertFalse(spaceship.isConnectionValid(ConnectorType.SMOOTH, ConnectorType.CANNON));
    }

    @Test
    public void testIsTileValid_ValidConnection() {
        // Cabin a [2][3] con UNIVERSAL a est
        ComponentTile cabin = new Cabin(new ConnectorType[]{
                ConnectorType.SMOOTH, ConnectorType.UNIVERSAL,
                ConnectorType.SMOOTH, ConnectorType.SMOOTH
        }, true, 1);

        // Cargo a [2][4] con DOUBLE a ovest
        ComponentTile cargo = new CargoHolds(new ConnectorType[]{
                ConnectorType.SMOOTH, ConnectorType.SMOOTH,
                ConnectorType.SMOOTH, ConnectorType.DOUBLE
        }, 2, false, 2);

        spaceship.getComponents()[2][3] = cabin;
        spaceship.getComponents()[2][4] = cargo;

        assertTrue(spaceship.isTileValid(3, 2));
    }

    @Test
    public void testIsTileValid_InvalidConnection() {
        // Cabin con SINGLE a est
        ComponentTile cabin = new Cabin(new ConnectorType[]{
                ConnectorType.SMOOTH, ConnectorType.SINGLE,
                ConnectorType.SMOOTH, ConnectorType.SMOOTH
        }, true, 3);

        // Cargo con DOUBLE a ovest
        ComponentTile cargo = new CargoHolds(new ConnectorType[]{
                ConnectorType.SMOOTH, ConnectorType.SMOOTH,
                ConnectorType.SMOOTH, ConnectorType.DOUBLE
        }, 4, false, 3);

        spaceship.getComponents()[2][3] = cabin;
        spaceship.getComponents()[2][4] = cargo;

        // Connessione CANNON ↔ ENGINE non valida
        assertFalse(spaceship.isTileValid(3, 2));
    }


    @Test
    public void testEdgeCases_ValidTrueCases() {
        // y == 0
        assertTrue(spaceship.edgeCases(0, 0));
        assertTrue(spaceship.edgeCases(0, 1));
        assertTrue(spaceship.edgeCases(0, 3));
        assertTrue(spaceship.edgeCases(0, 5));
        assertTrue(spaceship.edgeCases(0, 6));

        // y == 1
        assertTrue(spaceship.edgeCases(1, 0));
        assertTrue(spaceship.edgeCases(1, 6));

        // y == 4
        assertTrue(spaceship.edgeCases(4, 3));
    }

    @Test
    public void testEdgeCases_InvalidFalseCases() {
        // Altri casi, devono essere false
        assertFalse(spaceship.edgeCases(0, 2));
        assertFalse(spaceship.edgeCases(0, 4));
        assertFalse(spaceship.edgeCases(1, 1));
        assertFalse(spaceship.edgeCases(2, 3));
        assertFalse(spaceship.edgeCases(3, 6));
        assertFalse(spaceship.edgeCases(4, 2));
        assertFalse(spaceship.edgeCases(5, 3)); // y > 4, non previsto
    }
    @Test
    public void testInitShownComponents_AllValuesAreMinusOne() {
        spaceship.initShownComponents();
        int[][] shown = spaceship.getShownComponents();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                assertEquals("shownComponents[" + i + "][" + j + "] dovrebbe essere -1", -1, shown[i][j]);
            }
        }
    }
    @Test
    public void testCheckCorrectness_withOnlyCentralCabin_returnsTrue() {
        boolean result = spaceship.checkCorrectness();
        assertTrue("Solo la cabina centrale connessa, deve essere valida", result);
    }

    @Test
    public void testCheckCorrectness_withIsolatedTile_isRemoved() {
        // Creo una tile scollegata connessa a nulla
        ConnectorType[] noConnections = {
                ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH,

        };
        ComponentTile isolated = new Cabin(noConnections, false, 0);

        // Posiziono una tile in alto a sinistra che non è collegata alla cabina centrale
        spaceship.setComponent(0, 0, isolated);

        // Deve rimuoverla perché non è connessa
        boolean result = spaceship.checkCorrectness();

        assertTrue("La tile isolata dovrebbe essere rimossa, struttura valida", result);
        assertNull("Tile scollegata deve essere rimossa", spaceship.getComponent(0, 0));
    }

    @Test
    public void testCheckCorrectness_noCentralCabin_returnsFalse() {
        // Rimuovo la cabina centrale
        spaceship.setComponent(2, 3, null);

        boolean result = spaceship.checkCorrectness();
        assertFalse("Senza cabina centrale e connessioni, deve essere non valida", result);
    }
    @Test
    public void testDfsExploration_visitsConnectedTiles() {
        // Reset dello stato visitato
        spaceship.initVisited();

        // Tile con tutti connettori UNIVERSAL (connessi da tutti i lati)
        ConnectorType[] connectors = {
                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL
        };

        ComponentTile tileA = new Engine(connectors, 1);
        ComponentTile tileB = new Engine(connectors, 1);
        // Inserisco due tile connesse orizzontalmente
        spaceship.setComponent(2, 2, tileA); // punto di partenza
        spaceship.setComponent(2, 3, tileB); // a destra di tileA

        // Avvio DFS dalla tile A
        spaceship.dfsExploration(2, 2);

        // Verifico che entrambe siano state visitate
        boolean[][] visited = spaceship.getVisited();

        assertTrue("Tile A deve essere visitata", visited[2][2]);
        assertTrue("Tile B deve essere visitata", visited[2][3]);
    }
    @Test
    public void testRemoveUnvisitedTiles_removesCorrectly() {
        // Setup: connectors universali per semplicità
        ConnectorType[] connectors = {
                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL
        };

        // Inserisco tile in (1,1), (2,2), (3,3)
        spaceship.getComponents()[1][1] = new Cabin(connectors, false, 0);
        spaceship.getComponents()[2][2] = new Cabin(connectors, false, 1);
        spaceship.getComponents()[3][3] = new Cabin(connectors, false, 2);

        // Imposto visited: solo (1,1) e (3,3) sono visitate
        spaceship.getVisited()[1][1] = true;
        spaceship.getVisited()[2][2] = false;
        spaceship.getVisited()[3][3] = true;
        spaceship.getVisited()[2][3] = true;

        // Chiamata al metodo pubblico
        spaceship.removeUnvisitedTiles();

        // Verifica
        assertNotNull("La tile (1,1) deve rimanere", spaceship.getComponents()[1][1]);
        assertNull("La tile (2,2) deve essere rimossa", spaceship.getComponents()[2][2]);
        assertNotNull("La tile (3,3) deve rimanere", spaceship.getComponents()[3][3]);

        // Verifica che la tile rimossa sia finita in reserveSpot
        assertEquals("Una sola tile deve essere spostata in reserveSpot", 1, spaceship.getReserveSpot().size());
        assertTrue("La tile rimossa è in reserveSpot", spaceship.getReserveSpot().get(0) instanceof Cabin);


    }
    @Test
    public void testValidateRemainingTiles_allValid() {
        ConnectorType[] connectorsCenter = {
                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL
        };

        ConnectorType[] connectorsRight = {
                ConnectorType.UNIVERSAL, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.UNIVERSAL
        };

        spaceship.getComponents()[2][3] = new Cabin(connectorsCenter, false, 0); // centro
        spaceship.getComponents()[2][4] = new Cabin(connectorsRight, false, 0); // destra

        boolean result = spaceship.validateRemainingTiles();

        assertTrue("Tutte le tile sono ben connesse, deve restituire true", result);
        assertTrue(spaceship.getComponents()[2][3].isWellConnected());
        assertTrue(spaceship.getComponents()[2][4].isWellConnected());
    }
    @Test
    public void testSwapValidNonSpecialGoods() throws CargoManagementException {
        GoodsBlock[] goods1 = new GoodsBlock[] {
                new GoodsBlock(ColorType.BLUE),
                new GoodsBlock(ColorType.GREEN),
                null
        };
        GoodsContainer grayContainer = new GoodsContainer(goods1, false, 1); // isSpecial = false

        // Container rosso (2 slot) - può contenere solo RED
        GoodsBlock[] goods2 = new GoodsBlock[] {
                new GoodsBlock(ColorType.RED),
                new GoodsBlock(ColorType.RED)
        };
        GoodsContainer redContainer = new GoodsContainer(goods2, true, 2); // isSpecial = true

        spaceship.getGoodsContainers().add(grayContainer); // index 0
        spaceship.getGoodsContainers().add(redContainer);// index 1

        spaceship.handleSwap(0, 0, 0, 1); // swap BLUE <-> GREEN

        GoodsBlock[] goods = spaceship.getGoodsContainers().get(0).getGoods();
        assertEquals(ColorType.GREEN, goods[0].getType());
        assertEquals(ColorType.BLUE, goods[1].getType());
    }
    @Test(expected = CargoManagementException.class)
    public void testSwapInvalidRedToGray() throws CargoManagementException {
        GoodsBlock[] goods1 = new GoodsBlock[] {
                new GoodsBlock(ColorType.BLUE),
                new GoodsBlock(ColorType.GREEN),
                null
        };
        GoodsContainer grayContainer = new GoodsContainer(goods1, false, 1); // isSpecial = false

        // Container rosso (2 slot) - può contenere solo RED
        GoodsBlock[] goods2 = new GoodsBlock[] {
                new GoodsBlock(ColorType.RED),
                new GoodsBlock(ColorType.RED)
        };
        GoodsContainer redContainer = new GoodsContainer(goods2, true, 2); // isSpecial = true

        spaceship.getGoodsContainers().add(grayContainer); // index 0
        spaceship.getGoodsContainers().add(redContainer);// index 1

        // provo a mettere RED (da container 1) nel grigio (container 0)
        spaceship.handleSwap(1, 0, 0, 0);
    }
    @Test(expected = CargoManagementException.class)
    public void testSwapOutOfBoundsCargoIndex() throws CargoManagementException {
        GoodsBlock[] goods1 = new GoodsBlock[] {
                new GoodsBlock(ColorType.BLUE),
                new GoodsBlock(ColorType.GREEN),
                null
        };
        GoodsContainer grayContainer = new GoodsContainer(goods1, false, 1); // isSpecial = false

        // Container rosso (2 slot) - può contenere solo RED
        GoodsBlock[] goods2 = new GoodsBlock[] {
                new GoodsBlock(ColorType.RED),
                new GoodsBlock(ColorType.RED)
        };
        GoodsContainer redContainer = new GoodsContainer(goods2, true, 2); // isSpecial = true

        spaceship.getGoodsContainers().add(grayContainer); // index 0
        spaceship.getGoodsContainers().add(redContainer);// index 1

        spaceship.handleSwap(0, 2, 0, 0); // cargoIndex2 fuori range
    }
    @Test
    public void testHandleRemove_valid() throws CargoManagementException {
        GoodsBlock[] goods1 = new GoodsBlock[] {
                new GoodsBlock(ColorType.BLUE),
                new GoodsBlock(ColorType.GREEN),
                null
        };
        GoodsContainer grayContainer = new GoodsContainer(goods1, false, 1); // isSpecial = false

        // Container rosso (2 slot) - può contenere solo RED
        GoodsBlock[] goods2 = new GoodsBlock[] {
                new GoodsBlock(ColorType.RED),
                new GoodsBlock(ColorType.RED)
        };
        GoodsContainer redContainer = new GoodsContainer(goods2, true, 2); // isSpecial = true

        spaceship.getGoodsContainers().add(grayContainer); // index 0
        spaceship.getGoodsContainers().add(redContainer);// index 1

        spaceship.handleRemove(0, 1); // Rimuovo GREEN

        GoodsBlock[] goods = spaceship.getGoodsContainers().get(0).getGoods();
        assertNull("Il blocco dovrebbe essere rimosso", goods[1]);
    }
    @Test
    public void testHandleRemove_fromNullSlot() throws CargoManagementException {
        GoodsBlock[] goods1 = new GoodsBlock[] {
                new GoodsBlock(ColorType.BLUE),
                new GoodsBlock(ColorType.GREEN),
                null
        };
        GoodsContainer grayContainer = new GoodsContainer(goods1, false, 1); // isSpecial = false

        // Container rosso (2 slot) - può contenere solo RED
        GoodsBlock[] goods2 = new GoodsBlock[] {
                new GoodsBlock(ColorType.RED),
                new GoodsBlock(ColorType.RED)
        };
        GoodsContainer redContainer = new GoodsContainer(goods2, true, 2); // isSpecial = true

        spaceship.getGoodsContainers().add(grayContainer); // index 0
        spaceship.getGoodsContainers().add(redContainer);// index 1

        spaceship.handleRemove(0, 2); // slot già null
        GoodsBlock[] goods = spaceship.getGoodsContainers().get(0).getGoods();
        assertNull("Slot dovrebbe restare null", goods[2]);
    }
    @Test
    public void testHandleAdd_yellowToGray_success() throws CargoManagementException {
        GoodsBlock[] goods1 = new GoodsBlock[] {
                new GoodsBlock(ColorType.BLUE),
                new GoodsBlock(ColorType.GREEN),
                null
        };
        GoodsContainer grayContainer = new GoodsContainer(goods1, false, 1); // isSpecial = false

        // Container rosso (2 slot) - può contenere solo RED
        GoodsBlock[] goods2 = new GoodsBlock[] {
                new GoodsBlock(ColorType.RED),
                new GoodsBlock(ColorType.RED)
        };
        GoodsContainer redContainer = new GoodsContainer(goods2, true, 2); // isSpecial = true

        spaceship.getGoodsContainers().add(grayContainer); // index 0
        spaceship.getGoodsContainers().add(redContainer);// index 1

        GoodsBlock[] reward = new GoodsBlock[] {
                new GoodsBlock(ColorType.YELLOW)
        };

        spaceship.handleAdd(reward, 0, 2, 0); // slot 2 è libero

        GoodsBlock added = spaceship.getGoodsContainers().get(0).getGoods()[2];
        assertNotNull(added);
        assertEquals(ColorType.YELLOW, added.getType());
    }
    @Test(expected = CargoManagementException.class)
    public void testHandleAdd_redToGray_fails() throws CargoManagementException {
        GoodsBlock[] goods1 = new GoodsBlock[] {
                new GoodsBlock(ColorType.BLUE),
                new GoodsBlock(ColorType.GREEN),
                null
        };
        GoodsContainer grayContainer = new GoodsContainer(goods1, false, 1); // isSpecial = false

        // Container rosso (2 slot) - può contenere solo RED
        GoodsBlock[] goods2 = new GoodsBlock[] {
                new GoodsBlock(ColorType.RED),
                new GoodsBlock(ColorType.RED)
        };
        GoodsContainer redContainer = new GoodsContainer(goods2, true, 2); // isSpecial = true

        spaceship.getGoodsContainers().add(grayContainer); // index 0
        spaceship.getGoodsContainers().add(redContainer);// index 1

        GoodsBlock[] reward = new GoodsBlock[] {
                new GoodsBlock(ColorType.RED)
        };

        spaceship.handleAdd(reward, 0, 2, 0); // slot libero, ma tipo non ammesso
    }
    @Test
    public void testCountBatteries_withMultiplePowerCenters() {

        // PowerCenter con 3 batterie attive
        ConnectorType[] connectors = new ConnectorType[] {
                ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH
        };
        PowerCenter pc1 = new PowerCenter(connectors, 3, 1);

        // PowerCenter con 2 batterie, di cui solo una attiva
        PowerCenter pc2 = new PowerCenter(connectors, 2, 2);
        pc2.getBatteries()[1] = false;

        // Aggiungili alla nave
        spaceship.getPowerCenters().add(pc1);
        spaceship.getPowerCenters().add(pc2);

        // Esegui il conteggio
        spaceship.countBatteries();

        // pc1: 3 attive, pc2: 1 attiva → totale 4
        assertEquals(4, spaceship.getnBatteries());
    }
    @Test
    public void testCountExposedConnectors_twoAdjacentTiles_oneConnectorCovered() {
        SpaceshipPlance spaceship = new SpaceshipPlance();

        ConnectorType[] connectors1 = new ConnectorType[] {
                ConnectorType.SINGLE, // NORTH
                ConnectorType.SINGLE, // EAST
                ConnectorType.SINGLE, // SOUTH
                ConnectorType.SINGLE  // WEST
        };

        ConnectorType[] connectors2 = new ConnectorType[] {
                ConnectorType.SINGLE, // NORTH
                ConnectorType.SINGLE, // EAST
                ConnectorType.SINGLE, // SOUTH
                ConnectorType.SINGLE  // WEST
        };

        ComponentTile tile1 = new CargoHolds(connectors1, 1, false, 3);
        ComponentTile tile2 = new CargoHolds(connectors2, 2, false, 3);

        // Posiziono tile1 al centro, tile2 a destra (copre lato EAST di tile1)
        spaceship.getComponents()[2][2] = tile1;
        spaceship.getComponents()[2][3] = tile2;

        int exposed = spaceship.countExposedConnectors();

        // 4 (tile1) + 4 (tile2) - 2 (connessione tra i due) = 6
        assertEquals(6, exposed);
    }

    @Test
    public void testValidTileConnection() {
        // UNIVERSAL <-> UNIVERSAL
        ConnectorType[] connectorsA = {ConnectorType.UNIVERSAL, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH};
        ConnectorType[] connectorsB = {ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.UNIVERSAL, ConnectorType.SMOOTH};

        spaceship.getComponents()[2][2] = new StructuralModule(connectorsA, 1);
        spaceship.getComponents()[1][2] = new StructuralModule(connectorsB, 2);

        assertTrue(spaceship.checkNewTile(2, 2));
    }

    @Test
    public void testInvalidEngineBlockedBehind() {
        ConnectorType[] connectors = {ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.ENGINE, ConnectorType.SMOOTH};
        spaceship.getComponents()[2][3] = new Engine(connectors, 1);
        spaceship.getComponents()[3][3] = new StructuralModule(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH}, 2);

        assertFalse(spaceship.checkNewTile(3, 2));
    }

    @Test
    public void testInvalidCannonBlockedInFront() {
        ConnectorType[] connectors = {ConnectorType.CANNON, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH};
        spaceship.getComponents()[2][2] = new Cannon(connectors, 1);
        spaceship.getComponents()[1][2] = new StructuralModule(new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH}, 2);

        assertFalse(spaceship.checkNewTile(2, 2));
    }

    @Test
    public void testInvalidConnectionType() {
        ConnectorType[] connectorsA = {ConnectorType.SINGLE, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH};
        ConnectorType[] connectorsB = {ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.DOUBLE, ConnectorType.SMOOTH};

        spaceship.getComponents()[2][2] = new StructuralModule(connectorsA, 1);
        spaceship.getComponents()[1][2] = new StructuralModule(connectorsB, 2);

        assertFalse(spaceship.checkNewTile(2, 2));
    }

    @Test
    public void testTileOnEdgeCaseReturnsFalse() {
        ConnectorType[] connectors = {ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL};
        spaceship.getComponents()[0][0] = new StructuralModule(connectors, 1); // edge case: (0,0)

        assertFalse(spaceship.checkNewTile(0, 0));
    }
    @Test
    public void testGetCannonDirection() {
        ConnectorType[] connectors = new ConnectorType[]{
                ConnectorType.SMOOTH,
                ConnectorType.CANNON,
                ConnectorType.SINGLE,
                ConnectorType.SMOOTH
        };
        Cannon cannon = new Cannon(connectors, 1);

        int dir = spaceship.getCannonDirection(cannon);
        assertEquals(1, dir);
    }

    @Test
    public void testNullComponentReturnsFalse() {
        assertFalse(spaceship.checkNewTile(2, 2)); // nessuna tile lì
    }

    @Test
    public void testRemoveAndSelectPart() {
        ComponentTile[][] grid = spaceship.getComponents();

        // Tile centrale
        grid[2][2] = new StructuralModule(
                new ConnectorType[] {
                        ConnectorType.UNIVERSAL, // N
                        ConnectorType.UNIVERSAL, // E
                        ConnectorType.UNIVERSAL, // S
                        ConnectorType.UNIVERSAL  // W
                }, 0
        );

        // Tile a nord (iteration 0)
        grid[1][2] = new StructuralModule(
                new ConnectorType[] {
                        ConnectorType.UNIVERSAL, ConnectorType.SMOOTH, ConnectorType.UNIVERSAL, ConnectorType.SMOOTH
                }, 1
        );

        // Tile a est (iteration 1)
        grid[2][3] = new StructuralModule(
                new ConnectorType[] {
                        ConnectorType.SMOOTH, ConnectorType.UNIVERSAL, ConnectorType.SMOOTH, ConnectorType.UNIVERSAL
                }, 2
        );

        // Tile a sud (iteration 2)
        grid[3][2] = new StructuralModule(
                new ConnectorType[] {
                        ConnectorType.UNIVERSAL, ConnectorType.SMOOTH, ConnectorType.UNIVERSAL, ConnectorType.SMOOTH
                }, 3
        );

        // Tile a ovest (iteration 3)
        grid[2][1] = new StructuralModule(
                new ConnectorType[] {
                        ConnectorType.SMOOTH, ConnectorType.UNIVERSAL, ConnectorType.SMOOTH, ConnectorType.UNIVERSAL
                }, 4
        );

        // Rimuove il centro e genera i "tronconi"
        int removed = spaceship.remove(2, 2);
        assertEquals(4, removed);

        // Mostra i tronconi generati
        int[][] shown = spaceship.getShownComponents();

        // Conta i tronconi distinti rilevati
        Set<Integer> tronconi = new HashSet<>();
        for (int y = 0; y < shown.length; y++) {
            for (int x = 0; x < shown[0].length; x++) {
                if (shown[y][x] != -1) {
                    tronconi.add(shown[y][x]);
                }
            }
        }

        assertEquals(4, tronconi.size());

        // Simula la scelta del troncone 2 (quello a sud, iteration=2)
        spaceship.selectPart(2);

        // Verifica che solo il sud sia rimasto
        assertNull(grid[2][2]); // centro
        assertNull(grid[1][2]); // nord
        assertNull(grid[2][3]); // est
        assertNotNull(grid[3][2]); // sud (chosen)
        assertNull(grid[2][1]); // ovest
    }

    @Test
    public void testCheckProtectionHitsSingleCannon() {
        ConnectorType[] cannonConnectors1 = {
                ConnectorType.SMOOTH,    // NORTH
                ConnectorType.CANNON,    // EAST
                ConnectorType.SMOOTH,    // SOUTH
                ConnectorType.SMOOTH     // WEST
        };
        Cannon cannon1 = new Cannon(cannonConnectors1, 1);
        spaceship.getComponents()[2][5] = cannon1;
        spaceship.getCannons().add(cannon1);
        // Esempio: direction EAST, posizione che dovrebbe colpire cannon1 in (2,5)
        int result = spaceship.checkProtection(Direction.EAST, 7);

        assertEquals("Should hit single cannon and return 1", 1, result);
    }
    @Test
    public void testCheckProtectionHitsDoubleCannonWest() {
        // Doppio cannone rivolto a OVEST (indice 3 = OVEST)
        ConnectorType[] doubleCannonConnectors = {
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH,
                ConnectorType.CANNON
        };
        DoubleCannon doubleCannon = new DoubleCannon(doubleCannonConnectors, 2);
        spaceship.getComponents()[2][3] = doubleCannon;
        spaceship.getCannons().add(doubleCannon);
        // Direzione WEST, posizione che colpisce doubleCannon in (2,3)

        int result = spaceship.checkProtection(Direction.WEST, 7);
        assertEquals("Should hit double cannon and return 2", 2, result);
    }
    @Test
    public void testCheckProtectionHitsNoCannon() {
        ConnectorType[] CabinConnectors = {
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH,
                ConnectorType.UNIVERSAL
        };
        Cabin cabin = new Cabin(CabinConnectors,false,3);
        spaceship.getComponents()[1][3] =cabin;
        // Posizione senza cannone (ad esempio in posizione vuota o tile non cannon)
        int result = spaceship.checkProtection(Direction.NORTH, 7);
        assertEquals("No cannon hit should return 0", 0, result);
    }
    @Test
    public void testCheckProtectionNoHit() {
        // Direzione e posizione che non colpiscono nulla
        int result = spaceship.checkProtection(Direction.WEST, 0);
        assertEquals("No hit should return -1", -1, result);
    }
    @Test
    public void testCheckInterconnectedCabinsEmptyInitially() {
        SpaceshipPlance spaceship = new SpaceshipPlance();

        // all'inizio dovrebbe essere vuoto
        assertTrue(spaceship.checkInterconnectedCabinsEmpty());
    }


    @Test
    public void testInterconnectedCabinRealConnection() {
        // Step 1: setup
        SpaceshipPlance spaceship = new SpaceshipPlance();

        // Cabina centrale è già a (2, 3), ha connettori UNIVERSAL su tutti i lati.

        // Step 2: creiamo una cabina a destra (2, 4) che si collega tramite lato sinistro
        ConnectorType[] rightCabinConnectors = new ConnectorType[] {
                ConnectorType.SMOOTH,         // NORTH
                ConnectorType.SMOOTH,         // EAST
                ConnectorType.SMOOTH,         // SOUTH
                ConnectorType.UNIVERSAL       // WEST (si connette alla cabina centrale)
        };

        Cabin rightCabin = new Cabin(rightCabinConnectors, false, 200);
        rightCabin.setWellConnected(true);  // opzionale, se il tuo sistema lo usa
        spaceship.getComponents()[2][4] = rightCabin;

        // Step 3: aggiungiamo la cabina alla lista interconnectedCabins
        spaceship.getInterconnectedCabins().add(rightCabin);

        // Step 4: verifichiamo che non sia vuota
        assertFalse("La lista interconnectedCabins non dovrebbe essere vuota", spaceship.checkInterconnectedCabinsEmpty());

        // Step 5: rimuoviamo la cabina
        spaceship.removeInterconnectedCabin(rightCabin);

        // Step 6: verifichiamo che ora sia vuota
        assertTrue("La lista interconnectedCabins dovrebbe essere vuota dopo la rimozione", spaceship.checkInterconnectedCabinsEmpty());
    }
    @Test
    public void testTileGridToStringTile() {
        // Arrange
        SpaceshipPlance spaceship = new SpaceshipPlance();

        // Create a dummy tile to be placed in the grid
        ConnectorType[] connectors = {
                ConnectorType.SMOOTH, ConnectorType.SMOOTH,
                ConnectorType.SMOOTH, ConnectorType.SMOOTH
        };

        Cabin cabin = new Cabin(connectors, false, 42);
        spaceship.getComponents()[2][2] = cabin;

        // Another tile to place
        Cabin highlightedCabin = new Cabin(connectors, false, 43);
        spaceship.getComponents()[2][3] = highlightedCabin;

        // Act
        String gridString = spaceship.tileGridToStringTile(highlightedCabin);

        // Assert
        assertNotNull("Grid string should not be null", gridString);
        assertFalse("Grid string should not be empty", gridString.trim().isEmpty());

        // Optional: print it for visual inspection
        System.out.println(gridString);

        // If tileCrafterbyTile uses tileToShow to highlight the tile in a custom way,
        // we can assert specific characters (e.g., a symbol or border)
        // For now, generic string presence test:
        assertTrue("Should contain a tile representation", gridString.contains(" "));
    }
    @Test
    public void testTileCrafterbyTile_IDCenteredAndConnectors() {
        // Arrange
        ConnectorType[] connectors = new ConnectorType[] {
                ConnectorType.UNIVERSAL,  // Top
                ConnectorType.SINGLE,     // Right
                ConnectorType.DOUBLE,     // Bottom
                ConnectorType.SMOOTH      // Left
        };

        Cabin tile = new Cabin(connectors, false, 42);
        Cabin tileToShow = tile; // È lo stesso, quindi deve mostrare l'ID

        SpaceshipPlance spaceship = new SpaceshipPlance();

        // Act
        char[][] result = spaceship.tileCrafterbyTile(tile, tileToShow);

        // Assert
        assertEquals("┌", String.valueOf(result[0][0])); // Angolo superiore sinistro
        assertEquals('U', result[0][2]); // Connettore sopra
        assertEquals('S', result[2][4]); // Connettore destra
        assertEquals('D', result[4][2]); // Connettore sotto
        assertEquals(' ', result[2][1]); // Prima dello ID
        assertEquals('4', result[2][2]); // ID parte 1
        assertEquals('2', result[2][3]); // ID parte 2
    }

    @Test
    public void testTileCrafterbyTile_OtherTile_ShowsSymbol() {
        // Arrange
        ConnectorType[] connectors = new ConnectorType[] {
                ConnectorType.SMOOTH, ConnectorType.SMOOTH,
                ConnectorType.SMOOTH, ConnectorType.SMOOTH
        };

        Cabin tile = new Cabin(connectors, false, 10);
        Engine tileToShow = new Engine(connectors, 20); // Tipo diverso → deve mostrare simbolo

        SpaceshipPlance spaceship = new SpaceshipPlance();

        // Assicurati che la mappa ASCII contenga una lettera per Cabin (es. 'C')
        TileSymbols.ASCII_TILE_SYMBOLS.put("CABIN", 'C');

        // Act
        char[][] result = spaceship.tileCrafterbyTile(tile, tileToShow);

        // Assert centrale
        assertEquals('C', result[2][2]); // Simbolo ASCII per CABIN al centro
    }
    @Test
    public void testRemoveMVGood_ValidRedCargoHold() {
        // CargoHold speciale con capacità 2 (valido per rosso)
        ConnectorType[] connectors = new ConnectorType[] {
                ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH
        };
        CargoHolds redHold = new CargoHolds(connectors, 101, true, 2);
        redHold.getGoods()[0] = new GoodsBlock(ColorType.RED);
        spaceship.getCargoHolds().add(redHold);

        // GoodsContainer con blocco rosso da rimuovere
        GoodsBlock[] goods = { new GoodsBlock(ColorType.RED) };
        GoodsContainer container = new GoodsContainer(goods, false, 1);
        spaceship.getGoodsContainers().add(container);

        boolean result = spaceship.removeMVGood(0, 0);

        assertTrue("Dovrebbe rimuovere il blocco rosso perché esiste un CargoHold valido", result);
    }
    @Test
    public void testCountGoods() {
        // Setup: crea CargoHolds con vari GoodsBlock
        ConnectorType[] connectors = new ConnectorType[] {
                ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH
        };

        CargoHolds cargo1 = new CargoHolds(connectors, 1, false, 3);
        cargo1.getGoods()[0] = new GoodsBlock(ColorType.BLUE);
        cargo1.getGoods()[1] = new GoodsBlock(ColorType.GREEN);
        cargo1.getGoods()[2] = null;

        CargoHolds cargo2 = new CargoHolds(connectors, 2, true, 2);
        cargo2.getGoods()[0] = new GoodsBlock(ColorType.RED);
        cargo2.getGoods()[1] = null;

        spaceship.getCargoHolds().clear();
        spaceship.getCargoHolds().add(cargo1);
        spaceship.getCargoHolds().add(cargo2);

        // Il metodo dovrebbe contare 3 GoodsBlock non nulli
        int count = spaceship.countGoods();

        assertEquals(3, count);
    }










}












