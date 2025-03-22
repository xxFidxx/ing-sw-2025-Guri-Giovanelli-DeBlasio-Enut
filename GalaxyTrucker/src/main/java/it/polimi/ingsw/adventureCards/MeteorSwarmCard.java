package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;

public class MeteorSwarmCard extends AdventureCard {
    private Projectile[] meteors;

    public MeteorSwarmCard(String name, int level, Projectile[] meteors, Deck deck) {
        super(name, level, deck);
        this.meteors = meteors;
    }
    /**
     *
     * */
    public void activate() {

        for (Projectile meteor : meteors) {
            meteor.activate();
        }
    }

    public Projectile[] getMeteors() {
        return meteors;
    }
}
