package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

public abstract class EnemyCard extends AdventureCard {
    private int cannonStrength;
    private int lostDays;
    private Player activatedPlayer;

    public EnemyCard(String name, int level, Deck deck, int cannonStrength, int lostDays) {
        super(name, level, deck);
        this.cannonStrength = cannonStrength;
        this.lostDays = lostDays;
    }

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
