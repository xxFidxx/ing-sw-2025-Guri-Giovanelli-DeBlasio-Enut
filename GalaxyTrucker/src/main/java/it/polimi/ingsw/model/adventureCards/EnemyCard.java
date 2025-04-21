package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.Stack;

public abstract class EnemyCard extends AdventureCard {
    private int cannonStrength;
    private int lostDays;

    public EnemyCard(String name, int level, Deck deck, int cannonStrength, int lostDays) {
        super(name, level, deck);
        this.cannonStrength = cannonStrength;
        this.lostDays = lostDays;
    }

    public int getCannonStrength() {
        return cannonStrength;
    }

    public int getLostDays() {
        return lostDays;
    }

    protected int getFightOutcome(Player player) {
        // -1 vince il nemico
        // 0 pareggio
        // 1 vince il giocatore

        float fireStrenght = player.getFireStrenght();
        if ( fireStrenght > cannonStrength) return 1;
        else if (fireStrenght < cannonStrength) return -1;
        return 0;
    }

    @Override
    public void activate() {
        ArrayList<Player> players = deck.getFlightPlance().getGame().getPlayers();
        Stack<Player> playerStack = new Stack<>();

        for (Player player : players) {
            playerStack.push(player);
        }

        while (!playerStack.isEmpty()) {
            Player fightingPlayer = playerStack.pop();
            int out = getFightOutcome(fightingPlayer);

            switch (out) {
                case 1:
                    reward(fightingPlayer);
                    deck.getFlightPlance().move(-lostDays,fightingPlayer);
                    return;
                case -1:
                    penalize(fightingPlayer);
                    break;
                case 0:
                    continue;
            }
        }
    }

    public abstract void reward(Player player);
    public abstract void penalize(Player player);
}
