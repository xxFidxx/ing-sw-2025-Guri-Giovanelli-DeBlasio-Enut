package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

public class SlaversCard extends EnemyCard  {
    private int lostCrew;
    private int reward;

    public SlaversCard(String name, int level, Deck deck, int cannonStrength, int lostDays, int lostCrew, int reward) {
        super(name, level, deck, cannonStrength, lostDays);
        this.lostCrew = lostCrew;
        this.reward = reward;
    }

    public SlaversCard(String name, int level, int cannonStrength, int lostDays, int lostCrew, int reward) {
        super(name, level, cannonStrength, lostDays);
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
