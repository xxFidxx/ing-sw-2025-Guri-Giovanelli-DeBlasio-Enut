package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.adventureCards.AdventureCard;

import java.io.Serializable;
import java.util.List;

/**
 * The {@code Deck} class represents a collection of {@link AdventureCard} objects and their
 * associated {@link Flightplance}. This class manages the cards in the deck and associates
 * its instance with each card in the deck.
 */
public class Deck {
    /** The list of adventure cards contained in the deck. */
    private List<AdventureCard> cards;

    /** The flightplan associated with this deck. */
    private Flightplance flightplance;

    /**
     * Constructs a new {@code Deck} with the specified list of adventure cards and flightplan.
     * Each card in the deck is linked to this instance of {@code Deck}.
     *
     * @param cards       the list of {@link AdventureCard} objects that compose the deck
     * @param flightplance the {@link Flightplance} associated with this deck
     */
    public Deck(List<AdventureCard> cards, Flightplance flightplance) {
        this.cards = cards;
        this.flightplance = flightplance;
        for (AdventureCard ac : cards) {
            ac.setDeck(this);
        }
    }

    /**
     * Returns the list of adventure cards contained in the deck.
     *
     * @return the list of {@link AdventureCard} objects
     */
    public List<AdventureCard> getCards() {
        return cards;
    }

    /**
     * Returns the flightplan associated with this deck.
     *
     * @return the {@link Flightplance} object
     */
    public Flightplance getFlightPlance() {
        return flightplance;
    }
}