package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

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

    public boolean checkCondition(Player p) {
        return true;
    }

    public abstract void activate();
}
