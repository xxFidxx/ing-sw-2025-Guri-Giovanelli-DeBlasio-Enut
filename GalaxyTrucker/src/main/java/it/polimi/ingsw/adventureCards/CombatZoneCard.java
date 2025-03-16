package it.polimi.ingsw.adventureCards;

public class CombatZoneCard extends AdventureCard {
    private int lostDays;
    private CombatZoneType type;
    private int lostOther;
    private CannonFire[] shots; //liste o array?

    public CombatZoneCard(String name, int level, int lostDays, int lostOther, CannonFire[] shots) {
        super(name, level);
        this.lostDays = lostDays;
        this.lostOther = lostOther;
        this.shots = shots;
    }

    public void activate() {

    }

    public CannonFire[] getShots() {
        return shots;
    }

    public int getLostDays() {
        return lostDays;
    }

    public int getLostOther() {
        return lostOther;
    }
}
