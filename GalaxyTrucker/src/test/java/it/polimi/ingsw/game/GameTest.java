package it.polimi.ingsw.game;

import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Placeholder;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.SpaceshipPlance;
import it.polimi.ingsw.model.resources.Planet;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

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





}

