package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.adventureCards.AdventureCard;

import java.util.List;

public class Deck {
    private List<AdventureCard> cards;
    private Flightplance flightplance;

    public Deck(List<AdventureCard> cards, Flightplance flightplance) {
        this.cards = cards;
        this.flightplance = flightplance;
//        for(AdventureCard ac : cards ) {
//           ac.setDeck(this);
//       }
    }

    public List<AdventureCard> getCards() {
        return cards;
    }

    public Flightplance getFlightPlance() {
        return flightplance;
    }
}
