import it.polimi.ingsw.model.adventureCards.AbandonedShipCard;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import static org.mockito.Mockito.*;
import org.junit.Test;
import static org.junit.Assert.*;


public class AbandonedShipCardTest {

    @Test
    public void testActivate() {
        // Mocks
        Game mockGame = mock(Game.class);
        Flightplance mockPlance = mock(Flightplance.class);
        Deck mockDeck = mock(Deck.class);
        Player mockPlayer = mock(Player.class);

        // Simulazioni
        when(mockDeck.getFlightPlance()).thenReturn(mockPlance);
        when(mockPlance.getGame()).thenReturn(mockGame);
        when(mockGame.choosePlayer(any())).thenReturn(mockPlayer);

        when(mockPlayer.getNumEquip()).thenReturn(2);
        when(mockPlayer.getCredits()).thenReturn(3);

        // Creo la carta (lostDays = 2, lostCrew = 1, credits = 3)
        AbandonedShipCard card = new AbandonedShipCard("Nave", 1, 1, 3, 4, mockDeck);

        // Act
        card.activate();

        // Verify chiamate
        verify(mockGame).choosePlayer(card);
        verify(mockPlayer).setNumEquip(0);     // 2 - 3 = -1 saturato a 0
        verify(mockPlayer).setCredits(7);      // 3 + 4 = 7
        verify(mockPlance).move(-1, mockPlayer); // lostDays = 1
    }
}