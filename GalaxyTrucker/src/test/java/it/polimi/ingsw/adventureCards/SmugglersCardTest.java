package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.SmugglersCard;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.SpaceshipPlance;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SmugglersCardTest {

        @Test
        public void testReward() {
            // Mocks
            Deck mockDeck = mock(Deck.class);
            Player mockPlayer = mock(Player.class);
            SpaceshipPlance mockPlance = mock(SpaceshipPlance.class);

            // Simulo cargo
            GoodsBlock[] reward = new GoodsBlock[] { mock(GoodsBlock.class), mock(GoodsBlock.class) };

            // Simulo accesso alla plancia
            when(mockPlayer.getSpaceshipPlance()).thenReturn(mockPlance);

            // Creo la carta
            SmugglersCard card = new SmugglersCard("Smugglers", 2, mockDeck, 3, 1, 2, reward);

            // Act
            card.reward(mockPlayer);

            // Verify
            verify(mockPlance).cargoManagement(reward);
        }

        @Test
        public void testPenalize() {
            // Mocks
            Deck mockDeck = mock(Deck.class);
            Player mockPlayer = mock(Player.class);
            GoodsBlock[] emptyReward = new GoodsBlock[0]; // inutile per questo test

            // Creo la carta
            SmugglersCard card = new SmugglersCard("Smugglers", 2, mockDeck, 3, 1, 2, emptyReward);

            // Act
            card.penalize(mockPlayer);

            // Verify
            verify(mockPlayer).removeMostValuableCargo();
        }
    }


