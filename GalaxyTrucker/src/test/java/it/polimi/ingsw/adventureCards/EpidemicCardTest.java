package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.EpidemicCard;
import it.polimi.ingsw.model.componentTiles.Cabin;
import it.polimi.ingsw.model.game.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Arrays;

public class EpidemicCardTest {

    private Deck deck;
    private Flightplance flightPlance;
    private Game game;
    private Player player1;
    private Player player2;
    private SpaceshipPlance ship1;
    private SpaceshipPlance ship2;
    private Cabin cabin1, cabin2, cabin3;

    @Before
    public void setUp() {
        deck = mock(Deck.class);
        flightPlance = mock(Flightplance.class);
        game = mock(Game.class);
        player1 = mock(Player.class);
        player2 = mock(Player.class);
        ship1 = mock(SpaceshipPlance.class);
        ship2 = mock(SpaceshipPlance.class);
        cabin1 = mock(Cabin.class);
        cabin2 = mock(Cabin.class);
        cabin3 = mock(Cabin.class);

        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);

        // Setup cabin data
        when(player1.getSpaceshipPlance()).thenReturn(ship1);
        when(player2.getSpaceshipPlance()).thenReturn(ship2);

        when(ship1.getConnectedCabins()).thenReturn(new ArrayList<>(Arrays.asList(cabin1, cabin2)));
        when(ship2.getConnectedCabins()).thenReturn(new ArrayList<>(Arrays.asList(cabin3)));

        when(game.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(player1, player2)));
    }

    @Test
    public void testActivate_shouldRemoveCrewFromAllConnectedCabins() {
        EpidemicCard card = new EpidemicCard("Epidemic", 1, deck);

        card.activate();

        // Verifica che siano state chiamate le rimozioni per ogni cabina connessa
        verify(player1).askRemoveCrew(cabin1);
        verify(player1).askRemoveCrew(cabin2);
        verify(player2).askRemoveCrew(cabin3);
    }
}
