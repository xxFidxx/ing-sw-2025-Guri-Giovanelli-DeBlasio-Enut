package it.polimi.ingsw.adventureCards;

import static it.polimi.ingsw.model.game.ColorType.BLUE;
import static it.polimi.ingsw.model.game.ColorType.GREEN;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import it.polimi.ingsw.model.adventureCards.PlanetsCard;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.resources.Planet;
import org.junit.Before;
import org.junit.Test;

public class PlanetsCardTest {

    private PlanetsCard card;
    private Player mockPlayer;
    private Planet mockPlanet;
    private Deck mockDeck;
    private Flightplance mockFlightPlance;
    private GoodsBlock[] reward;

    @Before
    public void setUp() {
        mockPlayer = mock(Player.class);
        mockPlanet = mock(Planet.class);
        mockDeck = mock(Deck.class);
        mockFlightPlance = mock(Flightplance.class);

        reward = new GoodsBlock[] {
                new GoodsBlock(BLUE), // valore 1
                new GoodsBlock(GREEN) // valore 2
        };

        when(mockPlanet.getReward()).thenReturn(reward);
        when(mockDeck.getFlightPlance()).thenReturn(mockFlightPlance);

        ArrayList<Planet> planets = new ArrayList<>();
        planets.add(mockPlanet);

        // lostDays = 2
        card = new PlanetsCard("Planets", 1, planets, 2);
        card.setActivatedPlayer(mockPlayer);
        card.setChosenPlanet(mockPlanet);
        card.setDeck(mockDeck);
    }

    @Test
    public void testActivate_assignsRewardAndMovesPlayerBack() {
        card.activate();

        verify(mockPlayer).setReward(reward);
        verify(mockFlightPlance).move(-2, mockPlayer);
    }
}

