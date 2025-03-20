package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;

public abstract class AdventureCard {
    private String name;
    private int level;
    protected Deck deck;

    public AdventureCard(String name, int level, Deck deck) {
        this.name = name;
        this.level = level;
        this.deck = deck;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public boolean checkCondition() {
        return true;
    }

    public abstract void activate();
}
