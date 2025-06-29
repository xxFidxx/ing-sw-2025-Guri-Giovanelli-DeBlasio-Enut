package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ComponentTileFactory;
import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.resources.Planet;

import java.io.Serializable;
import java.util.*;


public class Game{
    private ArrayList<Player> players;
    final private Timer timer;
    final private Dice[] dices;
    final private Flightplance plance;
    private ComponentTile[] assemblingTiles;

    public Game(ArrayList<String> playersName) {
        this.players = new ArrayList<>();
        for (int i = 0; i < playersName.size(); i++) {
            this.players.add(new Player(playersName.get(i), this, i));
        }
        this.timer = new Timer(60);
        this.dices = new Dice[2];
        dices[0] = new Dice();
        dices[1] = new Dice();
        // gli spots dipenderanno dalla lobby size
        this.plance = new Flightplance(playersName.size(), this, players);

        // Prima definiamo i connettori per i componenti
        ConnectorType[] cannonConnectors = {
                ConnectorType.CANNON,   // Lato superiore
                ConnectorType.SMOOTH,   // Lato destro
                ConnectorType.SMOOTH,   // Lato inferiore
                ConnectorType.SMOOTH    // Lato sinistro
        };

        ConnectorType[] cargoConnectors = {
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH,
                ConnectorType.SMOOTH
        };

// Poi creiamo l'array di ComponentTile
//        this.assemblingTiles = new ComponentTile[]{
//                new Cannon(cannonConnectors, 0),     // Cannon1
//                new Cannon(cannonConnectors, 1),     // Cannon2
//                new CargoHolds(cargoConnectors, 2, false,3),  // Cabin1 (non speciale)
//                new CargoHolds(cargoConnectors, 3, false,4),  // Cabin2 (non speciale)
//                new CargoHolds(cargoConnectors, 4, true,4)    // Engine1 (speciale)
//        };

        try {
            List<ComponentTile> assemblingTilesList = ComponentTileFactory.loadTiles(this);
            assert assemblingTilesList != null;
            //Collections.shuffle(assemblingTilesList);
            this.assemblingTiles = assemblingTilesList.toArray(ComponentTile[]::new);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Setter che usi solo nei test
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Player>  getPlayers() {
        return players;
    }

    public ComponentTile[] getAssemblingTiles(){
        return assemblingTiles;
    }

    public Integer[] getTilesId(){
        return tilesToId(assemblingTiles);
    }

    public Dice[] getDice() {
        return dices;
    }

    public Timer getTimer() {
        return timer;
    }

    public Flightplance getFlightplance() {
        return plance;
    }



    /**
     * Rolls two dice and returns the sum of their values. Each die is rolled
     * using its respective {@code thr()} method, which generates a random number
     * between 1 and 6 (inclusive).
     *
     * @return the total sum of the values obtained by rolling the two dice
     */
    public int throwDices() {
        return dices[0].thr() + dices[1].thr();
    }


    /**
     * Checks if there are any planets in the provided list that are not currently marked as busy.
     *
     * @param card    the adventure card associated with this check; not used directly in the method.
     * @param planets the list of planets to evaluate for their busy status.
     * @return {@code true} if at least one planet in the list is not busy; {@code false} otherwise.
     */
    public boolean freePlanets(AdventureCard card, ArrayList<Planet> planets) {
        for (Planet planet : planets) {
            if (!planet.isBusy())
                return true;
        }
        return false;
    }

    /**
     * Orders the list of players based on their respective position values.
     * The method sorts the internal list of players using a comparator that
     * evaluates the position of each player, obtained through their associated
     * placeholders.
     *
     * The sorting is performed in ascending order of the players' positions.
     */
    public void orderPlayers(){
        players.sort(Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
    }

    /**
     * Picks a tile from the assembling tiles array and assigns it to the specified player's hand.
     * The method retrieves the tile corresponding to the given Tile ID.
     * If the tile is not available (null), the method returns null.
     *
     * @param player the player who is picking the tile; their hand will be updated with the specified tile
     * @param Tileid the identifier of the tile to be picked from the assembling tiles array
     * @return the picked tile as a {@code ComponentTile}, or {@code null} if the tile is not available
     */
    public ComponentTile pickTile(Player player, int Tileid){

        ComponentTile tile;
        synchronized(assemblingTiles){
            tile = assemblingTiles[Tileid];
        }

        if(assemblingTiles[Tileid] == null)
            return null;

        player.setHandTile(tile);
        assemblingTiles[Tileid] = null;
        return tile;
    }

    /**
     * Picks and removes a tile from the player's reserve spot and assigns it to the player's hand.
     * If the specified tile index is out of bounds or invalid, the method returns null.
     *
     * @param player    the player whose reserve spot is being accessed and whose hand will be updated
     * @param tileIndex the index of the tile to pick from the player's reserve spot
     * @return the picked {@code ComponentTile} if the index is valid, or {@code null} if the index is invalid or the tile does not exist
     */
    public ComponentTile pickTileReserveSpot(Player player, int tileIndex){

        List<ComponentTile> reserve = player.getSpaceshipPlance().getReserveSpot();
        ComponentTile tile = (tileIndex >= 0 && tileIndex < reserve.size()) ? reserve.get(tileIndex) : null;


        if(tile == null)
            return null;

        player.setHandTile(tile);
        player.getSpaceshipPlance().getReserveSpot().remove(tile);

        return tile;
    }

    /**
     * Converts an array of {@code ComponentTile} objects to an array of their corresponding IDs.
     * If a tile in the input array is {@code null}, the resulting array will have {@code null} at the same index.
     *
     * @param tiles an array of {@code ComponentTile} objects from which the IDs are to be extracted;
     *              the array can contain {@code null} elements.
     * @return an array of {@code Integer} IDs corresponding to the input tiles; the positions of
     *         {@code null} tiles in the input array will be mirrored in the output array.
     */
    public Integer[] tilesToId(ComponentTile[] tiles){
        Integer[] ids = new Integer[tiles.length];
        for(int i = 0; i < tiles.length; i++){
            ComponentTile tile =tiles[i];
            if(tile != null)
                ids[i] = tiles[i].getId();
            else
                ids[i] = null;
        }
        return ids;
    }

    /**
     * Resets the response status of all players in the game.
     * This method iterates through the list of players and sets
     * their responded status to false by calling the {@code setResponded(false)}
     * method on each player.
     */
    public void resetResponded() {
        for(Player p: players){
            p.setResponded(false);
        }
    }

    /**
     * Swaps two goods between respective cargo slots and indices in the player's spaceship.
     * Uses the player's spaceshipPlance to handle the swap operation.
     *
     * @param player the player whose goods are to be swapped; their spaceshipPlance will perform the operation
     * @param cargoIndex1 the index of the first cargo area involved in the swap
     * @param cargoIndex2 the index of the second cargo area involved in the swap
     * @param goodIndex1 the index of the first good in the first cargo area
     * @param goodIndex2 the index of the second good in the second cargo area
     */
    public void swapGoods(Player player, int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) {
        player.getSpaceshipPlance().handleSwap(cargoIndex1,cargoIndex2,goodIndex1, goodIndex2);
    }


    /**
     * Adds a good to a player's spaceship cargo, using the specified cargo index, good index, and reward index.
     * The operation is handled by the player's spaceshipPlance, which processes the addition
     * based on the player's current reward, cargo index, good index, and reward index.
     *
     * @param player      the player to whom the good will be added; their spaceship will be updated
     * @param cargoIndex  the index of the cargo area where the good will be added
     * @param goodIndex   the index of the good to be added
     * @param rewardIndex the index of the reward to be applied when adding the good
     */
    public void addGood(Player player, int cargoIndex, int goodIndex, int rewardIndex) {
        player.getSpaceshipPlance().handleAdd(player.getReward(),cargoIndex,goodIndex,rewardIndex);
    }

    /**
     * Removes a good from the specified cargo area and index in the player's spaceship.
     * This operation is handled by the player's spaceshipPlance.
     *
     * @param player the player whose spaceship is being modified
     * @param cargoIndex the index of the cargo area from which the good is to be removed
     * @param goodIndex the index of the good within the specified cargo area to be removed
     */
    public void removeGood(Player player, int cargoIndex, int goodIndex) {
        player.getSpaceshipPlance().handleRemove(cargoIndex,goodIndex);
    }



    /**
     * Ends the current player's turn and performs various reset and reordering operations
     * in preparation for the next turn.
     *
     * This method performs the following:
     * 1. Resets the response status of all players using {@code resetResponded()}.
     * 2. Resets the reward status of all players using {@code resetRewards()}.
     * 3. Resets the charged state of all double cannons in players' ships using {@code resetDoubleCannons()}.
     * 4. Resets the charged state of all double engines in players' ships using {@code resetDoubleEngines()}.
     * 5. Reorders the list of players based on their positions using {@code orderPlayers()}.
     */
    public void endTurn(){
        resetResponded();
        resetRewards();
        resetDoubleCannons();
        resetDoubleEngines();
        orderPlayers();
    }

    /**
     * Resets the charged status of all double cannons for every player in the game.
     *
     * This method iterates through each player's spaceship and retrieves their list
     * of cannons. If a cannon is identified as a DoubleCannon, it sets its charged
     * status to false using the {@code setCharged(false)} method.
     * This ensures that all double cannons are uncharged and ready to be re-used in
     * subsequent turns or actions.
     */
    public void resetDoubleCannons() {
        for(Player p: players){
            ArrayList<Cannon> cannons = p.getSpaceshipPlance().getCannons();
            for(Cannon c: cannons){
                if(c instanceof DoubleCannon)
                    ((DoubleCannon) c).setCharged(false);
            }
        }
    }

    /**
     * Resets the charged state of all double engines in the game.
     *
     * This method iterates through the list of players and accesses their spaceship's engines.
     * If an engine is an instance of {@code DoubleEngine}, it sets its charged state to {@code false}.
     */
    public void resetDoubleEngines() {
        for(Player p: players){
            ArrayList<Engine> engines = p.getSpaceshipPlance().getEngines();
            for(Engine e: engines){
                if(e instanceof DoubleEngine)
                    ((DoubleEngine) e).setCharged(false);
            }
        }
    }

    /**
     * Resets the rewards of all players in the game.
     *
     * This method iterates through the list of players and sets their reward to {@code null}.
     * It is typically used to clear any existing rewards during game resets or transitions between turns.
     */
    public void resetRewards() {
        for(Player p: players){
            p.setReward(null);
        }
    }

    /**
     * Calculates and returns the final statistics of the game's players.
     *
     * This method performs the following operations:
     * 1. Rewards players based on their respective positions using {@code rewardPlaces()}.
     * 2. Rewards players for their cargo using {@code rewardCargo()}.
     * 3. Penalizes players for lost tiles using {@code penalizeLostTiles()}.
     * 4. Rewards players for their spaceship using {@code rewardSpaceship()}.
     *
     * After calculating the rewards and penalties, players are sorted in descending order
     * of their credits. The method builds a result string listing each player's position,
     * nickname, and credits in the order of their ranking.
     *
     * @return a string containing the final rankings of all players, where each line
     *         represents a player's rank, formatted as "Rank. Nickname - Credits".
     */
    public String getEndStats(){
        rewardPlaces();
        rewardCargo();
        penalizeLostTiles();
        rewardSpaceship();

        ArrayList<Player> sortedList = new ArrayList<>(players);
        sortedList.sort(Comparator.comparing(Player::getCredits).reversed());

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sortedList.size(); i++) {
            Player p = sortedList.get(i);
            result.append((i + 1)).append(". ").append(p.getNickname()).append(" - ").append(p.getCredits()).append("\n");
        }

        return result.toString();
    }

    /**
     * Rewards the players who have the fewest exposed connectors on their spaceship.
     *
     * The method determines the minimum number of exposed connectors among all players' spaceships.
     * Subsequently, it identifies the players whose spaceships have this minimum number of exposed connectors.
     * Each of these players is rewarded with an additional 2 credits.
     *
     * The rewards are directly added to the players' current credit balances.
     */
    public void rewardSpaceship() {
        ArrayList<Player> sortedList = new ArrayList<>(players);
        int minExposed = sortedList.stream().mapToInt(p -> p.getSpaceshipPlance().countExposedConnectors()).min().getAsInt();
        List<Player> winners = sortedList.stream().filter(p -> p.getSpaceshipPlance().countExposedConnectors() == minExposed).toList();

        for(Player p: winners){
            p.setCredits(p.getCredits() + 2);
        }

    }

    /**
     * Applies a penalty to players based on the number of tiles in their spaceship's reserve spot.
     *
     * This method iterates over all players in the game and calculates a penalty for each player
     * by determining the size of their spaceship's reserve spot. The calculated penalty is then
     * subtracted from the player's current credits using the {@code setCredits()} method.
     *
     * The penalty for each player is determined as follows:
     * 1. Retrieve the size of the reserve spot in the player's spaceship.
     * 2. Subtract the size value (penalty) from the player's current credits.
     */
    public void penalizeLostTiles() {
        for (Player p: players) {
            int penalty = p.getSpaceshipPlance().getReserveSpot().size();
            p.setCredits(p.getCredits() - penalty);
        }
    }

    /**
     * Distributes rewards to players based on the goods stored in their spaceship's cargo holds.
     *
     * This method iterates through each player's spaceship cargo and calculates the value of goods stored.
     * For each non-null {@code GoodsBlock} in a cargo hold, the value of the goods is added to the player's credits.
     * If a player has surrendered, their total earned reward is halved at the end of the calculation.
     *
     * The reward calculation operates in the following steps:
     * 1. Iterate through all players in the game.
     * 2. For each player, access their spaceship's cargo holds.
     * 3. For each cargo hold, check the goods stored at each slot.
     * 4. Add the value of each non-null goods block to the player's credits.
     * 5. Adjust the credits for surrendered players by halving their total earned rewards.
     */
    public void rewardCargo() {
        for (Player p: players) {
            for (CargoHolds c: p.getSpaceshipPlance().getCargoHolds()) {
                for (int i=0; i < c.getCapacity(); i++) {
                    GoodsBlock goodsBlock = c.getGoods()[i];
                    if (goodsBlock == null) continue;
                    p.setCredits(p.getCredits() + goodsBlock.getValue());
                }
            }
            // surrended players get half of the cargo reward
            if(p.isSurrended())
                p.setCredits(p.getCredits() / 2);
        }
    }


    /**
     * Rewards players with credits based on their position in the game.
     * The method assumes players are ordered prior to distribution of rewards.
     * Players who have surrendered do not receive positional rewards.
     * Credits are distributed starting from the last player in the order with an initial
     * reward amount, decreasing progressively for succeeding players.
     */
    public void rewardPlaces() {
        orderPlayers();
        int amount = 4;
        for (int i = players.size()-1; i >= 0 ; i--) {
            // surrended players dont get position reward
            Player p = players.get(i);
            if(!p.isSurrended()){
                p.setCredits(players.get(i).getCredits() + amount);
            }
            amount--;
        }
    }
}