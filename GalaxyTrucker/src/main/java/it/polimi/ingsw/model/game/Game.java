package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.resources.Planet;

import java.util.*;

public class Game {
    private ArrayList<Player> players;
    private Timer timer;
    private Dice[] dices;
    private Flightplance plance;
    private final ComponentTile[] assemblingTiles;

    public Game(ArrayList<String> playersName) {
        this.players = new ArrayList<>();
        for (int i = 0; i < playersName.size(); i++) {
            this.players.add(new Player(playersName.get(i), this, i));
        }
        this.timer = new Timer();
        this.dices = new Dice[2];
        dices[0] = new Dice();
        dices[1] = new Dice();
        // gli spots dipenderanno dalla lobby size
        this.plance = new Flightplance(playersName.size(), this);

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
        this.assemblingTiles = new ComponentTile[]{
                new Cannon(cannonConnectors, 0),     // Cannon1
                new Cannon(cannonConnectors, 1),     // Cannon2
                new CargoHolds(cargoConnectors, 2, false),  // Cabin1 (non speciale)
                new CargoHolds(cargoConnectors, 3, false),  // Cabin2 (non speciale)
                new CargoHolds(cargoConnectors, 4, true)    // Engine1 (speciale)
        };
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

    public Integer[] getAssemblingTilesId(){
        return tilesToId(assemblingTiles);
    }

    public Dice[] getDice() {
        return dices;
    }

    public Timer getTimer() {
        return timer;
    }

    public Flightplance getFlightPlance() {
        return plance;
    }

    public Player choosePlayer(AdventureCard card) {
        // orderPlayers();
        for (int i = players.size() - 1; i >= 0; i--) {
            if (card.checkCondition(players.get(i)))
                if (players.get(i).getResponse())
                    return players.get(i);
        }
        return null;
    }

    public int throwDices() {
        return dices[0].thr() + dices[1].thr();
    }


    public Player choosePlayerPlanet(AdventureCard card,ArrayList<Planet> planets, Stack<Player> players ) {
        Collections.sort(players, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));

        while (!players.isEmpty()) {
            Player topPlayer = players.pop();
            for (Planet planet : planets) {
                if (!planet.isBusy())
                    if (topPlayer.getResponse())
                        return topPlayer;
            }

        }

        return null;
    }

    public void orderPlayers(){
        Collections.sort(players, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
    }

    public String pickTile(Player player, int Tileid){

        ComponentTile tile;
        synchronized(assemblingTiles){
            tile = assemblingTiles[Tileid];
        }

        if(assemblingTiles[Tileid] == null )
            return null;

        player.setHandTile(tile);
        assemblingTiles[Tileid] = null;
        return tiletoString(tile);
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

    public String tiletoString(ComponentTile tile){
        if (tile != null) {
            switch (tile) {
                case DoubleCannon dc -> {
                    return "DoubleCannon";
                }

                case Cannon c-> {
                    return "Cannon";
                }

                case DoubleEngine de -> {
                    return "DoubleEngine";
                }
                case Engine e -> {
                    return "Engine";
                }
                case Cabin cab -> {
                    return "Cabin";
                }
                case CargoHolds ch -> {
                    return "CargoHolds";
                }

                case ShieldGenerator sg -> {
                    return "ShieldGenerator";
                }

                case LifeSupportSystem lfs -> {
                    return "LifeSupportSystem";
                }

                case PowerCenter pc -> {
                    return "PowerCenter";
                }

                case StructuralModule sm -> {
                    return "StructuralModule";
                }

                default -> {
                    return "notCatched in tiletoString";
                }
            }
        }
        return null;
    }


}

