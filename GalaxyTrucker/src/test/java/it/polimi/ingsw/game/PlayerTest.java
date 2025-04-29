package it.polimi.ingsw.game;

import it.polimi.ingsw.model.componentTiles.DoubleEngine;
import it.polimi.ingsw.model.componentTiles.Engine;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.SpaceshipPlance;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlayerTest {

    private Player player;
    private SpaceshipPlance spaceshipPlance;
    private Engine singleEngine;
    private DoubleEngine doubleEngineCharged;
    private DoubleEngine doubleEngineNotCharged;

    @Before
    public void setUp() {
        Game mockGame = mock(Game.class);
        player = spy(new Player("TestPlayer", mockGame, 0));

        spaceshipPlance = mock(SpaceshipPlance.class);
        singleEngine = mock(Engine.class);
        doubleEngineCharged = mock(DoubleEngine.class);
        doubleEngineNotCharged = mock(DoubleEngine.class);

        player.setSpaceshipPlance(spaceshipPlance);

    }

    @Test
    public void testGetEngineStrenght_WithBrownAlienAndEngines() {
        when(spaceshipPlance.getBrownAliens()).thenReturn(1);

        ArrayList<Engine> engines = new ArrayList<>();
        engines.add(singleEngine);
        engines.add(doubleEngineCharged);
        engines.add(doubleEngineNotCharged);

        when(spaceshipPlance.getEngines()).thenReturn(engines);

        when(singleEngine.getPower()).thenReturn(1);
        when(doubleEngineCharged.isCharged()).thenReturn(true);
        when(doubleEngineCharged.getPower()).thenReturn(2);
        when(doubleEngineNotCharged.isCharged()).thenReturn(false);
        when(doubleEngineNotCharged.getPower()).thenReturn(2);

        // askToUseBattery true per doubleEngineCharged
        doReturn(true).when(player).askToUseBattery();

        int result = player.getEngineStrenght();

        // Calcolo:
        // +2 (brown alien)
        // +1 (single engine)
        // +2 (doubleEngineCharged: charged + user says yes)
        // +0 (doubleEngineNotCharged: no battery)
        // totale = 5
        assertEquals(5, result);
    }

    @Test
    public void testGetEngineStrenght_NoBrownAlien_DeclineUseBattery() {
        when(spaceshipPlance.getBrownAliens()).thenReturn(0);

        ArrayList<Engine> engines = new ArrayList<>();
        engines.add(doubleEngineCharged);

        when(spaceshipPlance.getEngines()).thenReturn(engines);

        when(doubleEngineCharged.isCharged()).thenReturn(true);
        when(doubleEngineCharged.getPower()).thenReturn(2);

        // askToUseBattery false
        doReturn(false).when(player).askToUseBattery();

        int result = player.getEngineStrenght();

        // Brown alien assente
        // DoubleEngineCharged: carico ma l'utente dice NO → 0
        assertEquals(0, result);
    }

    @Test
    public void testGetEngineStrenght_SingleEngineOnly() {
        when(spaceshipPlance.getBrownAliens()).thenReturn(0);

        ArrayList<Engine> engines = new ArrayList<>();
        engines.add(singleEngine);

        when(spaceshipPlance.getEngines()).thenReturn(engines);
        when(singleEngine.getPower()).thenReturn(1);

        int result = player.getEngineStrenght();

        // Solo single engine +1
        assertEquals(1, result);
    }

    @Test
    public void testGetEngineStrenght_NoEngines() {
        when(spaceshipPlance.getBrownAliens()).thenReturn(1);
        when(spaceshipPlance.getEngines()).thenReturn(new ArrayList<>());

        int result = player.getEngineStrenght();

        // Nessun motore
        assertEquals(0, result);
    }
}

