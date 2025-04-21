package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.adventureCards.AdventureCard;

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
