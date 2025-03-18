package it.polimi.ingsw.game;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import java.util.Arrays;

public class Flightplance {
    private Placeholder[] spots;
    private Deck[] decks;
    private Game game;

    public Flightplance(Placeholder[] spots, Deck[] decks, Game game) {
        this.spots = spots;
        this.decks = decks;
        this.game = game;
    }

    public Deck[] getDecks() {
        return decks;
    }

    public Optional<Player> getNext(Player player) {

    }

    public void move(Placeholder placeholder, int num){}

    public Optional <Player> getFirst() {
        return Arrays.stream(game.getPlayer()).max(Comparator.comparingInt(p->p.getPlaceholder().getPosizione()));





    }

    public Placeholder[] getSpots() {
        return spots;
    }

    public Game getGame() {
        return game;
    }
}
