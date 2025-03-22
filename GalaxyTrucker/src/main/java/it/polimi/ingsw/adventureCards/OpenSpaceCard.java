package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.componentTiles.DoubleEngine;
import it.polimi.ingsw.componentTiles.Engine;
import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.SpaceshipPlance;

import java.util.Arrays;
import java.util.Comparator;

public class OpenSpaceCard extends AdventureCard {

    public OpenSpaceCard(String name, int level, Deck deck) {
        super(name, level, deck);
    }

    @Override
    public void activate() {
        Player[] players = deck.getFlightplance().getGame().getPlayers();
        Arrays.sort(players, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        for (int i = players.length -1; i >= 0; i--) {
            SpaceshipPlance plance = players[i].getSpaceshipPlance();
            int power = (int) players[i].engineStrenght(plance);
            players[i].getPlaceholder().move(power);
        }
    }

}
