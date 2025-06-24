package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.resources.Planet;

import java.util.*;

public class PlanetsCard extends AdventureCard {
    private ArrayList<Planet> planets;
    private int lostDays;
    private Player activatedPlayer;
    private Planet chosenPlanet;

    public PlanetsCard(String name, int level, ArrayList<Planet> planets, int lostDays, Deck deck) {
        super(name, level, deck);
        this.lostDays = lostDays;
        this.planets = planets;
    }

    public PlanetsCard(String name, int level, ArrayList<Planet> planets, int lostDays) {
        super(name, level);
        this.lostDays = lostDays;
        this.planets = planets;
    }

    public void setActivatedPlayer(Player activatedPlayer) {
        this.activatedPlayer = activatedPlayer;
    }

    public void setChosenPlanet(Planet chosenPlanet) {
        this.chosenPlanet = chosenPlanet;
    }

    @Override
    public void activate() {

        activatedPlayer.setReward(chosenPlanet.getReward());
        deck.getFlightPlance().move(-lostDays, activatedPlayer);
    }


    public int getLostDays() {
        return lostDays;
    }

    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    // ritorna true se non tutti sono occupati
    public boolean checkCondition() {
        return !planets.stream().allMatch(Planet::isBusy);
    }



    @Override
    public String toString() {
        return "PlanetsCard{" +
                "planets=" + planets +
                ", lostDays=" + lostDays +
                '}';
    }
}
