package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

import java.util.ArrayList;
import java.util.List;

public class PlanetsCard extends AdventureCard {
    private ArrayList<Planet> planets;
    private int lostDays;

    public PlanetsCard(String name, int level, ArrayList<Planet> planets, int lostDays, Deck deck) {
        super(name, level, deck);
        this.lostDays = lostDays;
        this.planets = planets;
    }

    @Override
    public void activate() {
        Player[] players = deck.getFlightplance().getGame().getPlayers();
        for (int i = 0; i < players.length; i++) {
            // gli passo i per dirgli di saltare i players da checkare all'inizio
            Player p = deck.getFlightplance().getGame().choosePlayerPlanet(this, planets, i);

            //questo adesso capita se nessuno vuole attivare o se non ci sono più pianeti in cui atterrare
            if (p == null) {
                System.out.println("No player selected");
                return;
            }

            Planet chosenPlanet = p.choosePlanet(planets);

            if (chosenPlanet == null) {
                System.out.println("No planet chosen");
                return;
            }

            p.cargoManagement(chosenPlanet.getReward());
        }

    }


    public int getLostDays() {
        return lostDays;
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    // ritorna true se non tutti sono occupati
    public boolean checkCondition() {
        return !planets.stream().allMatch(Planet::isBusy);
    }

}
