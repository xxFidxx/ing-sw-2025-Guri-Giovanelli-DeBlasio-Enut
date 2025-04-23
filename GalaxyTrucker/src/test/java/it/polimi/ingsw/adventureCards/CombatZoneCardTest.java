package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.CombatZoneCard;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.model.resources.CombatZoneType;
import it.polimi.ingsw.model.resources.Projectile;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

public class CombatZoneCardTest {

    private Deck deck;
    private Flightplance flightPlance;
    private Game game;
    private Projectile projectile;
    private CombatZoneCard card;
    private Player p1, p2, p3;
    private ArrayList<Player> players;

    @Before
    public void setUp() {
        deck = mock(Deck.class);
        flightPlance = mock(Flightplance.class);
        game = mock(Game.class);
        projectile = mock(Projectile.class);

        when(deck.getFlightPlance()).thenReturn(flightPlance);
        when(flightPlance.getGame()).thenReturn(game);

        p1 = mock(Player.class);
        p2 = mock(Player.class);
        p3 = mock(Player.class);

        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);

        when(game.getPlayers()).thenReturn(players);
        when(game.throwDices()).thenReturn(8); // valore costante per semplificare
    }

    @Test
    public void testActivate_withLostCrewType_shouldAffectWeakestPlayers() {
        Projectile[] cannons = { projectile };
        card = new CombatZoneCard("Combat Zone", 1, 3, 2, cannons, deck, CombatZoneType.LOSTCREW);

        when(p1.getNumEquip()).thenReturn(3);
        when(p2.getNumEquip()).thenReturn(2); // weakest in crew
        when(p3.getNumEquip()).thenReturn(4);

        when(p1.getEngineStrenght()).thenReturn(4);
        when(p2.getEngineStrenght()).thenReturn(2); // weakest in engine
        when(p3.getEngineStrenght()).thenReturn(5);

        when(p1.getFireStrenght()).thenReturn(4F); // F sta per float
        when(p2.getFireStrenght()).thenReturn(6F);
        when(p3.getFireStrenght()).thenReturn(1F); // weakest in fire

        card.activate();

        verify(flightPlance).move(-3, p2); // weakest crew
        verify(p2).loseCrew(2); // weakest engine
        verify(projectile).activate(p3, 8); // weakest fire, roll = 8
    }

    @Test
    public void testActivate_withLostGoodsType_shouldAffectWeakestPlayers() {
        Projectile[] cannons = { projectile };
        card = new CombatZoneCard("Combat Zone", 1, 4, 3, cannons, deck, CombatZoneType.LOSTGOODS);

        SpaceshipPlance mockShip = mock(SpaceshipPlance.class);
        when(p1.getSpaceshipPlance()).thenReturn(mockShip);
        when(p2.getSpaceshipPlance()).thenReturn(mockShip);
        when(p3.getSpaceshipPlance()).thenReturn(mockShip);

        when(p1.getFireStrenght()).thenReturn(4F);
        when(p2.getFireStrenght()).thenReturn(3F);
        when(p3.getFireStrenght()).thenReturn(1F); // weakest fire

        when(p1.getEngineStrenght()).thenReturn(4);
        when(p2.getEngineStrenght()).thenReturn(2); // weakest engine
        when(p3.getEngineStrenght()).thenReturn(5);

        when(p1.getNumEquip()).thenReturn(2); // weakest crew
        when(p2.getNumEquip()).thenReturn(4);
        when(p3.getNumEquip()).thenReturn(3);

        card.activate();

        verify(flightPlance).move(-4, p3); // weakest fire
        verify(mockShip).looseGoods(3); // weakest engine
        verify(projectile).activate(p1, 8); // weakest crew
    }

}
