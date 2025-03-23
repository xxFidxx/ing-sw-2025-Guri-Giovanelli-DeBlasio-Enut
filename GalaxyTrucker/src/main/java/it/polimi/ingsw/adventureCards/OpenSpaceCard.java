package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

import java.util.Arrays;
import java.util.Comparator;

public class OpenSpaceCard extends AdventureCard {

    public OpenSpaceCard(String name, int level, Deck deck) {
        super(name, level, deck);
    }

    @Override
    public void activate() {
        Player[] players = deck.getFlightPlance().getGame().getPlayers();
        Arrays.sort(players, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        for (int i = players.length -1; i >= 0; i--) {
            int power = players[i].getEngineStrenght();
            deck.getFlightPlance().move(power, players[i]);
        }
    }

}
