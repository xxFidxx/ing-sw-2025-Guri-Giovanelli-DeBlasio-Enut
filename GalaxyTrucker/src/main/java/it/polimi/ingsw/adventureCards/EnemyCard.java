package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

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
}
