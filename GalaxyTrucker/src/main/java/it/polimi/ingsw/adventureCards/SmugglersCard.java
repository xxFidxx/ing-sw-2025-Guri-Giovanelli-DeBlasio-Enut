package it.polimi.ingsw.adventureCards;

public class SmugglersCard extends EnemyCard implements Penalizable, Rewardable{
    private int lossMalus;
    private int reward; // da aggiornare con goods block

    public SmugglersCard(String name, int level, int cannonStrength, int lostDays, int lossMalus, int reward) {
        super(name, level, cannonStrength, lostDays);
        this.lossMalus = lossMalus;
        this.reward = reward;
    }

    public void activate() {

    }

    public void penalize() {}

    public void reward() {}

    public int getLossMalus() {
        return lossMalus;
    }

    public int getReward() {
        return reward;
    }
}
