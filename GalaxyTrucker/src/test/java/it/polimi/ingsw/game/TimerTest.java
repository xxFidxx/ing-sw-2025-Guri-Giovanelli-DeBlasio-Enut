package it.polimi.ingsw.game;

import it.polimi.ingsw.model.game.Timer;
import org.junit.Test;
import static org.junit.Assert.*;

public class TimerTest {

    @Test
    public void testStartAndCountdown() throws InterruptedException {
        Timer timer = new Timer(2);
        timer.start();
        assertFalse(timer.isDone());

        Thread.sleep(1000); // Aspetta 1 secondo
        int remaining = timer.getRemainingTime();
        assertTrue(remaining <= 2 && remaining >= 1);

        Thread.sleep(1500); // Dopo altri 1.5 secondi, dovremmo essere a 0
        assertEquals(0, timer.getRemainingTime());
        assertTrue(timer.isDone());
    }

    @Test
    public void testReset() throws InterruptedException {
        Timer timer = new Timer(2);
        timer.start();
        Thread.sleep(1500);
        assertTrue(timer.getRemainingTime() <= 1);

        timer.reset(); // Riparte da capo
        Thread.sleep(500);
        assertTrue(timer.getRemainingTime() > 0);
    }
}
