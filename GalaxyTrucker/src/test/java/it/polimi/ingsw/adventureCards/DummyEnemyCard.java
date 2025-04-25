package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.model.adventureCards.EnemyCard;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

public class DummyEnemyCard extends EnemyCard {
    public boolean rewardCalled = false;
    public boolean penalizeCalled = false;

    public DummyEnemyCard(String name, int level, Deck deck, int cannonStrength, int lostDays) {
        super(name, level, deck, cannonStrength, lostDays);
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
