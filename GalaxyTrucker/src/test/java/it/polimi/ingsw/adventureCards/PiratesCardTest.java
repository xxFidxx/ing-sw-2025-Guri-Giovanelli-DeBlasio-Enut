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

        PiratesCard card = new PiratesCard("Pirates", 1, 5, 1, new Projectile[0], 4);

        card.reward(player);

        verify(player).setCredits(7); // 3 + 4
    }

}