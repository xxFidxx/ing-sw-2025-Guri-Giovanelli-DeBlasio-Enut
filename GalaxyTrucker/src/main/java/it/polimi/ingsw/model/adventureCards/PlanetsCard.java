package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.resources.Planet;

import java.util.*;

/**
 * The {@code PlanetsCard} class represents a card in the game related to exploring planets.
 * It extends the {@link AdventureCard} class and manages interactions with planets,
 * actions performed by players, and effects that impact gameplay, like lost days.
 */
public class PlanetsCard extends AdventureCard {
    private ArrayList<Planet> planets; // List of planets affected by the card.
    private int lostDays; // Days lost when the card is activated.
    private Player activatedPlayer; // The player that activated this card.
    private Planet chosenPlanet; // The planet chosen for activation.

    /**
     * Constructs a PlanetsCard with the specified parameters.
     *
     * @param name      the name of the card
     * @param level     the level of the card
     * @param planets   a list of planets associated with the card
     * @param lostDays  the number of days lost when activated
     */
    public PlanetsCard(String name, int level, ArrayList<Planet> planets, int lostDays) {
        super(name, level);
        this.lostDays = lostDays;
        this.planets = planets;
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
     * Sets the planet chosen during the card's activation.
     *
     * @param chosenPlanet the planet selected by the player
     */
    public void setChosenPlanet(Planet chosenPlanet) {
        this.chosenPlanet = chosenPlanet;
    }

    /**
     * Activates the card, applying its effects. This includes:
     * - Setting a reward to the activated player based on the chosen planet.
     * - Moving the flight plan's position based on the number of lost days.
     *
     * Overrides {@link AdventureCard#activate()}.
     */
    @Override
    public void activate() {
        activatedPlayer.setReward(chosenPlanet.getReward());
        deck.getFlightPlance().move(-lostDays, activatedPlayer);
    }

    /**
     * Gets the number of days lost due to the card's effects.
     *
     * @return the number of lost days
     */
    public int getLostDays() {
        return lostDays;
    }

    /**
     * Retrieves the list of planets associated with the card.
     *
     * @return a list of {@link Planet} objects
     */
    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    /**
     * Checks whether the card's condition is met, which is true if
     * at least one planet is not busy (unoccupied).
     *
     * @return {@code true} if not all planets are busy, {@code false} otherwise
     */
    public boolean checkCondition() {
        return !planets.stream().allMatch(Planet::isBusy);
    }

    /**
     * Returns a string representation of the card, including information
     * about the associated planets and the number of lost days.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "PlanetsCard{" +
                "planets=" + planets +
                ", lostDays=" + lostDays +
                '}';
    }
}