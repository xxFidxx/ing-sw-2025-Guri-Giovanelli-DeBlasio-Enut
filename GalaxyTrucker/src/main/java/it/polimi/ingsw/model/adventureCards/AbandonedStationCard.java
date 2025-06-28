package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.Arrays;

/**
 * Represents an adventure card called AbandonedStationCard, which is a specific type of {@link AdventureCard}.
 * It includes attributes such as lost days, required crew, and rewards, and provides specific behaviors
 * such as activation logic and condition checks.
 */
public class AbandonedStationCard extends AdventureCard {
    /**
     * The number of days lost when the card is activated.
     */
    private int lostDays;

    /**
     * The amount of crew required to activate the card.
     */
    private int requiredCrew;

    /**
     * The rewards associated with the card, represented as an array of {@link GoodsBlock}.
     */
    private GoodsBlock[] reward;

    /**
     * The player who has activated the card.
     */
    private Player activatedPlayer;

    /**
     * Constructor for creating an AbandonedStationCard instance with the specified attributes.
     *
     * @param name the name of the card.
     * @param level the level of the card, representing its difficulty or ranking.
     * @param lostDays the number of days lost when the card is activated.
     * @param requiredCrew the minimum number of crew members required to activate the card.
     * @param reward the rewards granted by the card upon activation.
     */
    public AbandonedStationCard(String name, int level, int lostDays, int requiredCrew, GoodsBlock[] reward) {
        super(name, level);
        this.lostDays = lostDays;
        this.requiredCrew = requiredCrew;
        this.reward = reward;
    }

    /**
     * Sets the player who activates the card.
     *
     * @param activatedPlayer the player activating the card.
     */
    public void setActivatedPlayer(Player activatedPlayer) {
        this.activatedPlayer = activatedPlayer;
    }

    /**
     * Activates the card's effect, applying the reward to the activated player and
     * deducting the lost days from the flight plan.
     */
    @Override
    public void activate() {
        activatedPlayer.setReward(reward);
        deck.getFlightPlance().move(-lostDays, activatedPlayer);
    }

    /**
     * Retrieves the number of crew members required to activate this card.
     *
     * @return the required crew count.
     */
    public int getRequiredCrew() {
        return requiredCrew;
    }

    /**
     * Retrieves the rewards associated with this card.
     *
     * @return the rewards as an array of {@link GoodsBlock}.
     */
    public GoodsBlock[] getReward() {
        return reward;
    }

    /**
     * Retrieves the number of days lost when this card is activated.
     *
     * @return the lost days count.
     */
    public int getLostDays() {
        return lostDays;
    }

    /**
     * Checks if the given player's crew meets the conditions for activating this card.
     *
     * @param p the player attempting to activate the card.
     * @return {@code true} if the player's crew count is greater than or equal to the required crew; {@code false} otherwise.
     */
    public boolean checkCondition(Player p) {
        return p.getSpaceshipPlance().getCrew() >= requiredCrew;
    }

    /**
     * Provides a string representation of the AbandonedStationCard instance.
     *
     * @return a string containing the card's details, such as lost days, required crew, and rewards.
     */
    @Override
    public String toString() {
        return "AbandonedStationCard{" +
                "lostDays=" + lostDays +
                ", requiredCrew=" + requiredCrew +
                ", reward=" + Arrays.toString(reward) +
                '}';
    }
}