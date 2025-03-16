package it.polimi.ingsw.adventureCards;

public class SmugglersCard extends EnemyCard{
    private int lossMalus;
    private int reward; // da aggiornare con goods block

    public SmugglersCard(String name, int level, int cannonStrength, int lostDays, int lossMalus, int reward) {
        super(name, level, cannonStrength, lostDays);
        this.lossMalus = lossMalus;
        this.reward = reward;
    }

    public void activate() {

    }

    public int getLossMalus() {
        return lossMalus;
    }

    public int getReward() {
        return reward;
    }
}
