package it.polimi.ingsw.adventureCards;

public class SlaversCard extends EnemyCard implements Penalizable, Rewardable {
    private int lostCrew;
    private int reward;

    public SlaversCard(String name, int level, int cannonStrength, int lostDays, int lostCrew, int reward) {
        super(name, level, cannonStrength, lostDays);
        this.lostCrew = lostCrew;
        this.reward = reward;
    }

    public void activate() {

    }

    public void penalize() {}

    public void reward() {}

    public int getLostCrew() {
        return lostCrew;
    }

    public int getReward() {
        return reward;
    }
}
