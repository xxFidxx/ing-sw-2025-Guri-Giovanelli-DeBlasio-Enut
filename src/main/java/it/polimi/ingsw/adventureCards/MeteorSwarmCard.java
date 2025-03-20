package it.polimi.ingsw.adventureCards;

public class MeteorSwarmCard extends AdventureCard{
    private final Meteor[] meteors;

    public MeteorSwarmCard(String name, int level, Meteor[] meteors) {
        super(name, level);
        this.meteors = meteors;
    }

    public void activate() {

    }

    public void penalize() {}

    public Meteor[] getMeteors() {
        return meteors;
    }
}
