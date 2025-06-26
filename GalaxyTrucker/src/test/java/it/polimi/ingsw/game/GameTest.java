package it.polimi.ingsw.game;

import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.resources.Planet;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameTest {

    private Game game;
    private AdventureCard mockCard;



    @Before
    public void setUp() {
        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add("Alice");
        playerNames.add("Bob");
        playerNames.add("Charlie");

        // Inizializza un vero Game
        game = new Game(playerNames);
        mockCard = mock(AdventureCard.class);
        game.getPlayers().get(0).getPlaceholder().setPosizione(3);
        game.getPlayers().get(1).getPlaceholder().setPosizione(1);
        game.getPlayers().get(2).getPlaceholder().setPosizione(2);

        for (Player p : game.getPlayers()) {
            ComponentTile[][] grid = p.getSpaceshipPlance().getComponents();
            for (int y = 0; y < grid.length; y++) {
                for (int x = 0; x < grid[0].length; x++) {
                    grid[y][x] = null;
                }
            }
        }
    }

    @Test
    public void testFreePlanets_WithOneFreePlanet() {
        Planet busyPlanet = mock(Planet.class);
        when(busyPlanet.isBusy()).thenReturn(true);

        Planet freePlanet = mock(Planet.class);
        when(freePlanet.isBusy()).thenReturn(false);



        ArrayList<Planet> planets = new ArrayList<>();
        planets.add(busyPlanet);
        planets.add(freePlanet);

        boolean result = game.freePlanets(mockCard, planets);


        assertTrue(result);
    }

    @Test
    public void testFreePlanets_AllBusy() {
        Planet busy1 = mock(Planet.class);
        Planet busy2 = mock(Planet.class);

        when(busy1.isBusy()).thenReturn(true);
        when(busy2.isBusy()).thenReturn(true);

        ArrayList<Planet> planets = new ArrayList<>();
        planets.add(busy1);
        planets.add(busy2);

        boolean result = game.freePlanets(mockCard, planets);
        assertFalse(result);
    }

    @Test
    public void testFreePlanets_EmptyList() {
        ArrayList<Planet> planets = new ArrayList<>();
        boolean result = game.freePlanets(mockCard, planets);
        assertFalse(result);
    }
    @Test
    public void testOrderPlayers() {

        game.orderPlayers();

        // Dopo l'ordinamento: ordine atteso → B (1), C (2), A (3)
        List<Player> players = game.getPlayers();

        assertEquals("Bob", players.get(0).getNickname());
        assertEquals("Charlie", players.get(1).getNickname());
        assertEquals("Alice", players.get(2).getNickname());
    }
    @Test
    public void testPickTile_ValidTile() {
        // Otteniamo un player reale dal gioco
        Player player = game.getPlayers().get(0); // Alice

        // Otteniamo l’array di tile disponibili
        ComponentTile[] assemblingTiles = game.getAssemblingTiles();

        // Assicuriamoci che ci sia almeno una tile disponibile
        int validIndex = -1;
        for (int i = 0; i < assemblingTiles.length; i++) {
            if (assemblingTiles[i] != null) {
                validIndex = i;
                break;
            }
        }
        assertTrue("Nessuna tile valida trovata", validIndex != -1);

        ComponentTile expectedTile = assemblingTiles[validIndex];

        // Esegui il metodo da testare
        ComponentTile pickedTile = game.pickTile(player, validIndex);

        // Verifica che la tile sia stata assegnata al player
        assertEquals(expectedTile, pickedTile);
        assertEquals(expectedTile, player.getHandTile());

        // Verifica che la tile non sia più nell’array
        assertNull(game.getAssemblingTiles()[validIndex]);
    }
    @Test
    public void testPickTileReserveSpot_ValidIndex() {
        // Otteniamo un player reale dal gioco
        Player player = game.getPlayers().get(0); // Alice
        SpaceshipPlance plance = player.getSpaceshipPlance();

        // Creiamo una tile mock per la riserva
        ComponentTile mockTile = mock(ComponentTile.class);

        // Inseriamo la tile nella riserva
        plance.getReserveSpot().clear(); // assicuriamoci sia vuoto
        plance.getReserveSpot().add(mockTile);

        // Chiamata al metodo da testare
        ComponentTile result = game.pickTileReserveSpot(player, 0);

        // Verifiche
        assertEquals(mockTile, result);
        assertEquals(mockTile, player.getHandTile());
        assertFalse(plance.getReserveSpot().contains(mockTile));
    }

    @Test
    public void testTilesToId_WithMixedComponentTiles() {
        // Crea mock delle ComponentTile
        ComponentTile tile1 = mock(ComponentTile.class);
        ComponentTile tile2 = mock(ComponentTile.class);
        ComponentTile tile3 = null;

        // Simula il comportamento dei getId()
        when(tile1.getId()).thenReturn(10);
        when(tile2.getId()).thenReturn(20);

        // Prepara l’array da testare
        ComponentTile[] inputTiles = new ComponentTile[]{tile1, tile3, tile2};

        // Chiama il metodo tilesToId del game
        Integer[] result = game.tilesToId(inputTiles);

        // Verifiche
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(Integer.valueOf(10), result[0]);
        assertNull(result[1]);
        assertEquals(Integer.valueOf(20), result[2]);
    }

    @Test
    public void testResetResponded_SetsAllPlayersToFalse() {
        // Arrange: imposta alcuni player come già responded = true
        game.getPlayers().get(0).setResponded(true);
        game.getPlayers().get(1).setResponded(true);
        game.getPlayers().get(2).setResponded(false);

        // Act: chiama il metodo da testare
        game.resetResponded();

        // Assert: controlla che tutti siano tornati a false
        for (Player p : game.getPlayers()) {
            assertFalse("Player " + p.getNickname() + " dovrebbe avere responded = false", p.hasResponded());
        }
    }

    @Test
    public void testResetDoubleCannons() {
        // Crea un DoubleCannon "carico"
        ConnectorType[] connectors = {
                ConnectorType.CANNON,
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH
        };
        DoubleCannon doubleCannon = new DoubleCannon(connectors, 42);
        doubleCannon.setCharged(true); // inizialmente carico

        // Aggiungilo alla nave del primo giocatore (Alice)
        Player alice = game.getPlayers().get(0);
        alice.getSpaceshipPlance().getCannons().add(doubleCannon);

        // Assicuriamoci che sia carico
        assertTrue(doubleCannon.isCharged());

        // Chiamiamo il metodo da testare
        game.resetDoubleCannons();

        // Verifica che ora il cannone sia "scarico"
        assertFalse("Il DoubleCannon dovrebbe essere scarico dopo resetDoubleCannons()", doubleCannon.isCharged());
    }

    @Test
    public void testResetDoubleEngines() {
        // Crea un DoubleEngine "carico"
        ConnectorType[] connectors = {
                ConnectorType.ENGINE,
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH
        };
        DoubleEngine doubleEngine = new DoubleEngine(connectors, 101);
        doubleEngine.setCharged(true); // inizialmente carico

        // Aggiungilo alla nave di un giocatore, ad esempio Bob
        Player bob = game.getPlayers().get(1);
        bob.getSpaceshipPlance().getEngines().add(doubleEngine);

        // Assicuriamoci che sia carico prima del reset
        assertTrue(doubleEngine.isCharged());

        // Invoca il metodo da testare
        game.resetDoubleEngines();

        // Verifica che ora sia scarico
        assertFalse("Il DoubleEngine dovrebbe essere scarico dopo resetDoubleEngines()", doubleEngine.isCharged());
    }

    @Test
    public void testResetRewards() {
        // Imposta delle reward simulate per ciascun giocatore
        for (Player player : game.getPlayers()) {
            GoodsBlock[] fakeReward = new GoodsBlock[]{mock(GoodsBlock.class)};
            player.setReward(fakeReward);
            assertNotNull("Setup fallito: la reward dovrebbe essere inizializzata", player.getReward());
        }

        // Chiamata al metodo da testare
        game.resetRewards();

        // Verifica che ogni reward sia stata azzerata (null)
        for (Player player : game.getPlayers()) {
            assertNull("La reward dovrebbe essere null dopo resetRewards()", player.getReward());
        }
    }

    @Test
    public void testGetEndStats_fullFlow() throws SpaceShipPlanceException {

        Player alice = game.getPlayers().get(0); // posizione 3
        Player bob = game.getPlayers().get(1);   // posizione 1
        Player charlie = game.getPlayers().get(2); // posizione 2

        // ========== 1. rewardSpaceship() ==========
        // Alice: 0 exposed connectors (solo SMOOTH)
        ComponentTile aliceTile = new StructuralModule(
                new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH},
                100);
        alice.getSpaceshipPlance().placeTileComponents(aliceTile, 3, 2); // posizione centrale valida

        // Bob e Charlie: almeno 1 connettore esposto
        StructuralModule bobTile = new StructuralModule(
                new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH},
                101);
        bob.getSpaceshipPlance().placeTileComponents(bobTile, 3, 2);

        StructuralModule charlieTile = new StructuralModule(
                new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH},
                102);
        charlie.getSpaceshipPlance().placeTileComponents(charlieTile, 3, 2);

        // ========== 2. penalizeLostTiles() ==========
        // Bob ha 1 tessera nella riserva → penalità -1
        StructuralModule reserveTile = new StructuralModule(
                new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH},
                200);
        bob.getSpaceshipPlance().getReserveSpot().add(reserveTile);

        // ========== 3. rewardCargo() ==========
        // Charlie ha 2 merci: una RED (4), una BLUE (1) → totale 5 → dimezzato (surrended) = +2
        charlie.setSurrended(true);
        CargoHolds cargo = new CargoHolds(
                new ConnectorType[]{ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH, ConnectorType.SMOOTH},
                300, false, 3);
        cargo.getGoods()[0] = new GoodsBlock(ColorType.RED);
        cargo.getGoods()[1] = new GoodsBlock(ColorType.BLUE);
        charlie.getSpaceshipPlance().getCargoHolds().add(cargo);

        // ========== 4. rewardPlaces() ==========
        // Alice (pos 3) → +4, Charlie (pos 2, surrended) → 0, Bob (pos 1) → +2

        // ========== Esegui ==========
        String stats = game.getEndStats();

        // ========== Asserzioni ==========
        // Crediti totali:
        // Alice = 4 (posizione) + 2 (spaceship) = 6
        // Bob = 2 (posizione) -1 (lost tile) = 1
        // Charlie = 2 (cargo dimezzato), niente posizione, niente spaceship = 2

        String expected = """
        1. Alice - 6
        2. Charlie - 2
        3. Bob - 1
        """;

        assertEquals(expected.trim(), stats.trim());
    }


    @Test
    public void testRewardSpaceship() {
        Player alice = game.getPlayers().get(0);
        Player bob = game.getPlayers().get(1);
        Player charlie = game.getPlayers().get(2);

        // Crea mock di SpaceshipPlance
        SpaceshipPlance mockAlicePlance = mock(SpaceshipPlance.class);
        SpaceshipPlance mockBobPlance = mock(SpaceshipPlance.class);
        SpaceshipPlance mockCharliePlance = mock(SpaceshipPlance.class);

        // Configura il comportamento del mock
        when(mockAlicePlance.countExposedConnectors()).thenReturn(2);
        when(mockBobPlance.countExposedConnectors()).thenReturn(3);
        when(mockCharliePlance.countExposedConnectors()).thenReturn(4);

        // Sostituisci i plance reali con i mock
        alice.setSpaceshipPlance(mockAlicePlance);
        bob.setSpaceshipPlance(mockBobPlance);
        charlie.setSpaceshipPlance(mockCharliePlance);

        game.rewardSpaceship();

        assertEquals(2, alice.getCredits());     // vincitore
        assertEquals(0, bob.getCredits());
        assertEquals(0, charlie.getCredits());

    }
    @Test
    public void testRewardCargo() {
        Player alice = game.getPlayers().get(0);
        Player bob = game.getPlayers().get(1);

        alice.setSurrended(false);
        bob.setSurrended(true);

        // Mock CargoHolds per Alice con capacità 3
        CargoHolds aliceCargo = mock(CargoHolds.class);
        GoodsBlock goodBlue = mock(GoodsBlock.class);
        GoodsBlock goodGreen = mock(GoodsBlock.class);
        GoodsBlock goodYellow = mock(GoodsBlock.class);

        when(goodBlue.getValue()).thenReturn(0);    // Blu
        when(goodGreen.getValue()).thenReturn(1);   // Verde
        when(goodYellow.getValue()).thenReturn(2);  // Giallo

        GoodsBlock[] aliceGoods = new GoodsBlock[]{goodBlue, goodGreen, goodYellow};
        when(aliceCargo.getCapacity()).thenReturn(3);
        when(aliceCargo.getGoods()).thenReturn(aliceGoods);

        // Mock CargoHolds per Bob con capacità 2 e 2 rossi
        CargoHolds bobCargo = mock(CargoHolds.class);
        GoodsBlock goodRed1 = mock(GoodsBlock.class);
        GoodsBlock goodRed2 = mock(GoodsBlock.class);

        when(goodRed1.getValue()).thenReturn(3);      // Rosso
        when(goodRed2.getValue()).thenReturn(3);      // Rosso

        GoodsBlock[] bobGoods = new GoodsBlock[]{goodRed1, goodRed2};
        when(bobCargo.getCapacity()).thenReturn(2);
        when(bobCargo.getGoods()).thenReturn(bobGoods);

        // Mock spaceshipPlance per Alice e Bob
        SpaceshipPlance alicePlance = mock(SpaceshipPlance.class);
        SpaceshipPlance bobPlance = mock(SpaceshipPlance.class);

        when(alicePlance.getCargoHolds()).thenReturn(new ArrayList<>(List.of(aliceCargo)));
        when(bobPlance.getCargoHolds()).thenReturn(new ArrayList<>(List.of(bobCargo)));

        alice.setSpaceshipPlance(alicePlance);
        bob.setSpaceshipPlance(bobPlance);

        // Esegui il metodo da testare
        game.rewardCargo();

        // Alice: 0 + 1 + 2 = 3
        assertEquals(3, alice.getCredits());
        // Bob: (3 + 3) / 2 = 3 (perché surrender)
        assertEquals(3, bob.getCredits());
    }


    @Test
    public void testPenalizeLostTiles() {
        // Prendi i giocatori reali
        Player alice = game.getPlayers().get(0); // Posizione 3
        Player bob = game.getPlayers().get(1);   // Posizione 1
        Player charlie = game.getPlayers().get(2); // Posizione 2

        // Imposta crediti iniziali
        alice.setCredits(10);
        bob.setCredits(8);
        charlie.setCredits(5);

        // Aggiungi componenti ai reserveSpot (penalità = numero di componenti)
        SpaceshipPlance aliceShip = alice.getSpaceshipPlance();
        SpaceshipPlance bobShip = bob.getSpaceshipPlance();
        SpaceshipPlance charlieShip = charlie.getSpaceshipPlance();

        // Aggiungi componenti fittizi
        aliceShip.getReserveSpot().add(mock(ComponentTile.class));      // 1 componente → -1
        aliceShip.getReserveSpot().add(mock(ComponentTile.class));      // 2 totale → -2

        bobShip.getReserveSpot().add(mock(ComponentTile.class));        // 1 → -1

        // Charlie ha 0 componenti → nessuna penalità

        // Esegui metodo da testare
        game.penalizeLostTiles();

        // Verifica risultati
        assertEquals(8, alice.getCredits());  // 10 - 2
        assertEquals(7, bob.getCredits());    // 8 - 1
        assertEquals(5, charlie.getCredits()); // 5 - 0
    }

    @Test
    public void testRewardPlacesDescending() {
        // Giocatori reali
        Player alice = game.getPlayers().get(0);   // Primo nella lista
        Player bob = game.getPlayers().get(1);     // Secondo
        Player charlie = game.getPlayers().get(2); // Terzo

        // Imposta crediti iniziali
        alice.setCredits(5);
        bob.setCredits(10);
        charlie.setCredits(20);

        // Bob si arrende → non riceve bonus
        bob.setSurrended(true);

        // Esegui metodo
        game.rewardPlaces();

    /*
        Ordine inverso:
        i = 2 → Charlie: 20 + 3 = 23
        i = 1 → Bob: surrended → resta 10
        i = 0 → Alice: 5 + 4 = 9
     */

        assertEquals(9, alice.getCredits());
        assertEquals(10, bob.getCredits());
        assertEquals(23, charlie.getCredits());
    }


}

