package it.polimi.ingsw.game;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import it.polimi.ingsw.model.componentTiles.Cabin;
import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.componentTiles.ConnectorType;
import it.polimi.ingsw.model.componentTiles.Engine;
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
