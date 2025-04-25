package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.EnemyCard;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class EnemyCardTest {
    private Deck deck;
    private Flightplance flightPlance;
    private Game game;
    private Player strongPlayer;
    private Player weakPlayer;
    private DummyEnemyCard card;

    @Before
    public void setUp() {
        deck = mock(Deck.class);
        flightPlance = mock(Flightplance.class);
        game = mock(Game.class);
        strongPlayer = mock(Player.class);
        weakPlayer = mock(Player.class);

        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);
        doNothing().when(game).orderPlayers();


        ArrayList<Player> players = new ArrayList<>();
        players.add(weakPlayer);
        players.add(strongPlayer);
        when(game.getPlayers()).thenReturn(players);

        when(strongPlayer.getFireStrenght()).thenReturn(5f);
        when(weakPlayer.getFireStrenght()).thenReturn(2f);

        card = new DummyEnemyCard("Dummy Enemy", 1, deck, 3, 1); // cannonStrength = 3
    }

    @Test
    public void testActivate_playerWins_shouldCallReward() {
        card.activate();

        assertTrue(card.rewardCalled);
        assertFalse(card.penalizeCalled);

        verify(flightPlance).move(-1, strongPlayer);
    }

    @Test
    public void testActivate_playersLose_shouldCallPenalize() {
        // Tutti i player sono deboli
        when(strongPlayer.getFireStrenght()).thenReturn(1f);
        when(weakPlayer.getFireStrenght()).thenReturn(2f);

        card = new DummyEnemyCard("Dummy Enemy", 1, deck, 5, 1); // cannonStrength = 5

        card.activate();

        assertFalse(card.rewardCalled);
        assertTrue(card.penalizeCalled);
    }

    @Test
    public void testActivate_playersTie_shouldDoNothing() {
        // Entrambi pareggiano con cannonStrength = 3
        when(strongPlayer.getFireStrenght()).thenReturn(3f);
        when(weakPlayer.getFireStrenght()).thenReturn(3f);

        card = new DummyEnemyCard("Dummy Enemy", 1, deck, 3, 1);

        card.activate();

        assertFalse(card.rewardCalled);
        assertFalse(card.penalizeCalled);
    }

    /*@Test
    public void testActivate_firstPlayerLoses_secondPlayerWins() {
        when(strongPlayer.getFireStrenght()).thenReturn(3f);
        when(weakPlayer.getFireStrenght()).thenReturn(7f);

        card = new DummyEnemyCard("Dummy Enemy", 1, deck, 5, 1);

        card.activate();

        verify(card).penalize(strongPlayer);
        verify(card).reward(weakPlayer);
        verify(flightPlance).move(-1, weakPlayer);
    }*/
}
