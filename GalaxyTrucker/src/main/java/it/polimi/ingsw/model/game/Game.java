package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.controller.network.EventListenerInterface;
import it.polimi.ingsw.model.resources.Planet;

import java.util.*;

public class Game {
    private ArrayList<Player> players;
    private Timer timer;
    private Dice[] dices;
    private Flightplance plance;
    private List<EventListener> listeners = new ArrayList<>();
    private ArrayList<String> assemblingTiles;

    public Game(ArrayList<String> playersName) {
        this.players = new ArrayList<>();
        for (int i = 0; i < playersName.size(); i++) {
            this.players.add(new Player(playersName.get(i), this, i));
        }
        this.timer = new Timer();
        this.dices = new Dice[2];
        dices[0] = new Dice();
        dices[1] = new Dice();
        // gli spots dipenderanno da cosa ha deciso il player che ha creato la lobby
        this.plance = new Flightplance(playersName.size(),this);
        this.assemblingTiles = new ArrayList<>(List.of("Tile1", "Tile2", "Tile3", "Tile4", "Tile5"));
    }

    public void Startgame() {
    }

    public ArrayList<Player>  getPlayers() {
        return players;
    }

    public ArrayList<String> getAssemblingTiles() {
        return assemblingTiles;
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


    public void addEventListener(EventListenerInterface listener){
        listeners.add(listener);
    }

    public void removeEventListener(EventListenerInterface listener){
        listeners.remove(listener);
    }
}

