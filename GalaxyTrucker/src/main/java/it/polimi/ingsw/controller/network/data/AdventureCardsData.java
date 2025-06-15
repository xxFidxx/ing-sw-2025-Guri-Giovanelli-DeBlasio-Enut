package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;
import java.util.ArrayList;

public class AdventureCardsData extends DataContainer implements Serializable {
    private final ArrayList <Card> adventureCards;
    private final int nDeck;

    public AdventureCardsData(ArrayList<Card> cardsToShow, int nDeck) {
        this.adventureCards = cardsToShow;
        this.nDeck = nDeck;
    }


    public  ArrayList <Card> getAdventureCards() {
        return adventureCards;
    }

    public int getnDeck() {
        return nDeck;
    }
}
