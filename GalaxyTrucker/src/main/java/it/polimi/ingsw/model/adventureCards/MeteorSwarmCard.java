package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.resources.Projectile;

import java.util.ArrayList;
import java.util.Arrays;

public class MeteorSwarmCard extends AdventureCard {
    private Projectile[] meteors;

    public MeteorSwarmCard(String name, int level, Projectile[] meteors, Deck deck) {
        super(name, level, deck);
        this.meteors = meteors;
    }

    public MeteorSwarmCard(String name, int level, Projectile[] meteors) {
        super(name, level);
        this.meteors = meteors;
    }

    public void activate() {

    }

    public Projectile[] getMeteors() {
        return meteors;
    }

    @Override
    public String toString() {
        return "MeteorSwarmCard{" +
                "meteors=" + Arrays.toString(meteors) +
                '}';
    }
}
