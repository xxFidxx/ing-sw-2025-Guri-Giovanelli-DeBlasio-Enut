package it.polimi.ingsw.adventureCards;

public class PiratesCard extends EnemyCard {
    private CannonFire[] shots; //liste o array?
    private int reward;

    public PiratesCard(String name, int level, int cannonStrength, int lostDays, CannonFire[] shots, int reward) {
        super(name, level, cannonStrength, lostDays);
        this.shots = shots;
        this.reward = reward;
    }

    public void activate() {

    }



    public CannonFire[] getShots() {
        return shots;
    }

    public int getReward() {
        return reward;
    }
}
