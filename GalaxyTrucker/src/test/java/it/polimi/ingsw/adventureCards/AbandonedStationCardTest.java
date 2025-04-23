package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.AbandonedStationCard;
import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.game.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AbandonedStationCardTest {

    private Deck deck;
    private Flightplance flightPlance;
    private Game game;
    private Player player;
    private SpaceshipPlance spaceshipPlance;
    private GoodsBlock[] reward;

    @Before
    public void setUp() {
        deck = mock(Deck.class);
        flightPlance = mock(Flightplance.class);
        game = mock(Game.class);
        player = mock(Player.class);
        spaceshipPlance = mock(SpaceshipPlance.class);

        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);
        when(game.choosePlayer(any(AdventureCard.class))).thenReturn(player);
        when(player.getSpaceshipPlance()).thenReturn(spaceshipPlance);

        reward = new GoodsBlock[] {
                new GoodsBlock(2, ColorType.YELLOW),
                new GoodsBlock(1, ColorType.GREEN)
        };
    }

    @Test
    public void testActivate_withChosenPlayer_shouldGiveRewardAndMoveBack() {
        AbandonedStationCard card = new AbandonedStationCard("Abandoned Station", 1, deck, 1, 1, reward);

        card.activate();

        // Verifica: assegnazione ricompensa
        verify(spaceshipPlance).cargoManagement(reward);
        // Verifica: movimento all'indietro del giocatore
        verify(flightPlance).move(-1, player); // lostDays = 1
    }

    @Test
    public void testActivate_withNullPlayer_shouldNotDoAnything() {
        when(game.choosePlayer(any(AdventureCard.class))).thenReturn(null);
        AbandonedStationCard card = new AbandonedStationCard("Abandoned Station", 1, deck, 1, 1, reward);

        card.activate();

        verify(spaceshipPlance, never()).cargoManagement(any());
        verify(flightPlance, never()).move(anyInt(), any());
    }

    @Test
    public void testCheckCondition_whenPlayerHasEnoughCrew_shouldReturnTrue() {
        AbandonedStationCard card = new AbandonedStationCard("Abandoned Station", 1, deck, 1, 3, reward);

        when(player.getNumEquip()).thenReturn(3); // Uguale a requiredCrew
        assertTrue(card.checkCondition(player));

        when(player.getNumEquip()).thenReturn(5); // Maggiore di requiredCrew
        assertTrue(card.checkCondition(player));
    }

    @Test
    public void testCheckCondition_whenPlayerHasNotEnoughCrew_shouldReturnFalse() {
        AbandonedStationCard card = new AbandonedStationCard("Abandoned Station", 1, deck, 1, 3, reward);

        when(player.getNumEquip()).thenReturn(2); // Minore di requiredCrew
        assertFalse(card.checkCondition(player));
    }
}
