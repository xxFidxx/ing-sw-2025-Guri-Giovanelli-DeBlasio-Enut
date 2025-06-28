package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.AdventureCardFactory;
import it.polimi.ingsw.model.adventureCards.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * The {@code Flightplance} class represents the flight plan of the game,
 * associating players with their placeholders and managing the game deck and player movement.
 */
public class Flightplance {

    /**
     * Array of {@link Placeholder} objects representing the positions on the flight plan.
     */
    final private Placeholder[] spots;

    /**
     * The {@link Deck} containing the adventure cards for this game.
     */
    private Deck deck;

    /**
     * The {@link Game} instance associated with this flight plan.
     */
    final private Game game;

    /**
     * A mapping between each {@link Player} and their associated {@link Placeholder}.
     */
    final private Map<Player, Placeholder> placeholderByPlayer;

    /**
     * Constructs a new {@code Flightplance} instance.
     *
     * @param spots   Number of positions available on the flight plan.
     * @param game    The {@link Game} instance this flight plan belongs to.
     * @param players The list of {@link Player}s participating in the game.
     */
    public Flightplance(int spots, Game game, ArrayList<Player> players) {
        this.spots = new Placeholder[spots];
        this.placeholderByPlayer = new HashMap<>();
        this.game = game;

        // Initialize placeholders for each player
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            placeholderByPlayer.put(player, player.getPlaceholder());
            this.spots[i] = player.getPlaceholder();
        }

        try {
            // Load cards and initialize the deck
            List<AdventureCard> cards = AdventureCardFactory.loadCards(this.game);
            this.deck = new Deck(cards, this);
        } catch (Exception e) {
            System.err.println("Failed to load cards: " + e.getMessage());
        }
    }

    /**
     * Returns the {@link Placeholder} associated with the given {@link Player}.
     *
     * @param player The player whose placeholder is requested.
     * @return The placeholder associated with the given player, or {@code null} if none exists.
     */
    public Placeholder getPlaceholderByPlayer(Player player) {
        return placeholderByPlayer.get(player);
    }

    /**
     * Returns the current {@link Deck} of adventure cards.
     *
     * @return The deck of adventure cards.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Gets the next {@link Player} in the game after the specified player.
     *
     * @param player The current player.
     * @return An {@code Optional} containing the next player, or an empty {@code Optional} if no next player is available.
     */
    public Optional<Player> getNext(Player player) {
        return Optional.empty(); // Placeholder logic
    }

    /**
     * Gets the first player based on their position in the flight plan.
     *
     * @return An {@code Optional} containing the first player, or an empty {@code Optional} if no players are present.
     */
    public Optional<Player> getFirst() {
        return game.getPlayers().stream().max(Comparator.comparingInt(p -> p.getPlaceholder().getPosizione()));
    }

    /**
     * Returns the array of {@link Placeholder} objects representing the flight plan spots.
     *
     * @return The array of placeholders.
     */
    public Placeholder[] getSpots() {
        return spots;
    }

    /**
     * Returns the {@link Game} instance associated with this flight plan.
     *
     * @return The game object.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Moves the given player by the specified number of steps, considering additional steps if
     * crossing other players in the path.
     *
     * @param num          The number of steps to move.
     * @param chosenPlayer The {@link Player} to move.
     */
    public void move(int num, Player chosenPlayer) {
        int extraSteps = 0;
        ArrayList<Player> players = game.getPlayers();
        int chosenPlayerPosition = chosenPlayer.getPlaceholder().getPosizione();

        // Calculate extra steps based on crossing other players
        if (num > 0) {
            for (Player p : players) {
                int playerPosition = p.getPlaceholder().getPosizione();
                if (playerPosition > chosenPlayerPosition && playerPosition <= chosenPlayerPosition + num)
                    extraSteps++;
            }
        } else if (num < 0) {
            for (Player p : players) {
                int playerPosition = p.getPlaceholder().getPosizione();
                if (playerPosition >= chosenPlayerPosition + num && playerPosition < chosenPlayerPosition)
                    extraSteps--;
            }
        }

        // Move the chosen player
        chosenPlayer.getPlaceholder().setPosizione(chosenPlayer.getPlaceholder().getPosizione() + num + extraSteps);

        // Update the player order in the game
        game.orderPlayers();
    }
}