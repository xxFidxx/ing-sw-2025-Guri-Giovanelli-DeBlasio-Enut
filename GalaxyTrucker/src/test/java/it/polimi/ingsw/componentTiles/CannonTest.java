package it.polimi.ingsw.componentTiles;

import static org.junit.Assert.*;

import it.polimi.ingsw.model.componentTiles.Cannon;
import it.polimi.ingsw.model.componentTiles.ConnectorType;
import org.junit.Test;

public class CannonTest {

    @Test
    public void testGetPower_withCannonConnector() {
        ConnectorType[] connectors = new ConnectorType[] {
                ConnectorType.CANNON, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.SMOOTH
        };
        Cannon cannon = new Cannon(connectors, 1);

        float power = cannon.getPower();

        assertEquals(1.0F, power, 0.001);
    }

    @Test
    public void testGetPower_withNonCannonConnector() {
        ConnectorType[] connectors = new ConnectorType[] {
                ConnectorType.SINGLE, ConnectorType.UNIVERSAL, ConnectorType.DOUBLE, ConnectorType.SMOOTH
        };
        Cannon cannon = new Cannon(connectors, 2);

        float power = cannon.getPower();

        assertEquals(0.5F, power, 0.001);
    }
}

