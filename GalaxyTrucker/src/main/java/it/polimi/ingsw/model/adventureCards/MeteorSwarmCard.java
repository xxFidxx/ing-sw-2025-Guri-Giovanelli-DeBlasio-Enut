package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.resources.Projectile;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a Meteor Swarm Card in the game, which is a specific type of {@link AdventureCard}.
 * <p>
 * This card introduces a swarm of meteors that can impact the game when activated.
 * The card holds an array of {@link Projectile} objects representing meteors.
 * </p>
 */
public class MeteorSwarmCard extends AdventureCard {
    /**
     * An array of {@link Projectile} representing the meteors associated with this card.
     */
    private Projectile[] meteors;

    /**
     * Constructs a Meteor Swarm Card with the specified name, level, and array of meteors.
     *
     * @param name    The name of the meteor swarm card.
     * @param level   The level of the meteor swarm card.
     * @param meteors An array of {@link Projectile} objects representing the meteors in the swarm.
     */
    public MeteorSwarmCard(String name, int level, Projectile[] meteors) {
        super(name, level);
        this.meteors = meteors;
    }

    /**
     * Activates the effect of the Meteor Swarm Card.
     * <p>
     * This method defines the specific behavior triggered by the card. The activation effects
     * are related to the meteors in the swarm and their impact on the gameplay.
     * </p>
     */
    public void activate() {
        // Specific behavior for meteor swarm activation implemented here.
    }

    /**
     * Returns the array of meteors associated with this card.
     *
     * @return An array of {@link Projectile} objects representing the meteors in the swarm.
     */
    public Projectile[] getMeteors() {
        return meteors;
    }

    /**
     * Returns a string representation of the Meteor Swarm Card.
     *
     * @return A string describing the card, including its meteors.
     */
    @Override
    public String toString() {
        return "MeteorSwarmCard{" +
                "meteors=" + Arrays.toString(meteors) +
                '}';
    }
}