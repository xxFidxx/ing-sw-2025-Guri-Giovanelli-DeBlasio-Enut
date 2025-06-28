package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.resources.Planet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Represents a player in the game, storing all attributes and behaviors associated with a player's state during the game.
 * This includes player-specific data such as nickname, spaceship state, score, and game-specific attributes.
 */
public class Player {

    /**
     * The nickname of the player.
     */
    private String nickname;

    /**
     * The player's placeholder representing their position or state in the game.
     */
    private Placeholder placeholder;

    /**
     * The player's spaceship configuration.
     */
    private SpaceshipPlance spaceshipPlance;

    /**
     * The player's total credits, representing their score or economic resources.
     */
    private int credits;

    /**
     * The number of astronauts controlled by the player.
     */
    private int numAstronauts;

    /**
     * The number of aliens controlled by the player.
     */
    private int numAliens;

    /**
     * The current game instance the player is participating in.
     */
    private Game game;

    /**
     * The tile currently held in the player's hand.
     */
    private ComponentTile handTile;

    /**
     * Flag denoting whether the player has responded in the current turn.
     */
    private boolean responded;

    /**
     * The rewards earned by the player.
     */
    private GoodsBlock[] reward;

    /**
     * Flag denoting whether the player has surrendered.
     */
    private boolean surrended;

    /**
     * Constructs a new player for the given game.
     *
     * @param nickname the nickname of the player.
     * @param game the game instance the player is part of.
     * @param playerNumber an identifier for the player.
     */
    public Player(String nickname, Game game, int playerNumber) {
        // Implementation omitted for brevity.
    }

    /**
     * Sets the spaceship configuration for the player.
     *
     * @param spaceshipPlance the spaceship configuration to set.
     */
    public void setSpaceshipPlance(SpaceshipPlance spaceshipPlance) {
        this.spaceshipPlance = spaceshipPlance;
    }

    /**
     * Sets whether the player has surrendered in the game.
     *
     * @param surrended true if the player has surrendered, false otherwise.
     */
    public void setSurrended(boolean surrended) {
        this.surrended = surrended;
    }

    /**
     * Checks if the player has surrendered.
     *
     * @return true if the player has surrendered, false otherwise.
     */
    public boolean isSurrended() {
        return surrended;
    }

    /**
     * Gets the rewards earned by the player.
     *
     * @return an array of {@link GoodsBlock} representing the rewards.
     */
    public GoodsBlock[] getReward() {
        return reward;
    }

    /**
     * Sets the rewards for the player.
     *
     * @param reward the rewards to set.
     */
    public void setReward(GoodsBlock[] reward) {
        this.reward = reward;
    }

    /**
     * Gets the nickname of the player.
     *
     * @return the player's nickname.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Gets the tile currently held by the player.
     *
     * @return the tile in the player's hand.
     */
    public ComponentTile getHandTile() {
        return handTile;
    }

    /**
     * Sets the tile to be held by the player.
     *
     * @param handTile the tile to assign to the player.
     */
    public void setHandTile(ComponentTile handTile) {
        this.handTile = handTile;
    }

    /**
     * Gets the player's placeholder.
     *
     * @return the placeholder.
     */
    public Placeholder getPlaceholder() {
        return placeholder;
    }

    /**
     * Sets the player's placeholder.
     *
     * @param placeholder the placeholder to assign.
     */
    public void setPlaceholder(Placeholder placeholder) {
        this.placeholder = placeholder;
    }

    /**
     * Gets the spaceship configuration for the player.
     *
     * @return the spaceship configuration.
     */
    public SpaceshipPlance getSpaceshipPlance() {
        return spaceshipPlance;
    }

    /**
     * Sets the number of credits for the player.
     *
     * @param credits the credits to assign.
     */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    /**
     * Gets the number of credits owned by the player.
     *
     * @return the player's credits.
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Gets the total number of equipment (astronauts and aliens) the player possesses.
     *
     * @return the total equipment count.
     */
    public int getNumEquip() {
        return numAstronauts + numAliens;
    }

    /**
     * Sets the number of astronauts controlled by the player.
     *
     * @param n the number to set.
     */
    public void setNumEquip(int n) {
        this.numAstronauts = n;
    }

    /**
     * Checks if the player has responded in the current turn.
     *
     * @return true if the player has responded, false otherwise.
     */
    public boolean hasResponded() {
        return responded;
    }

    /**
     * Sets the player's response status for the current turn.
     *
     * @param responded true if the player has responded, false otherwise.
     */
    public void setResponded(boolean responded) {
        this.responded = responded;
    }

    /**
     * Gets the game instance the player is participating in.
     *
     * @return the game instance.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Calculates the engine strength of the player's spaceship.
     *
     * @return the total engine strength.
     */
    public int getEngineStrenght() {
        // Implementation omitted for brevity.
        return 0;
    }

    /**
     * Calculates the firepower strength of the player's spaceship.
     *
     * @return the total firepower strength.
     */
    public float getFireStrenght() {
        // Implementation omitted for brevity.
        return 0.0f;
    }
}