package it.polimi.ingsw.adventureCards;

public abstract class AdventureCard {
    private String name;
    private int level;

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

    public abstract void activate();
}
