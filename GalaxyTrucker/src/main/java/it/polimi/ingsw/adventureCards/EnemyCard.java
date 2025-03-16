package it.polimi.ingsw.adventureCards;

public abstract class EnemyCard extends AdventureCard {
    private final int cannonStrength;
    private final int lostDays;

    public EnemyCard(String name, int level, int cannonStrength, int lostDays) {
        super(name, level);
        this.cannonStrength = cannonStrength;
        this.lostDays = lostDays;
    }

    public int getCannonStrength() {
        return cannonStrength;
    }

    public int getLostDays() {
        return lostDays;
    }
}
