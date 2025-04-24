package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.game.*;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class StardustCardTest {
    private Deck deck;
    private Flightplance flightPlance;
    private Game game;
    private Player player1;
    private Player player2;
    private SpaceshipPlance spaceshipPlance;

    @Before
    public void setUp(){
        deck=mock(Deck.class);
        flightPlance=mock(Flightplance.class);
        game=mock(Game.class);
        player1=mock(Player.class);
        player2=mock(Player.class);
        spaceshipPlance=mock(SpaceshipPlance.class);

    }

}
