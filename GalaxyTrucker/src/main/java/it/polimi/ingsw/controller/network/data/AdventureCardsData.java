package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;
import java.util.ArrayList;

public class AdventureCardsData extends DataContainer implements Serializable {
    private final ArrayList <AdventureCardData> adventureCards;
    private final int nDeck;

    public AdventureCardsData(ArrayList<AdventureCardData> cardsToShow, int nDeck) {
        this.adventureCards = cardsToShow;
        this.nDeck = nDeck;
    }


    public  ArrayList <AdventureCardData> getAdventureCards() {
        return adventureCards;
    }

    public int getnDeck() {
        return nDeck;
    }
}
