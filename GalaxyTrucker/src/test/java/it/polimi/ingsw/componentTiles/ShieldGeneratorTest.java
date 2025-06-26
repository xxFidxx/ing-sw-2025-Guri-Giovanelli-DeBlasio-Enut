package it.polimi.ingsw.componentTiles;

import it.polimi.ingsw.model.componentTiles.ConnectorType;
import it.polimi.ingsw.model.componentTiles.Direction;
import it.polimi.ingsw.model.componentTiles.ShieldGenerator;
import org.junit.Test;
import static org.junit.Assert.*;

public class ShieldGeneratorTest {

    @Test
    public void testRotateClockwise_updatesConnectorsAndProtection() {
        ConnectorType[] connectors = {
                ConnectorType.SINGLE,     // Top
                ConnectorType.DOUBLE,     // Right
                ConnectorType.UNIVERSAL,  // Bottom
                ConnectorType.SMOOTH      // Left
        };

        boolean[] protection = {
                true, false, false, true
        };

        ShieldGenerator shield = new ShieldGenerator(connectors.clone(), protection.clone(), 1);
        shield.rotateClockwise();

        ConnectorType[] expectedConnectors = {
                ConnectorType.SMOOTH,
                ConnectorType.SINGLE,
                ConnectorType.DOUBLE,
                ConnectorType.UNIVERSAL
        };

        boolean[] expectedProtection = {
                true,  // <- da index 3
                true,  // <- da index 0
                false, // <- da index 1
                false  // <- da index 2
        };

        assertArrayEquals(expectedConnectors, shield.getConnectors());
        assertArrayEquals(expectedProtection, shield.getProtection());
        assertEquals(90, shield.getRotation());
    }

    @Test
    public void testRotateCounterClockwise_updatesConnectorsAndProtection() {
        ConnectorType[] connectors = {
                ConnectorType.SINGLE,
                ConnectorType.DOUBLE,
                ConnectorType.UNIVERSAL,
                ConnectorType.SMOOTH
        };

        boolean[] protection = {
                true, false, true, false
        };

        ShieldGenerator shield = new ShieldGenerator(connectors.clone(), protection.clone(), 2);
        shield.rotateCounterClockwise();

        ConnectorType[] expectedConnectors = {
                ConnectorType.DOUBLE,
                ConnectorType.UNIVERSAL,
                ConnectorType.SMOOTH,
                ConnectorType.SINGLE
        };

        boolean[] expectedProtection = {
                false, // <- da index 1
                true,  // <- da index 2
                false, // <- da index 3
                true   // <- da index 0
        };

        assertArrayEquals(expectedConnectors, shield.getConnectors());
        assertArrayEquals(expectedProtection, shield.getProtection());
        assertEquals(-90, shield.getRotation());
    }

    @Test
    public void testCheckProtection_returnsCorrectValuePerDirection() {
        // Supponiamo: NORTH → true, EAST → false, SOUTH → true, WEST → false
        boolean[] protection = {true, false, true, false};
        ConnectorType[] connectors = {
                ConnectorType.SINGLE,
                ConnectorType.DOUBLE,
                ConnectorType.UNIVERSAL,
                ConnectorType.SMOOTH
        };

        ShieldGenerator shield = new ShieldGenerator(connectors, protection, 1);

        assertTrue(shield.checkProtection(Direction.NORTH));
        assertFalse(shield.checkProtection(Direction.EAST));
        assertTrue(shield.checkProtection(Direction.SOUTH));
        assertFalse(shield.checkProtection(Direction.WEST));
    }
}

