package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;

public class OpenSpaceCard extends AdventureCard {

    public OpenSpaceCard(String name, int level, Deck deck) {
        super(name, level, deck);
    }

    @Override
    public void activate() {
        ArrayList<Player> players = deck.getFlightPlance().getGame().getPlayers();
        /* risistemarlo usando ArrayList al posto di un array normale
        Arrays.sort(players, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        for (int i = players.length -1; i >= 0; i--) {
            int power = players[i].getEngineStrenght();
            deck.getFlightPlance().move(power, players[i]);
        } */
    }

}
