package it.polimi.ingsw.adventureCards;

import static it.polimi.ingsw.model.game.ColorType.YELLOW;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.model.adventureCards.AbandonedStationCard;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.SpaceshipPlance;
import org.junit.Before;
import org.junit.Test;

public class AbandonedStationCardTest {

    private AbandonedStationCard card;
    private Player mockPlayer;
    private SpaceshipPlance mockPlance;
    private Deck mockDeck;
    private Flightplance mockFlightPlance;

    private GoodsBlock[] reward;

    @Before
    public void setUp() {
        // Ricompensa della carta: 2 GoodsBlock (es. tipo YELLOW, valore 3)
        reward = new GoodsBlock[] {
                new GoodsBlock(YELLOW),
                new GoodsBlock(YELLOW)
        };

        // requiredCrew = 6, lostDays = 1
        card = new AbandonedStationCard("Abandoned Station 2", 1, 1, 6, reward);

        mockPlayer = mock(Player.class);
        mockPlance = mock(SpaceshipPlance.class);
        when(mockPlayer.getSpaceshipPlance()).thenReturn(mockPlance);

        mockDeck = mock(Deck.class);
        mockFlightPlance = mock(Flightplance.class);
        when(mockDeck.getFlightPlance()).thenReturn(mockFlightPlance);

        card.setDeck(mockDeck);
        card.setActivatedPlayer(mockPlayer);
    }

    @Test
    public void testActivate_setsRewardAndMovesBackDays() {
        card.activate();

        // Verifica che venga assegnata la ricompensa
        verify(mockPlayer).setReward(reward);

        // Verifica che venga spostato indietro di lostDays
        verify(mockFlightPlance).move(-1, mockPlayer);
    }

    @Test
    public void testCheckCondition_returnsTrue_whenEnoughCrew() {
        when(mockPlance.getCrew()).thenReturn(7); // >= requiredCrew (6)

        assertTrue(card.checkCondition(mockPlayer));
    }

    @Test
    public void testCheckCondition_returnsTrue_whenExactlyEnoughCrew() {
        when(mockPlance.getCrew()).thenReturn(6); // == requiredCrew

        assertTrue(card.checkCondition(mockPlayer));
    }

    @Test
    public void testCheckCondition_returnsFalse_whenNotEnoughCrew() {
        when(mockPlance.getCrew()).thenReturn(4); // < requiredCrew

        assertFalse(card.checkCondition(mockPlayer));
    }

}

