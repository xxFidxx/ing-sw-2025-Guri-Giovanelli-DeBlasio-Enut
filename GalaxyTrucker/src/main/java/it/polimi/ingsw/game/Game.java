package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.adventureCards.AdventureCard;
import it.polimi.ingsw.adventureCards.Planet;
import it.polimi.ingsw.adventureCards.PlanetsCard;
import it.polimi.ingsw.componentTiles.CargoHolds;

import java.util.*;

import static it.polimi.ingsw.game.ColorType.RED;

public class Game {
    private ArrayList<Player> players;
    private Timer timer;
    private Dice[] dices;
    private Flightplance plance;

    public Game(ArrayList<Player> players, Timer timer, Dice[] dices, Flightplance plance) {
        this.players = players;
        this.timer = timer;
        this.dices = dices;
        this.plance = plance;
    }

    public void Startgame() {
    }

    public ArrayList<Player>  getPlayers() {
        return players;
    }

    public Dice[] getDice() {
        return dices;
    }

    public Timer getTimer() {
        return timer;
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
        Dice dice1 = new Dice();
        Dice dice2 = new Dice();
        return dice1.thr() + dice2.thr();
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


    public Player choosePlayerPlanet(AdventureCard card,ArrayList<Planet> planets, int skip ) {
        ArrayList<Player> tmp = players;
        Collections.sort(tmp, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        for (int i = tmp.size() - 1 - skip; i >= 0; i--) {
            for (Planet planet : planets) {
                if (!planet.isBusy())
                    if (tmp.get(i).getResponse())
                        return tmp.get(i);
            }

        }
        return null;
    }
}

