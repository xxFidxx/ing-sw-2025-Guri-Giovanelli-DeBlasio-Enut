package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.AbandonedShipCard;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class AbandonedShipCardTest {

    private Game game;
    private Flightplance flightPlance;
    private Deck deck;
    private Player player;
    private AbandonedShipCard card;

    @Before
    public void setUp() {
        game = mock(Game.class);
        flightPlance = mock(Flightplance.class);
        deck = mock(Deck.class);
        player = mock(Player.class);

        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);
        when(game.choosePlayer(any())).thenReturn(player);

        // Creo la carta con lostDays = 1, lostCrew = 3, credits = 4
        card = new AbandonedShipCard("Nave", 1, 1, 3, 4, deck);
    }

    @Test
    public void testActivate_shouldApplyEffectsToPlayer() {
        when(player.getNumEquip()).thenReturn(5); // Equipaggio iniziale
        when(player.getCredits()).thenReturn(3);  // Crediti iniziali

        card.activate();

        verify(game).choosePlayer(card);
        verify(player).setNumEquip(2);     // 5 - 3
        verify(player).setCredits(7);      // 3 + 4
        verify(flightPlance).move(-1, player); // lostDays = 1
    }

    @Test
    public void testActivate_withNullPlayer_shouldDoNothing() {
        when(game.choosePlayer(card)).thenReturn(null);

        card.activate();

        verify(player, never()).setNumEquip(anyInt());
        verify(player, never()).setCredits(anyInt());
        verify(flightPlance, never()).move(anyInt(), any());
    }

    @Test
    public void testCheckCondition_whenPlayerHasEnoughCrew_shouldReturnTrue() {
        when(player.getNumEquip()).thenReturn(3); // Uguale a lostCrew
        assertTrue(card.checkCondition(player));

        when(player.getNumEquip()).thenReturn(5); // Maggiore di lostCrew
        assertTrue(card.checkCondition(player));
    }

    @Test
    public void testCheckCondition_whenPlayerHasNotEnoughCrew_shouldReturnFalse() {
        when(player.getNumEquip()).thenReturn(2); // Minore di lostCrew
        assertFalse(card.checkCondition(player));
    }
}