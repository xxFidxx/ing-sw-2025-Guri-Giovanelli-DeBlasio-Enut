package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Represents a Stardust Card in the game, which is a specific type of {@link AdventureCard}.
 * Stardust cards can be activated and have specific properties such as a name and a level, inherited from the parent class.
 */
public class StardustCard extends AdventureCard {

    /**
     * Constructs a new Stardust Card with the specified name, level, and lost days.
     *
     * @param name     The name of the Stardust Card.
     * @param level    The level of the Stardust Card.
     * @param lostDays This parameter is unused in the current implementation,
     *                 but it may represent some form of penalty or additional data related to the card.
     */
    public StardustCard(String name, int level, int lostDays) {
        super(name, level);
    }

    /**
     * Activates the Stardust Card.
     * <p>
     * This method provides the activation behavior of the card. Currently, it is not implemented,
     * but subclasses or future extensions can define specific activation effects.
     * </p>
     */
    @Override
    public void activate() {
        // Activation logic to be implemented in the future.
    }
}