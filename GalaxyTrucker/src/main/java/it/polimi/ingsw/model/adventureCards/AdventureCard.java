package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.io.Serializable;

/**
 * Represents an abstract Adventure Card in the game.
 * <p>
 * Adventure Cards are central elements of the game with properties such as a name, level, and associated deck.
 * Each card must implement its own specific activation behavior through the {@link #activate()} method.
 * </p>
 */
public abstract class AdventureCard {
    /**
     * The name of the adventure card.
     */
    private String name;

    /**
     * The level of the adventure card.
     */
    private int level;

    /**
     * The deck to which this card belongs. Can be set dynamically.
     */
    protected Deck deck;

    /**
     * Constructs an Adventure Card with the specified name and level.
     *
     * @param name  The name of the card.
     * @param level The level of the card.
     */
    public AdventureCard(String name, int level) {
        this.name = name;
        this.level = level;
    }

    /**
     * Returns the name of this adventure card.
     *
     * @return The name of the card.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the level of this adventure card.
     *
     * @return The level of the card.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the deck to which this adventure card belongs.
     *
     * @param deck The {@link Deck} containing this card.
     */
    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    /**
     * Checks if a specified condition is met for the given player.
     * By default, this method always returns {@code true},
     * but subclasses can override it to define custom conditions.
     *
     * @param p The player for which the condition needs to be checked.
     * @return {@code true} if the condition is met; otherwise, {@code false}.
     */
    public boolean checkCondition(Player p) {
        return true;
    }

    /**
     * Activates the effect of the adventure card.
     * Subclasses must implement this method to define the activation behavior.
     */
    public abstract void activate();
}