package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.data.DataString;
import it.polimi.ingsw.model.adventureCards.*;
import it.polimi.ingsw.model.componentTiles.*;
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

    }

    @Test
    public void testDrawCard_normalCard() {
        Mockito.doNothing().when(controller).manageCard();

        // Mock della carta avventura
        card = mock(AdventureCard.class);
        when(card.getName()).thenReturn("Pirates");
        when(card.getLevel()).thenReturn(1);

        controller.setCards(Collections.singletonList(card));

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

    @Test
    public void testDrawCard_skippedCombatZone() {
        // Imposta un solo giocatore
        controller.setPlayers(new ArrayList<>(Collections.singletonList(player1)));
        controller.addRealListeners(Collections.singletonList(listener1));
        controller.addPlayerListenerPair(listener1, player1);

        // Crea una carta CombatZoneCard mockata
        AdventureCard combatCard = mock(CombatZoneCard.class);
        when(combatCard.getName()).thenReturn("Combat");
        when(combatCard.getLevel()).thenReturn(1);

        // Imposta la lista delle carte
        controller.setCards(Collections.singletonList(combatCard));

        Mockito.doNothing().when(controller).resetShowAndDraw();

        // Esegui drawCard()
        controller.drawCard();

        // Verifica che venga notificato SKIPPED_CARD
        verify(listener1, atLeastOnce()).onEvent(argThat(event ->
                event.getState() == GameState.SKIPPED_CARD
        ));
    }


    @Test
    public void testTakeHit_HitComponentFromNorth() {
        // Setup: griglia 5x7 con un componente nella traiettoria
        ComponentTile[][] mockGrid = new ComponentTile[5][7];
        Cabin mockedCabin = mock(Cabin.class);
        mockGrid[1][1] = mockedCabin; // componente colpibile a y=1, x=1

        // Configura spaceship mock
        when(spaceship.getComponents()).thenReturn(mockGrid);

        // Posizione di partenza: position 5 da NORD => x=1, y=0, direzione NORD
        controller.takeHit(player1, Direction.NORTH, 5);

        // Verifica che l'evento SHOT_HIT sia stato inviato
        verify(listener1).onEvent(argThat(event -> event.getState().equals(GameState.SHOT_HIT)));

        // Verifica che il metodo removeAdjust sia stato invocato per rimuovere il componente
        verify(controller).removeAdjust(eq(listener1), eq(1), eq(1)); // x=1, y=1
    }

    @Test
    public void testManageCard_abandonedShip_conditionMet() {
        // Setup carta AbandonedShipCard mockata
        AbandonedShipCard card = mock(AbandonedShipCard.class);
        when(card.checkCondition(player1)).thenReturn(true);
        controller.setCurrentAdventureCard(card);

        // Stubbo handleWaitersPlayer per non testarla ora
        doNothing().when(controller).handleWaitersPlayer(listener1);

        // Imposto i listener e giocatori temporanei
        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        // Eseguo il metodo
        controller.manageCard();

        // Verifico che checkCondition sia stata chiamata
        verify(card).checkCondition(player1);

        // Verifica che il giocatore sia stato rimosso
        assertFalse(controller.getTmpPlayers().contains(player1));

        // Verifico che handleWaitersPlayer sia stata chiamata
        verify(controller).handleWaitersPlayer(listener1);
    }

    @Test
    public void testManageCard_abandonedShip_conditionNotMet() {
        // Setup carta AbandonedShipCard mockata
        AbandonedShipCard card = mock(AbandonedShipCard.class);
        when(card.checkCondition(player1)).thenReturn(false);
        controller.setCurrentAdventureCard(card);

        // Imposto tmpPlayers e mappo listener
        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        doNothing().when(controller).resetShowAndDraw();

        // Eseguo il metodo
        controller.manageCard();

        // Verifico che venga notificato FAILED_CARD
        verify(listener1).onEvent(argThat(event ->
                event.getState() == GameState.FAILED_CARD
        ));

        // Verifica che il giocatore sia stato rimosso
        assertFalse(controller.getTmpPlayers().contains(player1));
    }

    @Test
    public void testManageCard_abandonedStation_conditionMet() {
        // Creo e imposto la carta AbandonedStationCard
        AbandonedStationCard card = mock(AbandonedStationCard.class);
        when(card.checkCondition(player1)).thenReturn(true);
        controller.setCurrentAdventureCard(card);

        // Imposto i listener e giocatori temporanei
        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        // Stubbo handleWaitersPlayer per evitare effetti collaterali
        doNothing().when(controller).handleWaitersPlayer(listener1);

        // Eseguo il metodo da testare
        controller.manageCard();

        // Verifico che sia stato chiamato handleWaitersPlayer
        verify(controller).handleWaitersPlayer(listener1);

        // Verifico che il giocatore sia stato rimosso dalla lista temporanea
        assertFalse(controller.getTmpPlayers().contains(player1));

        // Verifico che NON sia stato notificato lo stato FAILED_CARD
        verify(listener1, never()).onEvent(argThat(event ->
                event.getState() == GameState.FAILED_CARD
        ));
    }

    @Test
    public void testManageCard_abandonedStation_conditionNotMet() {
        // Creo e imposto la carta AbandonedStationCard
        AbandonedStationCard card = mock(AbandonedStationCard.class);
        when(card.checkCondition(player1)).thenReturn(false);
        controller.setCurrentAdventureCard(card);

        // Imposto i listener e giocatori temporanei
        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        doNothing().when(controller).resetShowAndDraw();

        // Esegui il metodo da testare
        controller.manageCard();

        // Verifico che il giocatore sia stato notificato con FAILED_CARD
        verify(listener1).onEvent(argThat(event ->
                event.getState() == GameState.FAILED_CARD
        ));

        // Verifico che il giocatore sia stato rimosso
        assertFalse(controller.getTmpPlayers().contains(player1));
    }

    @Test
    public void testManageCard_openSpace_noEngines() {
        // Carta mockata
        OpenSpaceCard card = mock(OpenSpaceCard.class);
        controller.setCurrentAdventureCard(card);

        // Nessun motore
        when(spaceship.getEngines()).thenReturn(new ArrayList<>());

        // Imposta il giocatore
        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        // Stub metodi di flusso
        doNothing().when(controller).resetShowAndDraw();

        // Esegui
        controller.manageCard();

        // Verifica comportamento
        verify(controller).handleEarlyEnd(player1);

        // Verifico che il giocatore sia stato rimosso
        assertFalse(controller.getTmpPlayers().contains(player1));
    }

    @Test
    public void testManageCard_openSpace_singleEngines() {
        OpenSpaceCard card = mock(OpenSpaceCard.class);
        controller.setCurrentAdventureCard(card);

        // Mock 2 motori singoli
        Engine e1 = mock(Engine.class);
        Engine e2 = mock(Engine.class);
        when(spaceship.getEngines()).thenReturn(new ArrayList<>(List.of(e1, e2)));

        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        // Stub metodo chiamato
        doNothing().when(controller).fromChargeToManage(listener1);

        controller.manageCard();

        verify(controller).fromChargeToManage(listener1);
        assertFalse(controller.getTmpPlayers().contains(player1));
    }

    @Test
    public void testManageCard_openSpace_withDoubleEngine() {
        OpenSpaceCard card = mock(OpenSpaceCard.class);
        controller.setCurrentAdventureCard(card);

        // 1 motore singolo, 1 doppio
        Engine e1 = mock(Engine.class);
        Engine de = mock(DoubleEngine.class);
        when(spaceship.getEngines()).thenReturn(new ArrayList<>(List.of(e1, de)));

        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        doNothing().when(controller).handleWaitersBattery(listener1, player1);

        controller.manageCard();

        verify(controller).handleWaitersBattery(listener1, player1);
        assertFalse(controller.getTmpPlayers().contains(player1));
    }








}

