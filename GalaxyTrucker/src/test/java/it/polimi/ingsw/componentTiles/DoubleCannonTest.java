package it.polimi.ingsw.componentTiles;

import static org.junit.Assert.*;

import it.polimi.ingsw.model.componentTiles.ConnectorType;
import it.polimi.ingsw.model.componentTiles.DoubleCannon;
import org.junit.Before;
import org.junit.Test;

public class DoubleCannonTest {

    private ConnectorType[] cannonConnectors;

    @Before
    public void setUp() {
        cannonConnectors = new ConnectorType[] {
                ConnectorType.CANNON, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.UNIVERSAL
        };
    }

    @Test
    public void testGetPower_withCannonConnector() {
        DoubleCannon dc = new DoubleCannon(cannonConnectors, 1);
        float power = dc.getPower();
        assertEquals(2.0F, power, 0.001);
    }

    @Test
    public void testIsCharged_defaultFalse() {
        DoubleCannon dc = new DoubleCannon(cannonConnectors, 3);
        assertFalse(dc.isCharged());
    }

    @Test
    public void testSetCharged_trueThenFalse() {
        DoubleCannon dc = new DoubleCannon(cannonConnectors, 4);
        dc.setCharged(true);
        assertTrue(dc.isCharged());

        dc.setCharged(false);
        assertFalse(dc.isCharged());
    }
}
