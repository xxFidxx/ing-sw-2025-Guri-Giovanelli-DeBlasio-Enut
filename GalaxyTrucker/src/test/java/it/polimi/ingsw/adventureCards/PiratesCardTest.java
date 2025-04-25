package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.PiratesCard;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.resources.Projectile;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PiratesCardTest {
    private Deck deck;

    @Test
    public void testReward_shouldIncreaseCreditsByReward() {
        Player player = mock(Player.class);
        when(player.getCredits()).thenReturn(3);

        PiratesCard card = new PiratesCard("Pirates", 1, deck, 5, 1, new Projectile[0], 4);

        card.reward(player);

        verify(player).setCredits(7); // 3 + 4
    }

    @Test
    public void testPenalize_shouldActivateAllProjectilesWithDiceRoll() {
        // Preparazione mock
        Player player = mock(Player.class);
        Game game = mock(Game.class);
        deck = mock(Deck.class);
        Flightplance flightPlance = mock(Flightplance.class);
        Projectile p1 = mock(Projectile.class);
        Projectile p2 = mock(Projectile.class);
        Projectile[] shots = new Projectile[]{p1, p2};

        when(game.throwDices()).thenReturn(3);
        when(flightPlance.getGame()).thenReturn(game);
        when(deck.getFlightPlance()).thenReturn(flightPlance);

        PiratesCard card = new PiratesCard("Pirates", 1, deck, 5, 1, shots, 4);

        // Esegui penalità
        card.penalize(player);

        // Verifica che tutti i proiettili siano stati attivati con posizione = 3
        verify(p1).activate(player, 3);
        verify(p2).activate(player, 3);
    }

}