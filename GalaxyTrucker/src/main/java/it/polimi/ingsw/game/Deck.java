package it.polimi.ingsw.game;

import it.polimi.ingsw.adventureCards.AdventureCard;

public class Deck {
    private AdventureCard[] cards;
    private Flightplance flightplance;

    public Deck(AdventureCard[] cards, Flightplance flightplance) {
        this.cards = cards;
        this.flightplance = flightplance;
    }

    public AdventureCard[] getCards() {
        return cards;
    }

    public Flightplance getFlightPlance() {
        return flightplance;
    }
}
