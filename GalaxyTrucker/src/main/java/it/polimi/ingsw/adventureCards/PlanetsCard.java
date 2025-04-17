package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;
import it.polimi.ingsw.resources.Planet;

import java.util.*;

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

        Game game = deck.getFlightPlance().getGame();
        ArrayList<Player> tmp = game.getPlayers();

        Stack<Player> playerStack = new Stack<>();

        for (Player player : tmp) {
            playerStack.push(player);
        }


        while (!playerStack.isEmpty()) {
            Player p = deck.getFlightPlance().getGame().choosePlayerPlanet(this, planets, playerStack);

            //questo adesso capita se nessuno vuole attivare o se non ci sono più pianeti in cui atterrare
            if (p == null) {
                System.out.println("No player selected");
                return;
            }

            Planet chosenPlanet = p.choosePlanet(planets);

            if (chosenPlanet == null) {
                System.out.println("No planet chosen");
            }else{
                p.getSpaceshipPlance().cargoManagement(chosenPlanet.getReward());
                deck.getFlightPlance().move(- lostDays,p);
            }


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
