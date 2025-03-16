package it.polimi.ingsw.game;

public class Flightplance {
    private Placeholder[] spots;
    private Deck[] decks;

    public Flightplance(Placeholder[] spots, Deck[] decks) {
        this.spots = spots;
        this.decks = decks;
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
}
