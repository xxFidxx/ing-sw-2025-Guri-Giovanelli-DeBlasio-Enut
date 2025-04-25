package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.SlaversCard;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SlaversCardTest {
    private Deck deck;

    @Test
    public void testReward_shouldIncreaseCreditsByReward() {
        Player player = mock(Player.class);
        when(player.getCredits()).thenReturn(5); // valore iniziale

        SlaversCard card = new SlaversCard("Slavers", 1, deck, 5, 2, 1, 3);

        card.reward(player);

        // Controlla che i crediti finali siano 5 + 3 = 8
        verify(player).setCredits(8);
    }

    @Test
    public void testPenalize_shouldLoseCorrectAmountOfCrew() {
        Player player = mock(Player.class);

        SlaversCard card = new SlaversCard("Slavers", 1, deck, 5, 2, 2, 3);

        card.penalize(player);

        // Verifica che il metodo loseCrew venga chiamato con il numero giusto
        verify(player).loseCrew(2);
    }

}