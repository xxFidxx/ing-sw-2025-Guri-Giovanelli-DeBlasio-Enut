package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

public class PiratesCard extends EnemyCard {
    private CannonFire[] shots; //liste o array?
    private int reward;

    public PiratesCard(String name, int level, Deck deck, int cannonStrength, int lostDays, CannonFire[] shots, int reward) {
        super(name, level, deck, cannonStrength, lostDays);
        this.shots = shots;
        this.reward = reward;
    }

    @Override
    public void reward(Player player) {
        player.setCredits(player.getCredits() + reward);
    }

    @Override
    public void penalize(Player player) {

    }
}
