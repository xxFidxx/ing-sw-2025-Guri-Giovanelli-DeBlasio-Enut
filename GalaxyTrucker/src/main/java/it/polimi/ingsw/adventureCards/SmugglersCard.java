package it.polimi.ingsw.adventureCards;
import it.polimi.ingsw.Bank.GoodsBlock;

public class SmugglersCard extends EnemyCard implements Penalizable, Rewardable{
    private int lossMalus;
    private GoodsBlock[] reward; // da aggiornare con goods block

    public SmugglersCard(String name, int level, int cannonStrength, int lostDays, int lossMalus, GoodsBlock[] reward) {
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

    public GoodsBlock[] getReward() {
        return reward;
    }
}
