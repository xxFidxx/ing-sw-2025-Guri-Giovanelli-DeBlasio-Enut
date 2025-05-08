package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OpenSpaceCard extends AdventureCard {

    public OpenSpaceCard(String name, int level, Deck deck) {
        super(name, level, deck);
    }

    public OpenSpaceCard(String name, int level) {
        super(name, level);
    }

    @Override
    public void activate() {
        ArrayList<Player> players = deck.getFlightPlance().getGame().getPlayers();

        // players.sort(Comparator.comparingInt(p -> p.getPlaceholder().getPosizione()));
        Collections.reverse(players);

        for (Player player : players) {
            int power = player.getEngineStrenght();
            deck.getFlightPlance().move(power, player);
        }
    }

}
