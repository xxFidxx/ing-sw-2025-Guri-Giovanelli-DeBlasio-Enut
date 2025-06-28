package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represents an Open Space Card in the game, which is a specific type of {@link AdventureCard}.
 * <p>
 * This card allows the activated player to move through an open space depending on their engine's strength.
 * </p>
 */
public class OpenSpaceCard extends AdventureCard {

    /**
     * The player who activated the card.
     */
    private Player activatedPlayer;

    /**
     * Constructs an Open Space Card with the specified name and level.
     *
     * @param name  The name of the open space card.
     * @param level The level of the open space card.
     */
    public OpenSpaceCard(String name, int level) {
        super(name, level);
    }

    /**
     * Activates the Open Space Card.
     * <p>
     * When activated, this card uses the engine strength of the activated player to perform
     * a move operation on the flight plan. Specific behavior is dependent on the player's attributes
     * and the deck configuration.
     * </p>
     */
    @Override
    public void activate() {
        // Logic for moving through open space using the activated player's engine strength.
        // Example: int power = activatedPlayer.getEngineStrength();
        // deck.getFlightPlan().move(power, activatedPlayer);
    }

    /**
     * Sets the player who activated this card.
     *
     * @param activatedPlayer The {@link Player} who activates the card.
     */
    public void setActivatedPlayer(Player activatedPlayer) {
        this.activatedPlayer = activatedPlayer;
    }
}