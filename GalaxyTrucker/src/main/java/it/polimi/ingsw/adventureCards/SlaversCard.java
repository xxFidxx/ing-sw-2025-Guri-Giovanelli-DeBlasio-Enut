package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

public class SlaversCard extends EnemyCard  {
    private int lostCrew;
    private int reward;

    public SlaversCard(String name, int level, Deck deck, int cannonStrength, int lostDays, int lostCrew, int reward) {
        super(name, level, deck, cannonStrength, lostDays);
        this.lostCrew = lostCrew;
        this.reward = reward;
    }

    @Override
    public void reward(Player player) {
        player.setCredits(player.getCredits() + reward);
    }

    @Override
    public void penalize(Player player) {
        player.loseCrew(lostCrew);
    }
}
