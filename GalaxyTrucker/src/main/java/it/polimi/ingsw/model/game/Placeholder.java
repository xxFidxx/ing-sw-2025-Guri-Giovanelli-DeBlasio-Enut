package it.polimi.ingsw.model.game;

/**
 * The {@code Placeholder} class represents an object that serves as a placeholder
 * for a player's position and color in the game.
 *
 * Each instance of this class is associated with a specific player, identified by their order
 * in the game, and it assigns the player's color accordingly.
 */
public class Placeholder {
    /**
     * The color associated with the placeholder instance.
     */
    private final ColorType color;

    /**
     * The position of the player in the game.
     */
    private int posizione;

    /**
     * Creates a new {@code Placeholder} for a player.
     * The player's color is assigned based on their position in the order of play.
     *
     * @param numPlayer The order of the player (starting from 0).
     *                  This value is used to assign a color from {@link ColorType}.
     */
    public Placeholder(int numPlayer) {
        // Assegna il colore in base all'ordine del giocatore (numPlayer)
        this.color = ColorType.values()[numPlayer];
        this.posizione = 0; // Posizione iniziale
    }

    /**
     * Returns the color associated with this placeholder.
     *
     * @return the {@link ColorType} of the placeholder.
     */
    public ColorType getColor() {
        return color;
    }

    /**
     * Returns the current position of the player in the game.
     *
     * @return the position of the player.
     */
    public int getPosizione() {
        return posizione;
    }

    /**
     * Updates the player's position in the game.
     *
     * @param posizione the new position to set.
     */
    public void setPosizione(int posizione) {
        this.posizione = posizione;
    }
}