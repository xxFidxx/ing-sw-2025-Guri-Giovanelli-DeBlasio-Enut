package it.polimi.ingsw.model.game;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.*;

public class FlightplanceTest {

    private Flightplance flightplance;

    @Mock
    private Game gameMock;

    @Mock
    private Player chosenPlayer;

    @Mock
    private Placeholder chosenPlaceholder;

    @Mock
    private Player otherPlayer1;
    @Mock
    private Player otherPlayer2;

    @Mock
    private Placeholder placeholder1;
    @Mock
    private Placeholder placeholder2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Prepara i mock dei giocatori
        when(chosenPlayer.getPlaceholder()).thenReturn(chosenPlaceholder);
        when(otherPlayer1.getPlaceholder()).thenReturn(placeholder1);
        when(otherPlayer2.getPlaceholder()).thenReturn(placeholder2);

        // Costruisce la lista dei giocatori
        ArrayList<Player> players = new ArrayList<>(Arrays.asList(chosenPlayer, otherPlayer1, otherPlayer2));

        // Imposta il comportamento di game.getPlayers()
        when(gameMock.getPlayers()).thenReturn(players);

        // Crea l’istanza di Flightplance usando il costruttore reale
        flightplance = new Flightplance(4, gameMock, players); // 10 è il numero massimo di placeholder
    }

    @Test
    public void testMoveForwardWithExtraSteps() {
        when(chosenPlaceholder.getPosizione()).thenReturn(2);
        when(placeholder1.getPosizione()).thenReturn(3); // +1 step
        when(placeholder2.getPosizione()).thenReturn(5); // +1 step

        flightplance.move(3, chosenPlayer); // 3 + 2 step = 5

        verify(chosenPlaceholder).setPosizione(2 + 3 + 2); // 7
        verify(gameMock).orderPlayers();
    }

    @Test
    public void testMoveBackwardWithExtraSteps() {
        when(chosenPlaceholder.getPosizione()).thenReturn(5);
        when(placeholder1.getPosizione()).thenReturn(3); // -1 step
        when(placeholder2.getPosizione()).thenReturn(2); // -1 step

        flightplance.move(-3, chosenPlayer); // -3 -2 = -5

        verify(chosenPlaceholder).setPosizione(5 - 3 - 2); // 0
        verify(gameMock).orderPlayers();
    }

    @Test
    public void testMoveWithNoExtraSteps() {
        when(chosenPlaceholder.getPosizione()).thenReturn(1);
        when(placeholder1.getPosizione()).thenReturn(10);
        when(placeholder2.getPosizione()).thenReturn(0);

        flightplance.move(3, chosenPlayer); // solo +3

        verify(chosenPlaceholder).setPosizione(1 + 3); // 4
        verify(gameMock).orderPlayers();
    }
}

