package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.OpenSpaceCard;
import it.polimi.ingsw.model.game.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class OpenSpaceCardTest {

    private Deck deck;
    private Flightplance flightPlance;
    private Game game;
    private Player player1, player2, player3;
    private Placeholder placeholder1, placeholder2, placeholder3;

    @Before
    public void setUp() {
        // Mocks
        deck = mock(Deck.class);
        flightPlance = mock(Flightplance.class);
        game = mock(Game.class);
        player1 = mock(Player.class);
        player2 = mock(Player.class);
        player3 = mock(Player.class);
        placeholder1 = mock(Placeholder.class);
        placeholder2 = mock(Placeholder.class);
        placeholder3 = mock(Placeholder.class);

        // Comportamenti mock
        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);
        when(game.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(player1, player2, player3)));

        // Posizioni: player3 (pos 2), player2 (pos 1), player1 (pos 0) → ordine di rotta
        when(player1.getPlaceholder()).thenReturn(placeholder1);
        when(player2.getPlaceholder()).thenReturn(placeholder2);
        when(player3.getPlaceholder()).thenReturn(placeholder3);

        when(placeholder1.getPosizione()).thenReturn(0);
        when(placeholder2.getPosizione()).thenReturn(1);
        when(placeholder3.getPosizione()).thenReturn(2);

        // Potenza motrice
        when(player1.getEngineStrenght()).thenReturn(2);
        when(player2.getEngineStrenght()).thenReturn(3);
        when(player3.getEngineStrenght()).thenReturn(1);
    }

    @Test
    public void testActivate_shouldMovePlayersAccordingToRouteOrderAndPower() {
        OpenSpaceCard card = new OpenSpaceCard("Open Space", 1, deck);

        card.activate();

        // Verifica l’ordine: player3, player2, player1 (in base alla rotta)
        verify(flightPlance).move(1, player3); // player3 ha pos 2 e power 1
        verify(flightPlance).move(3, player2); // player2 ha pos 1 e power 3
        verify(flightPlance).move(2, player1); // player1 ha pos 0 e power 2
    }
}
