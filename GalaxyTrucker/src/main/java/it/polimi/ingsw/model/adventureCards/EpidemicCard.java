package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.componentTiles.Cabin;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;

/**
 * Represents an Epidemic Card in the game, which is a specific type of {@link AdventureCard}.
 * <p>
 * When activated, this card triggers an epidemic effect, potentially removing a member
 * from each interconnected cabin during gameplay.
 * </p>
 */
public class EpidemicCard extends AdventureCard {

    /**
     * Constructs an Epidemic Card with the given name and level.
     *
     * @param name  The name of the epidemic card.
     * @param level The level of the epidemic card.
     */
    public EpidemicCard(String name, int level) {
        super(name, level);
    }

    /**
     * Activates the effect of the Epidemic Card.
     * <p>
     * When this card is activated, it removes a member from each cabin that is interconnected.
     * The specific behavior of this effect is implemented during gameplay.
     * </p>
     */
    @Override
    public void activate() {
        // Logic for removing a member from each interconnected cabin during gameplay.
    }
}