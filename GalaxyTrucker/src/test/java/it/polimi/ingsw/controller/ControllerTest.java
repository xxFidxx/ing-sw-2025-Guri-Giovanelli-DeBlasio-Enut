package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.data.DataString;
import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.game.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

public class ControllerTest {
    private Controller controller;
    private Player player1, player2;
    private ClientListener listener1, listener2;
    private AdventureCard card;
    private SpaceshipPlance spaceship;

    @Before
    public void setUp() {
        //controller = new Controller();
        controller = Mockito.spy(new Controller());

        // Mock dei giocatori
        player1 = mock(Player.class);
        player2 = mock(Player.class);

        // Mock dei placeholder
        Placeholder placeholder1 = mock(Placeholder.class);
        when(player1.getPlaceholder()).thenReturn(placeholder1);
        when(placeholder1.getPosizione()).thenReturn(5);  // una posizione valida
        when(placeholder1.getColor()).thenReturn(ColorType.RED); // o qualunque colore tu usi

        Placeholder placeholder2 = mock(Placeholder.class);
        when(player2.getPlaceholder()).thenReturn(placeholder2);
        when(placeholder2.getPosizione()).thenReturn(10);
        when(placeholder2.getColor()).thenReturn(ColorType.BLUE);

        // Mock delle navi
        spaceship = mock(SpaceshipPlance.class);
        when(player1.getSpaceshipPlance()).thenReturn(spaceship);
        when(player2.getSpaceshipPlance()).thenReturn(spaceship);

        // Mock dei listener
        listener1 = mock(ClientListener.class);
        listener2 = mock(ClientListener.class);

        // Aggiungi ai map
        controller.addPlayerListenerPair(listener1, player1);
        controller.addPlayerListenerPair(listener2, player2);

        // Aggiungi alla lista giocatori
        controller.setPlayers(new ArrayList<>(Arrays.asList(player1, player2)));
        controller.addRealListeners(Arrays.asList(listener1, listener2));

        // Mock della carta avventura
        card = mock(AdventureCard.class);
        when(card.getName()).thenReturn("Pirates");
        when(card.getLevel()).thenReturn(1);

        controller.setCards(Collections.singletonList(card));

    }

    @Test
    public void testDrawCard_normalCard() {

        Mockito.doNothing().when(controller).manageCard();

        controller.drawCard();

        verify(listener1).onEvent(argThat(event -> event.getState() == GameState.TURN_START));
        verify(listener2).onEvent(argThat(event -> event.getState() == GameState.TURN_START));

        // Verifica che venga notificata la carta pescata
        verify(listener1).onEvent(argThat(event -> event.getState() == GameState.DRAW_CARD));
        verify(listener2).onEvent(argThat(event -> event.getState() == GameState.DRAW_CARD));


        // Verifica che la carta corrente sia impostata
        assertEquals(card, controller.getCurrentAdventureCard());

        // Verifica che venga chiamato updateLists() su ogni nave
        verify(spaceship, atLeastOnce()).updateLists();
    }

    @Test
    public void testDrawCard_noCardsLeft() {
        Game gameMock = Mockito.mock(Game.class);
        when(gameMock.getEndStats()).thenReturn("test stats");

        controller.setGame(gameMock);
        controller.setCards(new LinkedList<>());  // lista vuota
        controller.addRealListener(listener1); // aggiungi listener per la notifica

        controller.drawCard();

        // Verifica che venga notificato l'evento END_GAME
        verify(listener1, atLeastOnce()).onEvent(argThat(event ->
                event.getState() == GameState.END_GAME &&
                        event.getData() instanceof DataString &&
                        ((DataString) event.getData()).getText().equals("test stats")
        ));
    }



}

