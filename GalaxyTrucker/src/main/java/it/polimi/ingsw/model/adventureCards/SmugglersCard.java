package it.polimi.ingsw.model.adventureCards;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.Arrays;

public class SmugglersCard extends EnemyCard {
    private int lossMalus;
    private GoodsBlock[] reward; // da aggiornare con goods block

    public SmugglersCard(String name, int level, int cannonStrength, int lostDays, int lossMalus, GoodsBlock[] reward) {
        super(name, level, cannonStrength, lostDays);
        this.lossMalus = lossMalus;
        this.reward = reward;
    }

    @Override
    public void reward(Player player) {
        player.setReward(reward);
    }

    @Override
    public void penalize(Player player) {
    }

    public int getLossMalus() {
        return lossMalus;
    }

    @Override
    public String toString() {
        return super.toString() + "SmugglersCard{" +
                "lossMalus=" + lossMalus +
                ", reward=" + Arrays.toString(reward) +
                '}';
    }
}
