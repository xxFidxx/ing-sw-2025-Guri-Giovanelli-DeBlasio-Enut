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
    private Player player1 , player2 , player3 ;
    private Planet planet1, planet2;
    private ArrayList<Planet> planets;
    private int lostDays;
    private SpaceshipPlance spaceshipPlance;
    private GoodsBlock good1, good2;

    @Before
    public void setUp(){
        deck= mock(Deck.class);
        flightPlance = mock(Flightplance.class);
        game = mock(Game.class);
        player1 = mock(Player.class);
        player2 = mock(Player.class);
        player3 = mock(Player.class);
        planet1= mock(Planet.class);
        planet2= mock(Planet.class);
        spaceshipPlance= mock(SpaceshipPlance.class);
        good1= mock(GoodsBlock.class);
        good2= mock(GoodsBlock.class);
        GoodsBlock[] rewards = {good1, good2};
        planets = new ArrayList<>(Arrays.asList(planet1,planet2));


        //comportamenti di base
        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);
        when(game.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(player1,player2,player3)));
        when(player1.getSpaceshipPlance()).thenReturn(spaceshipPlance);
        when(player2.getSpaceshipPlance()).thenReturn(spaceshipPlance);
        when(player3.getSpaceshipPlance()).thenReturn(spaceshipPlance);






        //scelte simulate
        when(game.choosePlayerPlanet(any(), eq(planets), any())).thenReturn(player1,player2,null); // player1 sceglie, poi nessuno
        when(player1.choosePlanet(planets)).thenReturn(planet1);
        when(planet1.getReward()).thenReturn(rewards);
        when(planet2.getReward()).thenReturn(rewards);
        when(player2.choosePlanet(planets)).thenReturn(planet2);
    }
    @Test
    public void testActivate_shouldSelectPlayerAndMoveThem(){
        PlanetsCard card = new PlanetsCard("Planets!", 2, planets, 3, deck);
        card.activate();

        verify(player1).choosePlanet(planets);
        verify(player2).choosePlanet(planets);
        verify(spaceshipPlance, atLeastOnce()).cargoManagement(planet1.getReward());//verifico che il cargo sia gestito correttamente
        verify(spaceshipPlance, atLeastOnce()).cargoManagement(planet2.getReward());
        verify(flightPlance).move(-3,player1);//verifico che il giocatore sia stato spostato
        verify(flightPlance).move(-3,player2);

    }



    }






