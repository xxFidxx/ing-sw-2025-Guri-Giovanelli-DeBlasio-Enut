package it.polimi.ingsw.adventureCards;

public class StardustCard extends AdventureCard implements Penalizable {
    private int lostDays;

    public StardustCard(String name, int level, int lostDays) {
        super(name, level);
        this.lostDays = lostDays;
    }

    @Override
    public void activate() {

    }

    @Override
    public void penalize() {

    }

    public int getLostDays() {
        return lostDays;
    }
}
