package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.adventureCards.AdventureCard;
import it.polimi.ingsw.adventureCards.Planet;
import it.polimi.ingsw.adventureCards.PlanetsCard;
import it.polimi.ingsw.componentTiles.CargoHolds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static it.polimi.ingsw.game.ColorType.RED;

public class Game {
    private Player[] players;
    private Timer timer;
    private Dice[] dices;
    private Flightplance plance;

    public Game(Player[] player, Timer timer, Dice[] dices, Flightplance plance) {
        this.players = players;
        this.timer = timer;
        this.dices = dices;
        this.plance = plance;
    }

    public void Startgame() {
    }

    public Player[] getPlayers() {
        return players;
    }

    public Dice[] getDice() {
        return dices;
    }

    public Timer getTimer() {
        return timer;
    }

    public Player choosePlayer(AdventureCard card) {
        Player[] tmp = players;
        Arrays.sort(tmp, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        for (int i = tmp.length - 1; i >= 0; i--) {
            if (card.checkCondition(tmp[i]))
                if (tmp[i].getResponse())
                    return tmp[i];
        }
        return null;
    }

    public int throwDices() {
        Dice dice1 = new Dice();
        Dice dice2 = new Dice();
        return dice1.thr() + dice2.thr();
    }

    public Player choosePlayer(AdventureCard card, int n) {
        Player[] tmp = players;
        Arrays.sort(tmp, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        for (int i = tmp.length - 1; i >= 0; i--) {
            if (card.checkCondition(tmp[i]))
                if (tmp[i].getResponse())
                    return tmp[i];
        }
        return null;
    }


    public Player choosePlayerPlanet(AdventureCard card,ArrayList<Planet> planets, int skip ) {
        Player[] tmp = players;
        Arrays.sort(tmp, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        for (int i = tmp.length - 1 - skip; i >= 0; i--) {
            for (Planet planet : planets) {
                if (!planet.isBusy())
                    if (tmp[i].getResponse())
                        return tmp[i];
            }

        }
        return null;
    }
}

