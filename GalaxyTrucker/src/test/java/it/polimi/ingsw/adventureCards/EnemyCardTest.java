package it.polimi.ingsw.adventureCards;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Player;
import org.junit.Before;
import org.junit.Test;

public class EnemyCardTest {

    private ConcreteEnemyCard dummyCard;
    private Player mockPlayer;
    private Deck mockDeck;
    private Flightplance mockFlightPlance;

    @Before
    public void setUp() {
        mockPlayer = mock(Player.class);
        mockDeck = mock(Deck.class);
        mockFlightPlance = mock(Flightplance.class);

        when(mockDeck.getFlightPlance()).thenReturn(mockFlightPlance);

        // cannonStrength = 5, lostDays = 2
        dummyCard = new ConcreteEnemyCard("Dummy", 1, 5, 2);
        dummyCard.setActivatedPlayer(mockPlayer);
        dummyCard.setDeck(mockDeck);
    }

    @Test
    public void testGetFightOutcome_playerWins() {
        when(mockPlayer.getFireStrenght()).thenReturn(6f); // > cannonStrength

        int outcome = dummyCard.getFightOutcome(mockPlayer);
        assertEquals(1, outcome);
    }

    @Test
    public void testGetFightOutcome_playerLoses() {
        when(mockPlayer.getFireStrenght()).thenReturn(3f); // < cannonStrength

        int outcome = dummyCard.getFightOutcome(mockPlayer);
        assertEquals(-1, outcome);
    }

    @Test
    public void testGetFightOutcome_tie() {
        when(mockPlayer.getFireStrenght()).thenReturn(5f); // = cannonStrength

        int outcome = dummyCard.getFightOutcome(mockPlayer);
        assertEquals(0, outcome);
    }

    @Test
    public void testActivate_playerWins_callsRewardAndMovesBack() {
        when(mockPlayer.getFireStrenght()).thenReturn(6f); // > cannonStrength

        dummyCard.activate();

        assertTrue(dummyCard.rewardCalled);
        assertFalse(dummyCard.penalizeCalled);
        verify(mockFlightPlance).move(-2, mockPlayer);
    }

    @Test
    public void testActivate_playerLoses_callsPenalize() {
        when(mockPlayer.getFireStrenght()).thenReturn(3f); // < cannonStrength

        dummyCard.activate();

        assertFalse(dummyCard.rewardCalled);
        assertTrue(dummyCard.penalizeCalled);
        verify(mockFlightPlance, never()).move(anyInt(), any());
    }

    @Test
    public void testActivate_tie_callsNothing() {
        when(mockPlayer.getFireStrenght()).thenReturn(5f); // = cannonStrength

        dummyCard.activate();

        assertFalse(dummyCard.rewardCalled);
        assertFalse(dummyCard.penalizeCalled);
        verify(mockFlightPlance, never()).move(anyInt(), any());
    }
}

