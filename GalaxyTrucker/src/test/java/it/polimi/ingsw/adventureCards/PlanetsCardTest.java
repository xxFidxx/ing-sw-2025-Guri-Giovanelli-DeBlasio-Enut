package it.polimi.ingsw.adventureCards;
import it.polimi.ingsw.model.adventureCards.PlanetsCard;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.game.*;


import it.polimi.ingsw.model.resources.Planet;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class PlanetsCardTest {

    private Deck deck;
    private Flightplance flightPlance;
    private Game game;
    private Player player1;
    private Player player2;
    private SpaceshipPlance spaceship1;
    private SpaceshipPlance spaceship2;
    private Planet planet1;
    private Planet planet2;
    private ArrayList<Planet> planets;
    private ArrayList<Player> players;

    @Before
    public void setUp() {
        deck = mock(Deck.class);
        flightPlance = mock(Flightplance.class);
        game = mock(Game.class);
        player1 = mock(Player.class);
        player2 = mock(Player.class);
        spaceship1 = mock(SpaceshipPlance.class);
        spaceship2 = mock(SpaceshipPlance.class);

        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);

        players = new ArrayList<>(Arrays.asList(player1, player2));
        when(game.getPlayers()).thenReturn(players);

        when(player1.getSpaceshipPlance()).thenReturn(spaceship1);
        when(player2.getSpaceshipPlance()).thenReturn(spaceship2);

        GoodsBlock[] goods1 = {
                new GoodsBlock(2, ColorType.YELLOW)
        };
        GoodsBlock[] goods2 = {
                new GoodsBlock(1, ColorType.GREEN)
        };

        planet1 = new Planet(goods1, false);
        planet2 = new Planet(goods2, false);

        planets = new ArrayList<>(Arrays.asList(planet1, planet2));
    }

    @Test
    public void testActivate_withPlayersChoosingPlanets_shouldRewardAndMove() {
        // Simulazione ordine della lista
        ArrayList<Player> orderedPlayers = new ArrayList<>();
        orderedPlayers.add(player2);
        orderedPlayers.add(player1);

        when(game.getPlayers()).thenReturn(orderedPlayers);

        // Simula le risposte del metodo che sceglie il giocatore dal top dello stack
        when(game.choosePlayerPlanet(any(), eq(planets), any())).thenAnswer(invocation -> {
            Stack<Player> stack = invocation.getArgument(2);
            return stack.isEmpty() ? null : stack.pop();
        });

        // Simula le scelte di pianeti
        when(player1.choosePlanet(planets)).thenReturn(planet1);
        when(player2.choosePlanet(planets)).thenReturn(planet2);

        PlanetsCard card = new PlanetsCard("Planet Card", 1, planets, 2, deck);
        card.activate();

        // Verifiche
        verify(spaceship1).cargoManagement(planet1.getReward());
        verify(spaceship2).cargoManagement(planet2.getReward());

        verify(flightPlance).move(-2, player1);
        verify(flightPlance).move(-2, player2);
    }

    @Test
    public void testActivate_withNullPlayer_shouldTerminateEarly() {
        when(game.choosePlayerPlanet(any(), eq(planets), any())).thenReturn(null);

        PlanetsCard card = new PlanetsCard("Planet Card", 1, planets, 2, deck);
        card.activate();

        // Nessuna interazione
        verify(spaceship1, never()).cargoManagement(any());
        verify(flightPlance, never()).move(anyInt(), any());
    }
}







