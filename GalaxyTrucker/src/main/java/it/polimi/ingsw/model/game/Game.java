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
    private final ArrayList<String> assemblingTilesCovered;
    // mi serve un qualcosa che associ l'id stringa Tile1 con l'id vero e proprio della TIle, una hashmap
    private final Map<String,String> idbyCoveredId;
    private final ArrayList<String> Tiles;

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
        this.plance = new Flightplance(playersName.size(),this);
        this.assemblingTilesCovered = new ArrayList<>(List.of("Tile1", "Tile2", "Tile3", "Tile4", "Tile5"));
        this.Tiles = new ArrayList<>(List.of("Cannon1", "Cannon2", "Cabin1", "Cabin2", "Engine1"));
        this.idbyCoveredId = new HashMap<>();
        for (int i = 0; i < assemblingTilesCovered.size(); i++) {
            idbyCoveredId.put(assemblingTilesCovered.get(i), Tiles.get(i));
        }
    }

    public void Startgame() {
    }

    public ArrayList<Player>  getPlayers() {
        return players;
    }

    public ArrayList<String> getAssemblingTilesCovered() {
        return assemblingTilesCovered;
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
        ArrayList<Player> tmp = players;
        Collections.sort(tmp, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        for (int i = tmp.size() - 1; i >= 0; i--) {
            if (card.checkCondition(tmp.get(i)))
                if (tmp.get(i).getResponse())
                    return tmp.get(i);
        }
        return null;
    }

    public int throwDices() {
        return dices[0].thr() + dices[1].thr();
    }

//    public Player choosePlayer(AdventureCard card, int n) {
//        ArrayList<Player> tmp = players;
//        Collections.sort(tmp, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
//        for (int i = tmp.size() - 1; i >= 0; i--) {
//            if (card.checkCondition(tmp.get(i)))
//                if (tmp.get(i).getResponse())
//                    return tmp.get(i);
//        }
//        return null;
//    }


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

    public String pickTile(String CoveredId){

        synchronized(assemblingTilesCovered){
            if(!assemblingTilesCovered.contains(CoveredId))
                return null;
        }


        synchronized(assemblingTilesCovered){
            assemblingTilesCovered.remove(CoveredId);
        }

        return idbyCoveredId.get(CoveredId);
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

