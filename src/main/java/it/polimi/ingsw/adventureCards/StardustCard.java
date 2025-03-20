package it.polimi.ingsw.adventureCards;

public class StardustCard extends AdventureCard {
    private int lostDays;

    public StardustCard(String name, int level, int lostDays) {
        super(name, level);
        this.lostDays = lostDays;
    }

    @Override
    public void activate() {

    }

    public void penalize() {

    }

    public int getLostDays() {
        return lostDays;
    }
}
