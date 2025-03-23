package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;

public class StardustCard extends AdventureCard  {
    private int lostDays;

    public StardustCard(String name, int level, int lostDays, Deck deck) {
        super(name, level,deck);
        this.lostDays = lostDays;
    }

    @Override
    public void activate() {

    }

    @Override




    public int getLostDays() {
        return lostDays;
    }
}
