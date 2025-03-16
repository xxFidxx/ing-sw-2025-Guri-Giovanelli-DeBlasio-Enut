package it.polimi.ingsw.adventureCards;

public class SlaversCard extends EnemyCard{
    private int lostCrew;
    private int reward;

    public SlaversCard(String name, int level, int cannonStrength, int lostDays, int lostCrew, int reward) {
        super(name, level, cannonStrength, lostDays);
        this.lostCrew = lostCrew;
        this.reward = reward;
    }

    public void activate() {

    }

    public int getLostCrew() {
        return lostCrew;
    }

    public int getReward() {
        return reward;
    }
}
