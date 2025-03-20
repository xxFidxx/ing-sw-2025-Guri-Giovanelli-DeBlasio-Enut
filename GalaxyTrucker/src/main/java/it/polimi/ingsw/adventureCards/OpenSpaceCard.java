package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Player;
import it.polimi.ingsw.game.Game;

import java.util.Arrays;
import java.util.Comparator;

public class OpenSpaceCard extends AdventureCard {

    public OpenSpaceCard(String name, int level) {
        super(name, level);
    }

    @Override
    public void activate() {
        Player[] players = deck.getFlightplance().getGame().getPlayers();
        Arrays.sort(players, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        for (int i = players.length -1; i >= 0; i--) {

        }
    }
}
