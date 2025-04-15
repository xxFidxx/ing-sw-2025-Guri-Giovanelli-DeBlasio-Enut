package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

import java.util.ArrayList;
import java.util.Stack;

public class StardustCard extends AdventureCard  {

    public StardustCard(String name, int level, int lostDays, Deck deck) {
        super(name, level,deck);
    }

    @Override
    public void activate() {
        ArrayList<Player> tmp = deck.getFlightPlance().getGame().getPlayers();

        Stack<Player> playerStack = new Stack<>();

        for (Player player : tmp) {
            playerStack.push(player);
        }

        while (!playerStack.isEmpty()) {
            Player chosenPlayer = playerStack.pop();
            int lost = chosenPlayer.getSpaceshipPlance().getExposedConnectors();

            deck.getFlightPlance().move(- lost,chosenPlayer);
        }
    }

}
