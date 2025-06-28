package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.resources.CombatZoneType;
import it.polimi.ingsw.model.resources.Projectile;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

import java.util.*;

/**
 * Represents a combat zone card in the game, which details penalties and other characteristics
 * associated with a specific type of combat zone.
 * Extends the {@link AdventureCard}.
 */
public class CombatZoneCard extends AdventureCard {

    private int lostDays; // Days lost due to this card
    private CombatZoneType type; // Type of combat zone
    private int lostOther; // Other penalties
    private Projectile[] cannons; // Projectiles associated with this card

    /**
     * Constructs a new CombatZoneCard with the specified attributes.
     *
     * @param name      the name of the card
     * @param level     the level of the combat zone card
     * @param lostDays  the number of days lost because of this card
     * @param lostOther the amount of other penalties inflicted by this card
     * @param cannons   an array of {@link Projectile}s used in the combat zone
     * @param type      the {@link CombatZoneType} of this combat zone card
     */
    public CombatZoneCard(String name, int level, int lostDays, int lostOther, Projectile[] cannons, CombatZoneType type) {
        super(name, level);
        this.lostDays = lostDays;
        this.lostOther = lostOther;
        this.cannons = cannons;
        this.type = type;
    }

    /**
     * Activates the effects of this combat zone card.
     * <p>This method should be implemented to define the specific effect
     * of the card within the game logic.</p>
     */
    public void activate() {
        // Implementation of card activation logic
    }

    /**
     * Gets the number of days lost due to this combat zone card.
     *
     * @return the number of lost days
     */
    public int getLostDays() {
        return lostDays;
    }

    /**
     * Gets the amount of other penalties inflicted by this card.
     *
     * @return the amount of lost other penalties
     */
    public int getLostOther() {
        return lostOther;
    }

    /**
     * Gets the array of projectiles (cannons) associated with this combat zone card.
     *
     * @return an array of {@link Projectile}s
     */
    public Projectile[] getCannons() {
        return cannons;
    }

    /**
     * Gets the type of this combat zone card.
     *
     * @return the {@link CombatZoneType} of the card
     */
    public CombatZoneType getType() {
        return type;
    }
}