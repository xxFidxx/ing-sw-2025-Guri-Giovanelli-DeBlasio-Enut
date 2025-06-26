package it.polimi.ingsw.componentTiles;

import static org.junit.Assert.*;

import it.polimi.ingsw.model.componentTiles.ConnectorType;
import it.polimi.ingsw.model.componentTiles.StructuralModule;
import org.junit.Test;

public class ComponentTileTest {

    @Test
    public void testRotateClockwise_updatesConnectorsAndRotation() {
        ConnectorType[] connectors = {
                ConnectorType.SINGLE,     // 0 - Top
                ConnectorType.DOUBLE,     // 1 - Right
                ConnectorType.UNIVERSAL,  // 2 - Bottom
                ConnectorType.SMOOTH      // 3 - Left
        };

        StructuralModule tile = new StructuralModule(connectors, 1);
        tile.rotateClockwise();

        ConnectorType[] expected = {
                ConnectorType.SMOOTH,     // now Top
                ConnectorType.SINGLE,     // now Right
                ConnectorType.DOUBLE,     // now Bottom
                ConnectorType.UNIVERSAL   // now Left
        };

        assertArrayEquals(expected, tile.getConnectors());
        assertEquals(90, tile.getRotation());
    }

    @Test
    public void testRotateCounterClockwise_updatesConnectorsAndRotation() {
        ConnectorType[] connectors = {
                ConnectorType.SINGLE,     // 0 - Top
                ConnectorType.DOUBLE,     // 1 - Right
                ConnectorType.UNIVERSAL,  // 2 - Bottom
                ConnectorType.SMOOTH      // 3 - Left
        };

        StructuralModule tile = new StructuralModule(connectors, 2);
        tile.rotateCounterClockwise();

        ConnectorType[] expected = {
                ConnectorType.DOUBLE,     // now Top
                ConnectorType.UNIVERSAL,  // now Right
                ConnectorType.SMOOTH,     // now Bottom
                ConnectorType.SINGLE      // now Left
        };

        assertArrayEquals(expected, tile.getConnectors());
        assertEquals(-90, tile.getRotation());
    }
}

