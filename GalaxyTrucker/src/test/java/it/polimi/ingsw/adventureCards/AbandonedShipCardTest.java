package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Flightplance;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AbandonedShipCardTest {

    @Test
    public void test_activate() {
        // Setup: Creo un player reale
        Player player = new Player("Andrea", null, null, 5, 4, 0); // credits = 5, crew = 4
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);

        // Creo un Game fittizio che ritorna sempre questo player
        Game game = new Game(players, null, null, null) {
            @Override
            public Player choosePlayer(AdventureCard c) {
                return player;
            }
        };

        // Creo un Flightplance con override di move()
        Flightplance plance = new Flightplance(null, null, game) {
            private int daysMoved = 0;

            @Override
            public void move(int days, Player p) {
                daysMoved = days;
                assertEquals(-2, days); // lostDays = 2 -> -2
                assertEquals(player, p);
            }
        };

        // Sistemo le dipendenze tra oggetti
        Deck deck = new Deck(new AdventureCard[0], plance);

        // Creo la carta con deck, lostDays = 2, lostCrew = 1, credits = 3
        AbandonedShipCard card = new AbandonedShipCard("Nave", 1, 2, 1, 3, deck);

        // Act
        card.activate();

        // Assert finali
        assertEquals(3, player.getNumEquip());   // 4 - 1
        assertEquals(8, player.getCredits());    // 5 + 3
    }

}