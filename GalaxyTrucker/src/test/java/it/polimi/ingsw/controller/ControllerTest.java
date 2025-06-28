package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.Event;
import it.polimi.ingsw.controller.network.Lobby;
import it.polimi.ingsw.controller.network.data.DataString;
import it.polimi.ingsw.model.adventureCards.*;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.game.Timer;
import it.polimi.ingsw.model.resources.CombatZoneType;
import it.polimi.ingsw.model.resources.Planet;
import it.polimi.ingsw.model.resources.Projectile;
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
    private Lobby lobby;

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
    public void testHandlePlanets_AllPlayersDone_TriggersReset() {
        // Prepara lo stato: tutti i giocatori hanno isDone = true
        controller.isDone.put(player1, true);
        controller.isDone.put(player2, true);

        // mocka resetShowAndDraw per vedere se viene chiamato
        doNothing().when(controller).resetShowAndDraw();

        // chiama il metodo
        controller.handlePlanets(listener1);

        // deve aver aggiornato lastMethodCalled
        assertEquals("handlePlanets", controller.getLastMethodCalled());

        // deve aver chiamato resetShowAndDraw
        verify(controller).resetShowAndDraw();

        // non deve aver chiamato manageCard (quindi nessun evento wait dovrebbe partire)
        verify(listener1, never()).onEvent(any());
    }
    @Test
    public void testHandleWaitersPlayer_CorrectEventsDispatched() {
        // Chiamata del metodo con listener1 (quello associato a player1)
        controller.handleWaitersPlayer(listener1);

        // Verifica che player1 riceve CHOOSE_PLAYER
        verify(listener1).onEvent(argThat(event ->
                event.getState().equals(GameState.CHOOSE_PLAYER)));

        // Verifica che player2 riceve WAIT_PLAYER
        verify(listener2).onEvent(argThat(event ->
                event.getState().equals(GameState.WAIT_PLAYER)));

        // Verifica che il campo lastMethodCalled sia stato aggiornato
        assertEquals("handleWaitersPlayer", controller.getLastMethodCalled());
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

    @Test
    public void testManageCard_slaversCard_playerConnected() {
        // Mock della carta SlaversCard
        SlaversCard card = mock(SlaversCard.class);
        controller.setCurrentAdventureCard(card);

        // Imposto i giocatori
        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        // Stub metodi di flusso
        doNothing().when(controller).handleWaitersEnemy(listener1);

        // Eseguo il metodo
        controller.manageCard();

        // Verifico che venga chiamato handleWaitersEnemy
        verify(controller).handleWaitersEnemy(listener1);
        // Verifico che il giocatore sia stato rimosso
        assertFalse(controller.getTmpPlayers().contains(player1));
    }

    @Test
    public void testManageCard_slaversCard_enemyAlreadyDefeated() {
        SlaversCard card = mock(SlaversCard.class);
        controller.setCurrentAdventureCard(card);

        controller.setTmpPlayers(new ArrayList<>());
        // Imposta la condizione enemy già sconfitto
        controller.setEnemyDefeated(); // lo setta a TRUE

        // Stub metodo
        doNothing().when(controller).defeatedBySlavers();

        controller.manageCard();

        verify(controller).defeatedBySlavers();
    }

    @Test
    public void testManageCard_smugglersCard_activePlayerHandled() {
        SmugglersCard card = mock(SmugglersCard.class);
        controller.setCurrentAdventureCard(card);

        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        // Stub dei metodi usati
        doNothing().when(controller).handleWaitersEnemy(listener1);

        // Esegui
        controller.manageCard();

        // Verifica che il giocatore sia stato rimosso
        assertFalse(controller.getTmpPlayers().contains(player1));

        // Verifica che sia stato invocato il metodo di gestione
        verify(controller).handleWaitersEnemy(listener1);
    }

    @Test
    public void testManageCard_smugglersCard_cargoEnded() {
        SmugglersCard card = mock(SmugglersCard.class);
        controller.setCurrentAdventureCard(card);

        // Simula la condizione in cui il cargo è terminato
        controller.setCargoended(); //a TRUE

        controller.setTmpPlayers(new ArrayList<>());

        // Stub del metodo invocato
        doNothing().when(controller).defeatedBySmugglers();

        // Esegui
        controller.manageCard();

        // Verifica che venga invocato il comportamento corretto
        verify(controller).defeatedBySmugglers();

        // Verifica che il flag sia stato aggiornato
        assertTrue(controller.getSmugglersFlag());

        // Verifica che cargoended sia stato azzerato
        assertFalse(controller.getCargoended());
    }

    @Test
    public void testManageCard_piratesCard_activePlayerHandled() {
        PiratesCard card = mock(PiratesCard.class);
        controller.setCurrentAdventureCard(card);

        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        // Stub
        doNothing().when(controller).handleWaitersEnemy(listener1);

        // Esegui
        controller.manageCard();

        // Verifica
        verify(controller).handleWaitersEnemy(listener1);
        assertFalse(controller.getTmpPlayers().contains(player1));
    }

    @Test
    public void testManageCard_piratesCard_piratesEnded() {
        PiratesCard card = mock(PiratesCard.class);
        controller.setCurrentAdventureCard(card);

        controller.setPiratesended(); // a TRUE
        controller.setTmpPlayers(new ArrayList<>());

        // Aggiungi un giocatore sconfitto
        controller.setDefeatedPlayers(new ArrayList<>(Collections.singleton(player1)));

        // Stub del metodo invocato
        doNothing().when(controller).defeatedByPirates();

        // Esegui
        controller.manageCard();

        // Verifica chiamata al metodo e aggiornamento flag
        verify(controller).defeatedByPirates();
        assertFalse(controller.getPiratesended());
        assertTrue(controller.getPiratesFlag());

        // Verifica che venga inserito nella mappa isDonePirates
        assertTrue(controller.isDonePirates.containsKey(player1));
        assertFalse(controller.isDonePirates.get(player1));
    }

    @Test
    public void testManageCard_planetsCard_withFreePlanets() {
        Game game = Mockito.mock(Game.class);
        controller.setGame(game);
        PlanetsCard card = mock(PlanetsCard.class);
        controller.setCurrentAdventureCard(card);

        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));

        // Stub comportamento della carta
        ArrayList<Planet> planets = new ArrayList<>(List.of(mock(Planet.class), mock(Planet.class)));
        when(card.getPlanets()).thenReturn(planets);
        when(game.freePlanets(card, planets)).thenReturn(true);

        // Stub del metodo invocato
        doNothing().when(controller).handleWaitersPlanets(player1);

        // Esegui
        controller.manageCard();

        // Verifica
        verify(controller).handleWaitersPlanets(player1);
    }

    @Test
    public void testManageCard_planetsCard_noFreePlanets() {
        Game game = Mockito.mock(Game.class);
        controller.setGame(game);
        PlanetsCard card = mock(PlanetsCard.class);
        controller.setCurrentAdventureCard(card);

        controller.setTmpPlayers(new ArrayList<>(List.of(player1)));
        controller.addPlayerListenerPair(listener1, player1);

        // Stub comportamento della carta
        ArrayList<Planet> planets = new ArrayList<>(List.of(mock(Planet.class), mock(Planet.class)));
        when(card.getPlanets()).thenReturn(planets);
        when(game.freePlanets(card, planets)).thenReturn(false);

        // Stub metodo chiamato
        doNothing().when(controller).handlePlanets(listener1);

        // Esegui
        controller.manageCard();

        // Verifiche
        verify(controller).handlePlanets(listener1);
        assertFalse(controller.getTmpPlayers().contains(player1));
    }

    @Test
    public void testManageCard_meteorSwarmCard_allNullProjectiles() {
        MeteorSwarmCard card = mock(MeteorSwarmCard.class);
        controller.setCurrentAdventureCard(card);

        // Stub array di soli null
        Projectile[] meteors = new Projectile[4];
        when(card.getMeteors()).thenReturn(meteors);

        // Stub reset
        doNothing().when(controller).resetShowAndDraw();

        // Esegui
        controller.manageCard();

        // Verifica
        verify(controller).resetShowAndDraw();
    }

    @Test
    public void testManageCard_meteorSwarmCard_withProjectiles() {
        MeteorSwarmCard card = mock(MeteorSwarmCard.class);
        controller.setCurrentAdventureCard(card);

        // Stub array con almeno un proiettile valido
        Projectile projectile = mock(Projectile.class);
        Projectile[] meteors = new Projectile[] { projectile, null, null };
        when(card.getMeteors()).thenReturn(meteors);

        // Stub game
        Game game = mock(Game.class);
        controller.setGame(game);
        when(game.throwDices()).thenReturn(7);  // qualunque valore

        // Stub lista giocatori
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        Player p3 = mock(Player.class);
        Player p4 = mock(Player.class);
        controller.setPlayers(new ArrayList<>(List.of(p1, p2, p3, p4)));

        // Stub activateMeteor
        doNothing().when(controller).activateMeteor(p1);
        doNothing().when(controller).activateMeteor(p2);
        doNothing().when(controller).activateMeteor(p3);
        doNothing().when(controller).activateMeteor(p4);

        // Esegui
        controller.manageCard();

        // Verifica chiamate
        verify(controller).activateMeteor(p1);
        verify(controller).activateMeteor(p2);
        verify(controller).activateMeteor(p3);
        verify(controller).activateMeteor(p4);
    }

    @Test
    public void testManageCard_epidemicCard_sendsEventToAllPlayers() {
        // Crea mock della carta
        EpidemicCard card = mock(EpidemicCard.class);
        controller.setCurrentAdventureCard(card);

        // Crea 3 giocatori e listener mock
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        Player p3 = mock(Player.class);

        ClientListener l1 = mock(ClientListener.class);
        ClientListener l2 = mock(ClientListener.class);
        ClientListener l3 = mock(ClientListener.class);

        // Setta i player nel controller
        controller.setPlayers(new ArrayList<>(List.of(p1, p2, p3)));

        // Registra le associazioni player → listener
        controller.addPlayerListenerPair(l1, p1);
        controller.addPlayerListenerPair(l2, p2);
        controller.addPlayerListenerPair(l3, p3);

        // Stub del metodo eventCrafter (se lo chiami direttamente)
        GameState expectedState = GameState.EPIDEMIC_MANAGEMENT;
        doReturn(mock(Event.class)).when(controller).eventCrafter(eq(expectedState), isNull(), any(Player.class));

        // Esegui
        controller.manageCard();

        // Verifica che ogni listener abbia ricevuto l’evento
        verify(l1).onEvent(any());
        verify(l2).onEvent(any());
        verify(l3).onEvent(any());

        // (opzionale) Verifica che l'evento sia quello giusto per ogni player
        verify(controller).eventCrafter(expectedState, null, p1);
        verify(controller).eventCrafter(expectedState, null, p2);
        verify(controller).eventCrafter(expectedState, null, p3);
    }

    @Test
    public void testManageCard_stardustCard_movesAllPlayersAndSendsEvent() {
        // Mock della carta
        StardustCard card = mock(StardustCard.class);
        controller.setCurrentAdventureCard(card);

        // Mock dei giocatori
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        Player p3 = mock(Player.class); // disconnesso

        // Mock dei rispettivi listener
        ClientListener l1 = mock(ClientListener.class);
        ClientListener l2 = mock(ClientListener.class);

        // Mock dei plance
        SpaceshipPlance sp1 = mock(SpaceshipPlance.class);
        SpaceshipPlance sp2 = mock(SpaceshipPlance.class);
        SpaceshipPlance sp3 = mock(SpaceshipPlance.class);

        // Stub countExposedConnectors
        when(p1.getSpaceshipPlance()).thenReturn(sp1);
        when(p2.getSpaceshipPlance()).thenReturn(sp2);
        when(p3.getSpaceshipPlance()).thenReturn(sp3);

        when(sp1.countExposedConnectors()).thenReturn(2); // p1 ha 2 connettori esposti
        when(sp2.countExposedConnectors()).thenReturn(1); // p2 ha 1 connettore esposto
        when(sp3.countExposedConnectors()).thenReturn(3); // p3 ha 3 connettori esposti

        // Imposta players e disconnessi
        controller.setPlayers(new ArrayList<>(List.of(p1, p2)));
        controller.setDisconnectedPlayers(new ArrayList<>(List.of(p3)));

        controller.addPlayerListenerPair(l1, p1);
        controller.addPlayerListenerPair(l2, p2);

        // Mock Game e Flightplance
        Game game = mock(Game.class);
        Flightplance flightplance = mock(Flightplance.class);
        when(game.getFlightplance()).thenReturn(flightplance);
        controller.setGame(game);

        // Stub eventCrafter e reset
        doReturn(mock(Event.class)).when(controller).eventCrafter(eq(GameState.MOVE_PLAYER), anyInt(), isNull());
        doNothing().when(controller).resetShowAndDraw();

        // Esegui
        controller.manageCard();

        // Verifica invio eventi
        verify(l1).onEvent(any());
        verify(l2).onEvent(any());

        verify(controller).eventCrafter(GameState.MOVE_PLAYER, 2, null);
        verify(controller).eventCrafter(GameState.MOVE_PLAYER, 1, null);

        // Verifica movimento su flightplance
        verify(flightplance).move(-2, p1);
        verify(flightplance).move(-1, p2);
        verify(flightplance).move(-3, p3);

        // Verifica chiamata finale
        verify(controller).resetShowAndDraw();
    }

    @Test
    public void testManageCard_combatZone_lostCrew_withUniqueMin() {
        // Mock della carta
        CombatZoneCard card = mock(CombatZoneCard.class);
        when(card.getType()).thenReturn(CombatZoneType.LOSTCREW);
        when(card.getLostDays()).thenReturn(2);
        controller.setCurrentAdventureCard(card);

        // Mock giocatori
        Player p1 = mock(Player.class); // crew: 3
        Player p2 = mock(Player.class); // crew: 2 → min
        Player p3 = mock(Player.class); // crew: 4

        SpaceshipPlance sp1 = mock(SpaceshipPlance.class);
        SpaceshipPlance sp2 = mock(SpaceshipPlance.class);
        SpaceshipPlance sp3 = mock(SpaceshipPlance.class);

        when(p1.getSpaceshipPlance()).thenReturn(sp1);
        when(p2.getSpaceshipPlance()).thenReturn(sp2);
        when(p3.getSpaceshipPlance()).thenReturn(sp3);

        when(sp1.getCrew()).thenReturn(3);
        when(sp2.getCrew()).thenReturn(2); // minimo
        when(sp3.getCrew()).thenReturn(4);

        controller.setTmpPlayers(new ArrayList<>(List.of(p1, p2, p3)));
        controller.setPlayers(new ArrayList<>(List.of(p1, p2, p3)));

        // Listener
        ClientListener l1 = mock(ClientListener.class);
        ClientListener l2 = mock(ClientListener.class);
        ClientListener l3 = mock(ClientListener.class);

        controller.addPlayerListenerPair(l1, p1);
        controller.addPlayerListenerPair(l2, p2);
        controller.addPlayerListenerPair(l3, p3);

        // Game + Flightplance
        Game game = mock(Game.class);
        Flightplance flightplance = mock(Flightplance.class);
        when(game.getFlightplance()).thenReturn(flightplance);
        controller.setGame(game);

        // Stub eventCrafter
        Event moveEvent = mock(Event.class);
        Event chooseEngineEvent = mock(Event.class);
        doReturn(moveEvent).when(controller).eventCrafter(eq(GameState.MOVE_PLAYER), eq(2), isNull());
        doReturn(chooseEngineEvent).when(controller).eventCrafter(eq(GameState.CHOOSE_ENGINE), isNull(), any());

        // Stub metodo personalizzato
        doNothing().when(controller).handleMinEquip(l2);

        // Esegui
        controller.manageCard();

        // Verifica solo p2 subisce penalità
        verify(controller).handleMinEquip(l2);
        verify(l2).onEvent(moveEvent);
        verify(flightplance).move(-2, p2);

        verify(l1, never()).onEvent(moveEvent);
        verify(l3, never()).onEvent(moveEvent);

        // Verifica che tutti ricevono CHOOSE_ENGINE
        verify(l1).onEvent(chooseEngineEvent);
        verify(l2).onEvent(chooseEngineEvent);
        verify(l3).onEvent(chooseEngineEvent);
    }
    @Test
    public void testHandleMinEquip_SendsCorrectEvents() {
        // Mock degli eventi da restituire dal controller
        Event leastCrewEvent = mock(Event.class);
        when(leastCrewEvent.getState()).thenReturn(GameState.LEAST_CREW);
        Event notMinEquipEvent = mock(Event.class);
        when(notMinEquipEvent.getState()).thenReturn(GameState.NOT_MIN_EQUIP);

        // Fai in modo che eventCrafter restituisca gli eventi giusti
        doReturn(leastCrewEvent)
                .when(controller).eventCrafter(eq(GameState.LEAST_CREW), any(), any());
        doReturn(notMinEquipEvent)
                .when(controller).eventCrafter(eq(GameState.NOT_MIN_EQUIP), any(), any());

        // Chiamo il metodo passando listener1
        controller.handleMinEquip(listener1);

        // listener1 deve ricevere evento LEAST_CREW
        verify(listener1).onEvent(argThat(event ->
                event.getState() == GameState.LEAST_CREW));

        // listener2 deve ricevere evento NOT_MIN_EQUIP
        verify(listener2).onEvent(argThat(event ->
                event.getState() == GameState.NOT_MIN_EQUIP));
    }

    @Test
    public void testPickTile_reserveSpot0_success() throws LobbyExceptions {
        Game game = mock(Game.class);
        controller.setGame(game);

        ComponentTile tile = mock(ComponentTile.class);
        when(game.pickTileReserveSpot(player1, 0)).thenReturn(tile);
        controller.addPlayerListenerPair(listener1, player1);

        controller.pickTile(listener1, 1000);

        verify(listener1).onEvent(argThat(event ->
                event.getState() == GameState.PICK_RESERVED_CARD));
    }

    @Test
    public void testPickTile_reserveSpot1_success() throws LobbyExceptions {
        Game game = mock(Game.class);
        controller.setGame(game);

        ComponentTile tile = mock(ComponentTile.class);
        when(game.pickTileReserveSpot(player1, 1)).thenReturn(tile);
        controller.addPlayerListenerPair(listener1, player1);

        controller.pickTile(listener1, 1001);

        verify(listener1).onEvent(argThat(event ->
                event.getState() == GameState.PICK_RESERVED_CARD ));
    }

    @Test
    public void testPickTile_reserveSpot0_empty() throws LobbyExceptions {
        Game game = mock(Game.class);
        controller.setGame(game);

        when(game.pickTileReserveSpot(player1, 0)).thenReturn(null);
        controller.addPlayerListenerPair(listener1, player1);

        controller.pickTile(listener1, 1000);

        verify(listener1).onEvent(argThat(event -> event.getState() == GameState.VOID_RESERVED_SPOT));
        verify(listener1).onEvent(argThat(event ->
                event.getState() == GameState.ASSEMBLY ));
    }

    @Test
    public void testPickTile_normal_success() throws LobbyExceptions {
        Game game = mock(Game.class);
        controller.setGame(game);

        ComponentTile tile = mock(ComponentTile.class);
        when(game.pickTile(player1, 42)).thenReturn(tile);
        controller.addPlayerListenerPair(listener1, player1);

        controller.pickTile(listener1, 42);

        verify(listener1).onEvent(argThat(event ->
                event.getState() == GameState.PICKED_TILE ));
    }


    @Test
    public void testPickTile_normal_robbed() throws LobbyExceptions {
        Game game = mock(Game.class);
        controller.setGame(game);

        when(game.pickTile(player1, 42)).thenReturn(null);
        controller.addPlayerListenerPair(listener1, player1);

        controller.pickTile(listener1, 42);

        verify(listener1).onEvent(argThat(event -> event.getState() == GameState.ROBBED_TILE));
        verify(listener1).onEvent(argThat(event ->
                event.getState() == GameState.ASSEMBLY ));
    }

    @Test
    public void testAddTile_success() throws SpaceShipPlanceException {
        Game game = mock(Game.class);
        controller.setGame(game);
        ComponentTile tile = mock(ComponentTile.class);
        SpaceshipPlance spaceship = mock(SpaceshipPlance.class);

        when(player1.getHandTile()).thenReturn(tile);
        when(player1.getSpaceshipPlance()).thenReturn(spaceship);
        controller.addPlayerListenerPair(listener1, player1);

        controller.addTile(listener1, 2, 3);

        verify(spaceship).placeTileComponents(tile, 2, 3);
        verify(controller).printSpaceship(listener1);
        verify(listener1).onEvent(argThat(event ->
                event.getState() == GameState.ASSEMBLY ));
    }

    @Test
    public void testAddTile_withException() throws SpaceShipPlanceException {
        Game game = mock(Game.class);
        controller.setGame(game);
        ComponentTile tile = mock(ComponentTile.class);
        SpaceshipPlance spaceship = mock(SpaceshipPlance.class);

        when(player1.getHandTile()).thenReturn(tile);
        when(player1.getSpaceshipPlance()).thenReturn(spaceship);
        doThrow(new SpaceShipPlanceException("invalid placement"))
                .when(spaceship).placeTileComponents(tile, 1, 1);

        controller.addPlayerListenerPair(listener1, player1);

        assertThrows(SpaceShipPlanceException.class, () ->
                controller.addTile(listener1, 1, 1)
        );

        verify(spaceship).placeTileComponents(tile, 1, 1);
        verify(controller).printSpaceship(listener1);
        verify(listener1).onEvent(argThat(event ->
                event.getState() == GameState.ASSEMBLY ));
    }

    @Test
    public void testAddNickname_lobbyPhase_notFull() throws LobbyExceptions {
        Lobby lobby = spy(new Lobby(4));
        controller.setLobby(lobby);

        // Imposta lo stato corrente del gioco su LOBBY_PHASE
        controller.setCurrentGameState(GameState.LOBBY_PHASE);

        boolean result = controller.addNickname(listener1, "Alice");

        assertTrue(controller.getRegistredListeners().containsKey(listener1));
        assertTrue(controller.getRealListeners().contains(listener1));
        verify(listener1).onEvent(argThat(e -> e.getState() == GameState.WAIT_LOBBY));
        assertFalse(result);
    }

    @Test
    public void testAddNickname_lobbyPhase_full() throws LobbyExceptions {
        Lobby lobby = spy(new Lobby(1)); // Lobby con 1 solo posto
        Controller controllerSpy = spy(new Controller()); // <-- spy invece del controller reale
        controllerSpy.setLobby(lobby);
        controllerSpy.setCurrentGameState(GameState.LOBBY_PHASE);

        // Stub di gameInit per evitare eccezioni reali
        doNothing().when(controllerSpy).gameInit();

        // Stub: setPlayersNicknames simula aggiunta del nome
        doAnswer(invocation -> {
            lobby.getPlayersNicknames().add("Alice");
            return null;
        }).when(lobby).setPlayersNicknames("Alice");

        when(lobby.isFull()).thenReturn(true);

        boolean result = controllerSpy.addNickname(listener1, "Alice");

        assertTrue(controllerSpy.getRegistredListeners().containsKey(listener1));
        assertTrue(result);

        verify(lobby).setPlayersNicknames("Alice");
        verify(listener1).onEvent(argThat(e -> e.getState() == GameState.WAIT_LOBBY));
        verify(controllerSpy).gameInit(); // <-- verifica che venga chiamato
    }

    @Test(expected = LobbyExceptions.class)
    public void testAddNickname_noLobby() throws LobbyExceptions {
        controller.setLobby(null); // Nessuna lobby presente
        controller.setCurrentGameState(GameState.LOBBY_PHASE);

        controller.addNickname(listener1, "Alice"); // Deve lanciare LobbyExceptions
    }

    @Test
    public void testCreateLobby_success() {
        Controller controller = new Controller();

        // Aggiungi il listener alla lista corretta
        controller.addTestListener(listener1); // oppure accedi direttamente se possibile

        controller.createLobby(3);

        assertNotNull(controller.getLobby());
        assertEquals(GameState.LOBBY_PHASE, controller.getCurrentGameState());

        // Verifica che listener1 abbia ricevuto l'evento corretto
        verify(listener1).onEvent(argThat(e -> e.getState() == GameState.LOBBY_PHASE));
    }

    @Test(expected = LobbyExceptions.class)
    public void testCreateLobby_alreadyExists() {
        Controller controller = new Controller();
        controller.setLobby(new Lobby(2)); // Lobby già impostata

        controller.createLobby(3); // Deve lanciare eccezione
    }

    @Test(expected = LobbyExceptions.class)
    public void testCreateLobby_tooFewPlayers() {
        Controller controller = new Controller();
        controller.createLobby(1); // Deve lanciare eccezione
    }

    @Test(expected = LobbyExceptions.class)
    public void testCreateLobby_tooManyPlayers() {
        Controller controller = new Controller();
        controller.createLobby(5); // Deve lanciare eccezione
    }

    @Test
    public void testNotifyAllListeners() {
        Controller controller = new Controller();

        // Crea due listener mock
        ClientListener listener1 = mock(ClientListener.class);
        ClientListener listener2 = mock(ClientListener.class);

        // Aggiungili direttamente alla lista dei listeners
        synchronized (controller.getListeners()) {
            controller.getListeners().add(listener1);
            controller.getListeners().add(listener2);
        }

        // Crea un evento dummy da inviare
        Event event = new Event(GameState.LOBBY_PHASE, null);

        // Chiamata al metodo da testare
        controller.notifyAllListeners(event);

        // Verifica che entrambi i listener abbiano ricevuto l'evento
        verify(listener1).onEvent(event);
        verify(listener2).onEvent(event);
    }

    @Test
    public void testRemoveEventListener_removesListener() {
        Controller controller = new Controller();

        // Crea due listener mock
        ClientListener listener1 = mock(ClientListener.class);
        ClientListener listener2 = mock(ClientListener.class);

        // Aggiungili alla lista listeners (usa getListeners o riflessione)
        synchronized (controller.getListeners()) {
            controller.getListeners().add(listener1);
            controller.getListeners().add(listener2);
        }

        // Rimuovi listener1
        controller.removeEventListener(listener1);

        // Verifica che solo listener2 sia rimasto
        List<ClientListener> remaining;
        synchronized (controller.getListeners()) {
            remaining = new ArrayList<>(controller.getListeners());
        }

        assertEquals(1, remaining.size());
        assertTrue(remaining.contains(listener2));
        assertFalse(remaining.contains(listener1));
    }

    @Test
    public void testAddEventListener_idleState() {
        Controller controller = new Controller();
        controller.setCurrentGameState(GameState.IDLE);

        ClientListener listener = mock(ClientListener.class);

        controller.addEventListener(listener);

        // Verifica che sia stato aggiunto
        synchronized (controller.getListeners()) {
            assertTrue(controller.getListeners().contains(listener));
        }

        // Verifica che riceva evento IDLE
        verify(listener).onEvent(argThat(e -> e.getState() == GameState.IDLE));
    }

    @Test
    public void testAddEventListener_lobbyPhase() {
        Controller controller = new Controller();
        controller.setCurrentGameState(GameState.LOBBY_PHASE);

        ClientListener listener = mock(ClientListener.class);

        controller.addEventListener(listener);

        // Verifica che sia stato aggiunto
        synchronized (controller.getListeners()) {
            assertTrue(controller.getListeners().contains(listener));
        }

        // Verifica che riceva evento LOBBY_PHASE
        verify(listener).onEvent(argThat(e -> e.getState() == GameState.LOBBY_PHASE));
    }

    /*@Test
    public void testGameInit_success() {
        // Mocka/nicknames
        Lobby mockLobby = mock(Lobby.class);
        when(mockLobby.getPlayersNicknames()).thenReturn(new ArrayList<>(Arrays.asList("Alice", "Bob")));

        Game mockGame = mock(Game.class);
        Timer mockTimer = mock(Timer.class);

        // Mock giocatori
        Player mockPlayer1 = mock(Player.class);
        Player mockPlayer2 = mock(Player.class);

        Flightplance mockFlightplance = mock(Flightplance.class);
        Deck mockDeck = mock(Deck.class);
        when(mockDeck.getCards()).thenReturn(new ArrayList<>());
        when(mockFlightplance.getDeck()).thenReturn(mockDeck);

        when(mockGame.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(mockPlayer1, mockPlayer2)));
        when(mockGame.getFlightplance()).thenReturn(mockFlightplance);
        when(mockGame.getTimer()).thenReturn(mockTimer);

        // Spia Controller e inietti mock
        Controller spyController = spy(new Controller());
        spyController.setLobby(mockLobby);
        spyController.setCurrentGameState(GameState.LOBBY_PHASE);

        // Inietta mock Game al posto della creazione vera
        doReturn(mockGame).when(spyController).createGame(any(ArrayList.class));

        // Prepara realListeners
        spyController.getRealListeners().add(listener1);
        spyController.getRealListeners().add(listener2);

        // Sostituisci il metodo gameInit per usare il tuo game mockato
        doAnswer(invocation -> {
            return mockGame;
        }).when(spyController).createGame(any(ArrayList.class));

        // Esegui
        spyController.gameInit();

        // Verifiche principali
        assertEquals(GameState.ASSEMBLY, spyController.getCurrentGameState());
        assertEquals(2, spyController.getPlayers().size());

        // Verifica che i listener ricevano l'evento
        verify(listener1).onEvent(any(Event.class));
        verify(listener2).onEvent(any(Event.class));

        // Verifica che timer sia stato avviato
        verify(mockTimer).start();

        // Verifica che mappa player-listener sia coerente
        assertEquals(mockPlayer1, spyController.getPlayerbyListener().get(listener1));
        assertEquals(listener1, spyController.getListenerbyPlayer().get(mockPlayer1));
    }*/




}

