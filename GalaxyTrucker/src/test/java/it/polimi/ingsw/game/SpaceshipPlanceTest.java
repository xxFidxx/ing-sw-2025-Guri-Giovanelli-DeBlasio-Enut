package it.polimi.ingsw.game;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import it.polimi.ingsw.model.componentTiles.Cabin;
import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.componentTiles.ConnectorType;
import it.polimi.ingsw.model.componentTiles.Engine;
import it.polimi.ingsw.model.game.SpaceshipPlance;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.game.SpaceshipPlance;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.*;




public class SpaceshipPlanceTest {
    private SpaceshipPlance spaceship;

    @Before
    public void setUp() {
        spaceship = new SpaceshipPlance();
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
    public void testIsTileValid_ValidConnection() {
        // Cabin a [2][3] con UNIVERSAL a est
        ComponentTile cabin = new Cabin(new ConnectorType[]{
                ConnectorType.SMOOTH, ConnectorType.UNIVERSAL,
                ConnectorType.SMOOTH, ConnectorType.SMOOTH
        }, true, 1);

        // Cargo a [2][4] con UNIVERSAL a ovest
        ComponentTile cargo = new CargoHolds(new ConnectorType[]{
                ConnectorType.SMOOTH, ConnectorType.SMOOTH,
                ConnectorType.SMOOTH, ConnectorType.UNIVERSAL
        }, 2, false, 2);

        spaceship.getComponents()[2][3] = cabin;
        spaceship.getComponents()[2][4] = cargo;

        assertTrue(spaceship.isTileValid(3, 2));
    }

    @Test
    public void testIsTileValid_InvalidConnection() {
        // Cabin con CANNON a est
        ComponentTile cabin = new Cabin(new ConnectorType[]{
                ConnectorType.SMOOTH, ConnectorType.CANNON,
                ConnectorType.SMOOTH, ConnectorType.SMOOTH
        }, true, 3);

        // Cargo con ENGINE a ovest
        ComponentTile cargo = new CargoHolds(new ConnectorType[]{
                ConnectorType.SMOOTH, ConnectorType.SMOOTH,
                ConnectorType.SMOOTH, ConnectorType.ENGINE
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




}
