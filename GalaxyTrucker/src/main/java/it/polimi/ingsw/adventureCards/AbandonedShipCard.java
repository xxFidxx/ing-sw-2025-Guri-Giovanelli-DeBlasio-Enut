package it.polimi.ingsw.adventureCards;

public class AbandonedShipCard extends AdventureCard  {
    private int lostDays;
    private int lostCrew;
    private int reward;

    public AbandonedShipCard(String name, int level, int lostDays, int lostCrew, int reward) {
        super(name, level);
        this.lostDays = lostDays;
        this.lostCrew = lostCrew;
        this.reward = reward;
    }

    @Override
    public void activate(){

    }

    public int getLostDays(){
        return lostDays;
    }

    public int getLostCrew(){
        return lostCrew;
    }

    public int getReward(){
        return reward;
    }
}
