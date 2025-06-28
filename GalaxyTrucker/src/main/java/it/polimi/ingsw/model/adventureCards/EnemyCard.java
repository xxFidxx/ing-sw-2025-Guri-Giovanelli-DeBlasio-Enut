package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

/**
 * Represents an abstract enemy card in the adventure game.
 * EnemyCard serves as the base class for specific types of enemy cards
 * that players may encounter during gameplay. It extends the functionality
 * of the AdventureCard class and provides additional properties and behaviors
 * specific to enemy interactions.
 *
 * Each EnemyCard contains information about its cannon strength, the number of
 * days lost if it is activated, and the player who activates the card.
 * The class also defines methods to determine the outcome of a fight
 * between the player and the enemy, as well as abstract behaviors for
 * rewarding or penalizing players based on the fight outcome.
 */
public abstract class EnemyCard extends AdventureCard {
    private int cannonStrength;
    private int lostDays;
    private Player activatedPlayer;

    public EnemyCard(String name, int level, int cannonStrength, int lostDays) {
        super(name, level);
        this.cannonStrength = cannonStrength;
        this.lostDays = lostDays;
    }

    public int getCannonStrength() {
        return cannonStrength;
    }

    public int getLostDays() {
        return lostDays;
    }

    public void setActivatedPlayer(Player activatedPlayer) {
        this.activatedPlayer = activatedPlayer;
    }

    public int getFightOutcome(Player player) {
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
        int out = getFightOutcome(activatedPlayer);

        switch (out) {
            case 1:
                reward(activatedPlayer);
                deck.getFlightPlance().move(-lostDays,activatedPlayer);
                return;
            case -1:
                penalize(activatedPlayer);
                break;
            case 0:
                //continue;
        }
    }

    public abstract void reward(Player player);
    public abstract void penalize(Player player);

    @Override
    public String toString() {
        return "EnemyCard{" +
                "cannonStrength=" + cannonStrength +
                ", lostDays=" + lostDays +
                '}';
    }
}
