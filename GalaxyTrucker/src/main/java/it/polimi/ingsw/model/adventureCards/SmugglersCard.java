package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.Arrays;

/**
 * The {@code SmugglersCard} class represents a type of {@link EnemyCard} that
 * has specific behaviors including providing a reward and a loss malus.
 *
 * This card can reward or penalize a {@link Player} based on its properties.
 */
public class SmugglersCard extends EnemyCard {
    private int lossMalus;
    private GoodsBlock[] reward;

    /**
     * Constructs a SmugglersCard object with the specified parameters.
     *
     * @param name the name of the card
     * @param level the level of the card
     * @param cannonStrength the cannon strength associated with the card
     * @param lostDays the number of lost days caused by this card
     * @param lossMalus the malus applied when the card is lost
     * @param reward an array of {@link GoodsBlock} representing the reward of this card
     */
    public SmugglersCard(String name, int level, int cannonStrength, int lostDays, int lossMalus, GoodsBlock[] reward) {
        super(name, level, cannonStrength, lostDays);
        this.lossMalus = lossMalus;
        this.reward = reward;
    }

    /**
     * Rewards the specified player by setting the reward associated with this card.
     *
     * @param player the player to be rewarded
     */
    @Override
    public void reward(Player player) {
        player.setReward(reward);
    }

    /**
     * Penalizes the specified player. This method currently has no implementation.
     *
     * @param player the player to be penalized
     */
    @Override
    public void penalize(Player player) {
    }

    /**
     * Returns the loss malus of this card.
     *
     * @return the loss malus
     */
    public int getLossMalus() {
        return lossMalus;
    }

    /**
     * Returns the reward of this card as an array of {@link GoodsBlock}.
     *
     * @return the reward
     */
    public GoodsBlock[] getReward() {
        return reward;
    }

    /**
     * Returns a string representation of the SmugglersCard.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return super.toString() + "SmugglersCard{" +
                "lossMalus=" + lossMalus +
                ", reward=" + Arrays.toString(reward) +
                '}';
    }
}