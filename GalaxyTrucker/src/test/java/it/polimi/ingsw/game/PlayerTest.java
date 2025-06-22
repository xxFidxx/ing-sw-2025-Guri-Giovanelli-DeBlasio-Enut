package it.polimi.ingsw.game;

import it.polimi.ingsw.model.componentTiles.Cannon;
import it.polimi.ingsw.model.componentTiles.DoubleCannon;
import it.polimi.ingsw.model.componentTiles.DoubleEngine;
import it.polimi.ingsw.model.componentTiles.Engine;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.SpaceshipPlance;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlayerTest {

    private Player player;
    private SpaceshipPlance spaceshipPlance;
    private Engine singleEngine;
    private DoubleEngine doubleEngineCharged;
    private DoubleEngine doubleEngineNotCharged;
    private Cannon wellPlacedCannon;
    private Cannon badlyPlacedCannon;
    private DoubleCannon chargedDoubleWellPlaced;
    private DoubleCannon chargedDoubleBadlyPlaced;
    private DoubleCannon unchargedDouble;

    @Before
    public void setUp() {
        Game mockGame = mock(Game.class);
        player = spy(new Player("TestPlayer", mockGame, 0));

        spaceshipPlance = mock(SpaceshipPlance.class);
        singleEngine = mock(Engine.class);
        doubleEngineCharged = mock(DoubleEngine.class);
        doubleEngineNotCharged = mock(DoubleEngine.class);
        wellPlacedCannon = mock(Cannon.class);
        badlyPlacedCannon = mock(Cannon.class);
        chargedDoubleWellPlaced = mock(DoubleCannon.class);
        chargedDoubleBadlyPlaced = mock(DoubleCannon.class);
        unchargedDouble = mock(DoubleCannon.class);

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

    @Test
    public void testGetFireStrenght_WithMixedCannonsAndPurpleAlien() {
        // Setup 1 purple alien
        when(spaceshipPlance.getPurpleAliens()).thenReturn(1);

        // Crea lista cannoni
        ArrayList<Cannon> cannons = new ArrayList<>();
        cannons.add(wellPlacedCannon);              // 1
        cannons.add(badlyPlacedCannon);             // 0.5
        cannons.add(chargedDoubleWellPlaced);       // 2
        cannons.add(chargedDoubleBadlyPlaced);      // 1
        cannons.add(unchargedDouble);               // 0 (non carico)

        when(spaceshipPlance.getCannons()).thenReturn(cannons);

        // Singoli
        when(wellPlacedCannon.getPower()).thenReturn(1f);
        when(badlyPlacedCannon.getPower()).thenReturn(0.5f);

        // Doppi caricati
        when(chargedDoubleWellPlaced.isCharged()).thenReturn(true);
        when(chargedDoubleWellPlaced.getPower()).thenReturn(2f);

        when(chargedDoubleBadlyPlaced.isCharged()).thenReturn(true);
        when(chargedDoubleBadlyPlaced.getPower()).thenReturn(1f);

        // Doppio non carico (ignorato)
        when(unchargedDouble.isCharged()).thenReturn(false);
        when(unchargedDouble.getPower()).thenReturn(2f); // non usato

        // Calcolo atteso:
        // 1 (wellPlacedCannon)
        // +0.5 (badlyPlacedCannon)
        // +2 (chargedDoubleWellPlaced)
        // +1 (chargedDoubleBadlyPlaced)
        // +0 (unchargedDouble ignorato)
        // +2 (bonus alieno viola)
        // = 6.5

        float result = player.getFireStrenght();
        assertEquals(6.5f, result, 0.01f);
    }




}

