package it.polimi.ingsw.adventureCards;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.model.adventureCards.AbandonedShipCard;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.SpaceshipPlance;
import org.junit.Before;
import org.junit.Test;

public class AbandonedShipCardTest {

    private AbandonedShipCard card;
    private Player mockPlayer;
    private Deck mockDeck;
    private Flightplance mockFlightPlance;
    private SpaceshipPlance mockSpaceshipPlance;

    @Before
    public void setUp() {
        // Crea la carta con 1 giorni persi, 2 membri equipaggio persi, 3 crediti guadagnati
        card = new AbandonedShipCard("Abandoned Ship", 1, 1, 2, 3);

        // Mock delle dipendenze
        mockPlayer = mock(Player.class);
        mockDeck = mock(Deck.class);
        mockFlightPlance = mock(Flightplance.class);
        mockSpaceshipPlance = mock(SpaceshipPlance.class);

        when(mockPlayer.getSpaceshipPlance()).thenReturn(mockSpaceshipPlance);

        // Iniettiamo il mock della plancia nel deck
        when(mockDeck.getFlightPlance()).thenReturn(mockFlightPlance);

        // Impostiamo il deck nella superclasse
        card.setDeck(mockDeck);

        // Impostiamo il giocatore attivato
        card.setActivatedPlayer(mockPlayer);
    }

    @Test
    public void testActivate_addsCreditsAndMovesBackDays() {
        // Simula che il giocatore abbia inizialmente 10 crediti
        when(mockPlayer.getCredits()).thenReturn(10);

        // Attiva la carta
        card.activate();

        // Verifica che i crediti siano aggiornati correttamente
        verify(mockPlayer).setCredits(13); // 10 + 3

        // Verifica che il giocatore venga spostato indietro di 2 giorni
        verify(mockFlightPlance).move(-1, mockPlayer);
    }

    @Test
    public void testCheckCondition_returnsTrue_whenEnoughCrew() {
        // Crew is 3, which is >= lostCrew (2)
        when(mockSpaceshipPlance.getCrew()).thenReturn(3);

        assertTrue(card.checkCondition(mockPlayer));
    }

    @Test
    public void testCheckCondition_returnsTrue_whenExactlyEnoughCrew() {
        // Crew is exactly 2
        when(mockSpaceshipPlance.getCrew()).thenReturn(2);

        assertTrue(card.checkCondition(mockPlayer));
    }

    @Test
    public void testCheckCondition_returnsFalse_whenNotEnoughCrew() {
        // Crew is 1, which is < lostCrew (2)
        when(mockSpaceshipPlance.getCrew()).thenReturn(1);

        assertFalse(card.checkCondition(mockPlayer));
    }

}