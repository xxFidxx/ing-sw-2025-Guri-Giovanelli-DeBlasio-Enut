package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

/**
 * This class represents an Abandoned Ship Card in the game.
 * It contains information about the penalties (lost days, lost crew)
 * as well as rewards (credits) associated with the card.
 * The card can be activated by a player if the specified conditions are met.
 */
public class AbandonedShipCard extends AdventureCard {
    private int lostDays;
    private int lostCrew;
    private int credits;
    private Player activatedPlayer;

    /**
     * Creates a new AbandonedShipCard with the specified parameters.
     *
     * @param name      the name of the card
     * @param level     the level of the card
     * @param lostDays  the number of days lost when the card is activated
     * @param lostCrew  the number of crew members lost when the card is activated
     * @param credits   the amount of credits gained when the card is activated
     */
    public AbandonedShipCard(String name, int level, int lostDays, int lostCrew, int credits) {
        super(name, level);
        this.lostDays = lostDays;
        this.lostCrew = lostCrew;
        this.credits = credits;
    }

    /**
     * Activates the card, applying its effects to the activated player.
     * The player gains credits, and the flight plan is modified by the lost days.
     */
    public void activate() {
        activatedPlayer.setCredits(activatedPlayer.getCredits() + credits);
        deck.getFlightPlance().move(-lostDays, activatedPlayer);
    }

    /**
     * Sets the player who activated the card.
     *
     * @param activatedPlayer the player who activates the card
     */
    public void setActivatedPlayer(Player activatedPlayer) {
        this.activatedPlayer = activatedPlayer;
    }

    /**
     * Gets the number of days lost when activating the card.
     *
     * @return the lost days
     */
    public int getLostDays() {
        return lostDays;
    }

    /**
     * Gets the number of crew members lost when activating the card.
     *
     * @return the lost crew
     */
    public int getLostCrew() {
        return lostCrew;
    }

    /**
     * Gets the reward (credits) gained when activating the card.
     *
     * @return the amount of credits
     */
    public int getReward() {
        return credits;
    }

    /**
     * Checks the condition for activating the card.
     * The condition is met if the player's spaceship has enough crew.
     *
     * @param p the player to check
     * @return true if the player's spaceship has enough crew, false otherwise
     */
    public boolean checkCondition(Player p) {
        return p.getSpaceshipPlance().getCrew() >= lostCrew;
    }
}