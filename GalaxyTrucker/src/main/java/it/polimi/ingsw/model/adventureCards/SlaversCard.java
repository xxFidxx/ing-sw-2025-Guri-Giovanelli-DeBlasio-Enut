package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

/**
 * Represents a card of type "SlaversCard" in the adventure game.
 * This card is a specific type of enemy card with additional effects
 * such as lost crew and reward for the player.
 */
public class SlaversCard extends EnemyCard {

    /**
     * The number of crew members lost as a penalty when this card is encountered.
     */
    private int lostCrew;

    /**
     * The reward associated with this card, typically added to the player's credits.
     */
    private int reward;

    /**
     * Constructs a SlaversCard.
     *
     * @param name           the name of the card
     * @param level          the difficulty level of the card
     * @param cannonStrength the cannon strength of the enemy on the card
     * @param lostDays       the number of days lost when encountering the card
     * @param lostCrew       the number of crew members lost as a penalty
     * @param reward         the reward acquired after dealing with the card
     */
    public SlaversCard(String name, int level, int cannonStrength, int lostDays, int lostCrew, int reward) {
        super(name, level, cannonStrength, lostDays);
        this.lostCrew = lostCrew;
        this.reward = reward;
    }

    /**
     * Applies a reward to the specified player. The player's credits are
     * increased by the amount specified in the reward of this card.
     *
     * @param player the player who receives the reward
     */
    @Override
    public void reward(Player player) {
        player.setCredits(player.getCredits() + reward);
    }

    /**
     * Applies a penalty to the specified player. This is currently not implemented.
     *
     * @param player the player who receives the penalty
     */
    @Override
    public void penalize(Player player) {

    }

    /**
     * Gets the number of crew members lost when this card is encountered.
     *
     * @return the number of lost crew members
     */
    public int getLostCrew() {
        return lostCrew;
    }

    /**
     * Gets the reward associated with this card.
     *
     * @return the reward amount
     */
    public int getReward() {
        return reward;
    }
}