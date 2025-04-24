package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.StardustCard;
import it.polimi.ingsw.model.game.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class StardustCardTest {
    private Deck deck;
    private Flightplance flightPlance;
    private Game game;
    private Player player1;
    private Player player2;
    private SpaceshipPlance spaceshipPlance1,spaceshipPlance2;

    @Before
    public void setUp(){
        deck=mock(Deck.class);
        flightPlance=mock(Flightplance.class);
        game=mock(Game.class);
        player1=mock(Player.class);
        player2=mock(Player.class);
        spaceshipPlance1=mock(SpaceshipPlance.class);
        spaceshipPlance2=mock(SpaceshipPlance.class);

        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);
        when(game.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(player1,player2)));

        when(player1.getSpaceshipPlance()).thenReturn(spaceshipPlance1);
        when(player2.getSpaceshipPlance()).thenReturn(spaceshipPlance2);

        when(spaceshipPlance1.countExposedConnectors()).thenReturn(1);
        when(spaceshipPlance2.countExposedConnectors()).thenReturn(3);

    }

    @Test
    public void testActivate_ShouldMovePlayerBackByExposedConnectors() {

        StardustCard card = new StardustCard("StardustCard", 3, 0, deck);

        card.activate();

        verify(flightPlance).move(-1,player1);
        verify(flightPlance).move(-3,player2);


    }

}
