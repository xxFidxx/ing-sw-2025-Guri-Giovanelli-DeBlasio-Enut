package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.resources.Projectile;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a specific type of {@link EnemyCard}, the PiratesCard.
 * <p>
 * This card introduces a pirate-themed challenge in the game containing details
 * about projectiles fired by the pirates and the rewards granted upon victory.
 * </p>
 */
public class PiratesCard extends EnemyCard {
    private Projectile[] shots; // Array holding the projectiles fired by the pirates.
    private int reward; // Reward value that the player will receive if they win.

    /**
     * Constructs a PiratesCard with the specified attributes.
     *
     * @param name           the name of the card
     * @param level          the difficulty level of the card
     * @param cannonStrength the strength of the pirate cannons
     * @param lostDays       the number of days lost if defeated
     * @param shots          an array of {@link Projectile} objects representing pirate attacks
     * @param reward         the reward for defeating the pirates
     */
    public PiratesCard(String name, int level, int cannonStrength, int lostDays, Projectile[] shots, int reward) {
        super(name, level, cannonStrength, lostDays);
        this.shots = shots;
        this.reward = reward;
    }

    /**
     * Rewards the player with the specified amount of credits from this card's reward.
     *
     * @param player the {@link Player} to reward
     */
    @Override
    public void reward(Player player) {
        player.setCredits(player.getCredits() + reward);
    }

    /**
     * Penalizes the player should they lose to the pirates.
     * The implementation is currently left empty and can be customized based on game rules.
     *
     * @param player the {@link Player} to penalize
     */
    @Override
    public void penalize(Player player) {
        // Implementation can be added as per game rules.
    }

    /**
     * Returns the array of shots (projectiles) fired by the pirates.
     *
     * @return an array of {@link Projectile} objects
     */
    public Projectile[] getShots() {
        return shots;
    }

    /**
     * Returns the reward the player earns for defeating the pirates.
     *
     * @return the reward value as an integer
     */
    public int getReward() {
        return reward;
    }

    /**
     * Provides a string representation of the PiratesCard object.
     * Includes details about the shots and reward.
     *
     * @return a string representation of the card
     */
    @Override
    public String toString() {
        return "PiratesCard{" +
                "shots=" + Arrays.toString(shots) +
                ", reward=" + reward +
                '}';
    }
}