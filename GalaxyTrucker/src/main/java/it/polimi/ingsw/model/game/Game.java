/**
 * The {@code Game} class is responsible for managing the core logic of the game.
 * It handles players, dice rolling, component tile management, player actions,
 * and game rules such as rewards and penalties.
 * This class serves as the main controller for the game flow.
 */
package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ComponentTileFactory;
import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.resources.Planet;

import java.io.Serializable;
import java.util.*;

public class Game {
    /**
     * List of players participating in the game.
     */
    private ArrayList<Player> players;

    /**
     * The game's main timer, which controls the time allocated for various actions.
     */
    final private Timer timer;

    /**
     * Array of dice used in the game for random events.
     */
    final private Dice[] dices;

    /**
     * The game's flight planning board.
     */
    final private Flightplance plance;

    /**
     * Array of assembling tiles available to players during the game.
     */
    private ComponentTile[] assemblingTiles;

    /**
     * Initializes a new {@code Game} instance with the provided player names.
     *
     * @param playersName List of player names to initialize the game.
     */
    public Game(ArrayList<String> playersName) {
        this.players = new ArrayList<>();
        for (int i = 0; i < playersName.size(); i++) {
            this.players.add(new Player(playersName.get(i), this, i));
        }
        this.timer = new Timer(60);
        this.dices = new Dice[2];
        dices[0] = new Dice();
        dices[1] = new Dice();
        this.plance = new Flightplance(playersName.size(), this, players);

        try {
            List<ComponentTile> assemblingTilesList = ComponentTileFactory.loadTiles(this);
            assert assemblingTilesList != null;
            this.assemblingTiles = assemblingTilesList.toArray(ComponentTile[]::new);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the list of players. This method is primarily used for testing purposes.
     *
     * @param players The list of players to set.
     */
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    /**
     * Gets the list of players.
     *
     * @return The list of players.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the array of assembling tiles available in the game.
     *
     * @return The array of assembling tiles.
     */
    public ComponentTile[] getAssemblingTiles() {
        return assemblingTiles;
    }

    /**
     * Converts the array of assembling tiles to their respective IDs.
     *
     * @return Array of tile IDs.
     */
    public Integer[] getTilesId() {
        return tilesToId(assemblingTiles);
    }

    /**
     * Gets the array of dice used in the game.
     *
     * @return The array of dice.
     */
    public Dice[] getDice() {
        return dices;
    }

    /**
     * Gets the game's timer.
     *
     * @return The timer instance.
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * Gets the game's flight planning board (Flightplance).
     *
     * @return The flight planning board.
     */
    public Flightplance getFlightplance() {
        return plance;
    }

    /**
     * Rolls both dice and returns their total sum.
     *
     * @return The sum of the two dice rolls.
     */
    public int throwDices() {
        return dices[0].thr() + dices[1].thr();
    }

    /**
     * Checks if there are free planets available for a given adventure card.
     *
     * @param card    The adventure card being played.
     * @param planets List of planets to check.
     * @return {@code true} if there are free planets; {@code false} otherwise.
     */
    public boolean freePlanets(AdventureCard card, ArrayList<Planet> planets) {
        for (Planet planet : planets) {
            if (!planet.isBusy())
                return true;
        }
        return false;
    }

    /**
     * Orders the players based on their positions in the game.
     */
    public void orderPlayers() {
        players.sort(Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
    }

    /**
     * Allows a player to pick a tile from the assembling tiles.
     *
     * @param player The player selecting a tile.
     * @param Tileid The ID of the tile to pick.
     * @return The selected tile.
     */
    public ComponentTile pickTile(Player player, int Tileid) {
        ComponentTile tile;
        synchronized (assemblingTiles) {
            tile = assemblingTiles[Tileid];
        }

        if (assemblingTiles[Tileid] == null)
            return null;

        player.setHandTile(tile);
        assemblingTiles[Tileid] = null;
        return tile;
    }

    /**
     * Allows a player to pick a tile from their reserve spot.
     *
     * @param player    The player selecting a tile.
     * @param tileIndex The index of the tile in the reserve spot.
     * @return The selected tile.
     */
    public ComponentTile pickTileReserveSpot(Player player, int tileIndex) {
        List<ComponentTile> reserve = player.getSpaceshipPlance().getReserveSpot();
        ComponentTile tile = (tileIndex >= 0 && tileIndex < reserve.size()) ? reserve.get(tileIndex) : null;

        if (tile == null)
            return null;

        player.setHandTile(tile);
        player.getSpaceshipPlance().getReserveSpot().remove(tile);

        return tile;
    }

    /**
     * Converts a list of tiles into their respective IDs.
     *
     * @param tiles Array of component tiles.
     * @return Array of tile IDs.
     */
    public Integer[] tilesToId(ComponentTile[] tiles) {
        Integer[] ids = new Integer[tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            ComponentTile tile = tiles[i];
            if (tile != null)
                ids[i] = tiles[i].getId();
            else
                ids[i] = null;
        }
        return ids;
    }

    /**
     * Resets the "responded" status of all players.
     */
    public void resetResponded() {
        for (Player p : players) {
            p.setResponded(false);
        }
    }

    // Additional methods (e.g., swapGoods, add/removeGood, endTurn, etc.) will follow similar patterns
    // to the provided documentation, ensuring clarity and consistency throughout the class documentation.
    // Each method will be documented explaining its purpose and parameters.
}