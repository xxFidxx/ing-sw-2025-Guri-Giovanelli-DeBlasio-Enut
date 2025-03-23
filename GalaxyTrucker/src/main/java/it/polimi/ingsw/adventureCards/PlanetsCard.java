package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

import java.util.List;

public class PlanetsCard extends AdventureCard {
    private List<Planet> planets;
    private int lostDays;

    public PlanetsCard(String name, int level, List<Planet> planets, int lostDays, Deck deck) {
        super(name, level,deck);
        this.lostDays = lostDays;
        this.planets = planets;
    }

    @Override
    public void activate() {

            Player p = deck.getFlightplance().getGame().choosePlayer(this);
            //  ci serve qualcosa per dirgli di saltare il player che è atterrato in questo pianeta, meglio il metodo choosePlayerPlanet o una cosa del genere

            Planet chosenPlanet = p.choosePlanet();

            if (p == null) {
                System.out.println("No player selected");
                return;
            }

            p.cargoManagement(chosenPlanet.getReward());


    }







    public int getLostDays() {
        return lostDays;
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public boolean checkCondition(){
        return true;
    }
}
