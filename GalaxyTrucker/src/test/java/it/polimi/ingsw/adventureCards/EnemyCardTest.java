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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnemyCardActivateTest {

    private Deck deck;
    private Flightplance flightPlance;
    private Game game;
    private Player player1;
    private Player player2;

    @Before
    public void setUp() {
        deck = mock(Deck.class);
        flightPlance = mock(Flightplance.class);
        game = mock(Game.class);
        player1 = mock(Player.class);
        player2 = mock(Player.class);

        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);
    }

    @Test
    public void testActivate_FirstPlayerWins() {
        when(player1.getFireStrenght()).thenReturn(6f); // > 5
        when(player2.getFireStrenght()).thenReturn(2f);
        when(game.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(player1, player2)));

        EnemyCard card = Mockito.spy(new DummyEnemyCard("Enemy", 1, deck, 5, 3));
        card.activate();

        verify(card).reward(player1);
        verify(flightPlance).move(-3, player1);
        verify(card, never()).penalize(any());
    }

    @Test
    public void testActivate_FirstLoses_SecondWins() {
        when(player1.getFireStrenght()).thenReturn(2f); // < 5
        when(player2.getFireStrenght()).thenReturn(6f); // > 5
        when(game.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(player1, player2)));

        EnemyCard card = Mockito.spy(new DummyEnemyCard("Enemy", 1, deck, 5, 2));
        card.activate();

        verify(card).penalize(player1);
        verify(card).reward(player2);
        verify(flightPlance).move(-2, player2);
    }

    @Test
    public void testActivate_AllTie() {
        when(player1.getFireStrenght()).thenReturn(5f); // == cannonStrength
        when(player2.getFireStrenght()).thenReturn(5f);
        when(game.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(player1, player2)));

        EnemyCard card = Mockito.spy(new DummyEnemyCard("Enemy", 1, deck, 5, 1));
        card.activate();

        verify(card, never()).reward(any());
        verify(card, never()).penalize(any());
        verify(flightPlance, never()).move(anyInt(), any());
    }