package it.polimi.ingsw.adventureCards;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import it.polimi.ingsw.model.adventureCards.SlaversCard;
import it.polimi.ingsw.model.game.Player;
import org.junit.Before;
import org.junit.Test;

public class SlaversCardTest {

    private SlaversCard card;
    private Player mockPlayer;

    @Before
    public void setUp() {
        mockPlayer = mock(Player.class);

        // reward = 7, cannonStrength = 3, lostDays = 1, lostCrew = 2
        card = new SlaversCard("Slavers", 1, 3, 1, 2, 7);
    }

    @Test
    public void testReward_addsCreditsCorrectly() {
        when(mockPlayer.getCredits()).thenReturn(12);  // Crediti iniziali

        card.reward(mockPlayer);

        verify(mockPlayer).setCredits(19);  // 12 + 7 = 19
    }
}

