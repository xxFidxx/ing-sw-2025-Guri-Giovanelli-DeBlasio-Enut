package it.polimi.ingsw.game;
import static org.junit.Assert.*;

import it.polimi.ingsw.model.game.Dice;
import org.junit.Test;

public class DiceTest {

    @Test
    public void testInitialNumber() {
        Dice dice = new Dice();
        assertEquals(6, dice.getNumber());
    }

    @Test
    public void testThrowReturnsValidNumber() {
        Dice dice = new Dice();
        for (int i = 0; i < 100; i++) {  // Ripetiamo più volte per essere sicuri
            int roll = dice.thr();
            assertTrue("Roll should be between 1 and 6", roll >= 1 && roll <= 6);
            assertEquals(roll, dice.getNumber());
        }
    }
}

