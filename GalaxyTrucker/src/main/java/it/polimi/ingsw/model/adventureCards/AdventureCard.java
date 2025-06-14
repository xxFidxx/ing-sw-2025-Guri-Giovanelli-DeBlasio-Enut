package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.io.Serializable;

public abstract class AdventureCard implements Serializable {
    private String name;
    private int level;
    protected Deck deck;

    public AdventureCard(String name, int level, Deck deck) {
        this.name = name;
        this.level = level;
        this.deck = deck;
    }

    public AdventureCard(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public boolean checkCondition(Player p) {
        return true;
    }

    public abstract void activate();
}
