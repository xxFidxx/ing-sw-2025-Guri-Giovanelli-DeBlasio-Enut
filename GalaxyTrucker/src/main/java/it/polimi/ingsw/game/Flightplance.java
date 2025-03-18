package it.polimi.ingsw.game;

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

    public Placeholder[] getNext(Placeholder placeholder) {}

    public void move(Placeholder placeholder, int num){}

    public Placeholder[] getFirst() {}

    public Placeholder[] getSpots() {
        return spots;
    }

    public Game getGame() {
        return game;
    }
}
