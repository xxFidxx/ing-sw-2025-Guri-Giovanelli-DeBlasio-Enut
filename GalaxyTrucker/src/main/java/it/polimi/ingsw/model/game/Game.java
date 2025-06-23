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



    public int throwDices() {
        return dices[0].thr() + dices[1].thr();
    }


    public boolean freePlanets(AdventureCard card, ArrayList<Planet> planets) {
        for (Planet planet : planets) {
            if (!planet.isBusy())
                return true;
        }
        return false;
    }

    public void orderPlayers(){
        players.sort(Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
    }

    public ComponentTile pickTile(Player player, int Tileid){

        ComponentTile tile;
        synchronized(assemblingTiles){
            tile = assemblingTiles[Tileid];
        }

        if(assemblingTiles[Tileid] == null )
            return null;

        player.setHandTile(tile);
        assemblingTiles[Tileid] = null;
        return tile;
    }

    public ComponentTile pickTileReserveSpot(Player player, int tileIndex){

        List<ComponentTile> reserve = player.getSpaceshipPlance().getReserveSpot();
        ComponentTile tile = (tileIndex >= 0 && tileIndex < reserve.size()) ? reserve.get(tileIndex) : null;


        if(tile == null )
            return null;

        player.setHandTile(tile);
        player.getSpaceshipPlance().getReserveSpot().remove(tile);

        return tile;
    }

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

    public void resetResponded() {
        for(Player p: players){
            p.setResponded(false);
        }
    }

    public void swapGoods(Player player, int cargoIndex1, int cargoIndex2, int goodIndex1, int goodIndex2) {
        player.getSpaceshipPlance().handleSwap(cargoIndex1,cargoIndex2,goodIndex1, goodIndex2);
    }


    public void addGood(Player player, int cargoIndex, int goodIndex, int rewardIndex) {
        player.getSpaceshipPlance().handleAdd(player.getReward(),cargoIndex,goodIndex,rewardIndex);
    }

    public void removeGood(Player player, int cargoIndex, int goodIndex) {
        player.getSpaceshipPlance().handleRemove(cargoIndex,goodIndex);
    }



    public void endTurn(){
        resetResponded();
        resetRewards();
        resetDoubleCannons();
        resetDoubleEngines();
        orderPlayers();
    }

    public void resetDoubleCannons() {
        for(Player p: players){
            ArrayList<Cannon> cannons = p.getSpaceshipPlance().getCannons();
            for(Cannon c: cannons){
                if(c instanceof DoubleCannon)
                    ((DoubleCannon) c).setCharged(false);
            }
        }
    }

    public void resetDoubleEngines() {
        for(Player p: players){
            ArrayList<Engine> engines = p.getSpaceshipPlance().getEngines();
            for(Engine e: engines){
                if(e instanceof DoubleEngine)
                    ((DoubleEngine) e).setCharged(false);
            }
        }
    }

    public void resetRewards() {
        for(Player p: players){
            p.setReward(null);
        }
    }

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

    public void rewardSpaceship() {
        ArrayList<Player> sortedList = new ArrayList<>(players);
        int minExposed = sortedList.stream().mapToInt(p -> p.getSpaceshipPlance().countExposedConnectors()).min().getAsInt();
        List<Player> winners = sortedList.stream().filter(p -> p.getSpaceshipPlance().countExposedConnectors() == minExposed).toList();

        for(Player p: winners){
            p.setCredits(p.getCredits() + 2);
        }

    }

    public void penalizeLostTiles() {
        for (Player p: players) {
            int penalty = p.getSpaceshipPlance().getReserveSpot().size();
            p.setCredits(p.getCredits() - penalty);
        }
    }

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


    public void rewardPlaces() {
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

