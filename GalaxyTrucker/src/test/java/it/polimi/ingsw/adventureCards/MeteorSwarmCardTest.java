package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.MeteorSwarmCard;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.resources.Projectile;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MeteorSwarmCardTest {

    private Deck deck;
    private Flightplance flightPlance;
    private Game game;
    private Player player1;
    private Player player2;
    private Projectile meteor1;
    private Projectile meteor2;

    @Before
    public void setUp() {
        deck = mock(Deck.class);
        flightPlance = mock(Flightplance.class);
        game = mock(Game.class);
        player1 = mock(Player.class);
        player2 = mock(Player.class);
        meteor1 = mock(Projectile.class);
        meteor2 = mock(Projectile.class);

        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);

        when(game.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(player1, player2)));

        // Restituisce un valore di dado fisso per semplicità
        when(game.throwDices()).thenReturn(3);
    }

    @Test
    public void testActivate_shouldActivateAllMeteorsForAllPlayers() {
        Projectile[] meteors = new Projectile[] { meteor1, meteor2 };
        MeteorSwarmCard card = new MeteorSwarmCard("Meteor Swarm", 1, meteors, deck);

        card.activate();

        // Verifica che ogni meteorite sia attivato per ogni giocatore
        verify(meteor1).activate(player1, 3);
        verify(meteor1).activate(player2, 3);
        verify(meteor2).activate(player1, 3);
        verify(meteor2).activate(player2, 3);

        // Verifica che i dadi siano stati lanciati due volte (una per meteorite)
        verify(game, times(2)).throwDices();
    }
}
