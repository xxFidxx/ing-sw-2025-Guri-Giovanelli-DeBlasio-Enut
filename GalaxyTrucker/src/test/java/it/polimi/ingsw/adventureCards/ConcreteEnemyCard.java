package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.EnemyCard;
import it.polimi.ingsw.model.game.Player;

public class ConcreteEnemyCard extends EnemyCard {
    public boolean rewardCalled = false;
    public boolean penalizeCalled = false;

    public ConcreteEnemyCard(String name, int level, int cannonStrength, int lostDays) {
        super(name, level, cannonStrength, lostDays);
    }

    @Override
    public void reward(Player player) {
        rewardCalled = true;
    }

    @Override
    public void penalize(Player player) {
        penalizeCalled = true;
    }
}
