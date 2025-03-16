package it.polimi.ingsw.adventureCards;

import java.util.List;

public class PlanetsCard extends AdventureCard implements Rewardable, Penalizable{
    private List<Planet> planets;
    private int lostDays;

    public PlanetsCard(String name, int level, List<Planet> planets, int lostDays) {
        super(name, level);
        this.lostDays = lostDays;
        this.planets = planets;
    }

    @Override
    public void activate() {

    }

    public void reward(){

    }

    public void penalize(){

    }

    public int getLostDays() {
        return lostDays;
    }

    public List<Planet> getPlanets() {
        return planets;
    }
}
