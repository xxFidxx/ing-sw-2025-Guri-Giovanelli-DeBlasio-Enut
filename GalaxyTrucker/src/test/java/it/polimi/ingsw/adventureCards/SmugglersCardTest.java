package it.polimi.ingsw.adventureCards;

import static it.polimi.ingsw.model.game.ColorType.BLUE;
import static it.polimi.ingsw.model.game.ColorType.GREEN;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import it.polimi.ingsw.model.adventureCards.SmugglersCard;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.game.Player;
import org.junit.Before;
import org.junit.Test;

public class SmugglersCardTest {

    private SmugglersCard card;
    private Player mockPlayer;
    private GoodsBlock[] reward;

    @Before
    public void setUp() {
        mockPlayer = mock(Player.class);

        reward = new GoodsBlock[] {
                new GoodsBlock(BLUE), // valore 1
                new GoodsBlock(GREEN) // calore 2
        };

        // cannonStrength = 3, lostDays = 1, lossMalus = 1
        card = new SmugglersCard("Smugglers", 1, 3, 1, 1, reward);
    }

    @Test
    public void testReward_setsGoodsCorrectly() {
        card.reward(mockPlayer);

        verify(mockPlayer).setReward(reward);
    }
}

