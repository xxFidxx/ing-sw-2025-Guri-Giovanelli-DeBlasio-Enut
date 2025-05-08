package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.resources.Projectile;

import java.util.Arrays;

public class PiratesCard extends EnemyCard {
    private Projectile[] shots; //liste o array?
    private int reward;

    public PiratesCard(String name, int level, Deck deck, int cannonStrength, int lostDays, Projectile[] shots, int reward) {
        super(name, level, deck, cannonStrength, lostDays);
        this.shots = shots;
        this.reward = reward;
    }

    public PiratesCard(String name, int level, int cannonStrength, int lostDays, Projectile[] shots, int reward) {
        super(name, level, cannonStrength, lostDays);
        this.shots = shots;
        this.reward = reward;
    }

    @Override
    public void reward(Player player) {
        player.setCredits(player.getCredits() + reward);
    }

    @Override
    public void penalize(Player player) {
        Game game = deck.getFlightPlance().getGame();
        int position = game.throwDices();
        for (Projectile shot : shots) {
            shot.activate(player, position);
        }
    }

    @Override
    public String toString() {
        return "PiratesCard{" +
                "shots=" + Arrays.toString(shots) +
                ", reward=" + reward +
                '}';
    }
}
