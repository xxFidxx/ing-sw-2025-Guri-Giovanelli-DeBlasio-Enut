package it.polimi.ingsw.adventureCards;
import it.polimi.ingsw.bank.GoodsBlock;
import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

public class SmugglersCard extends EnemyCard {
    private int lossMalus;
    private GoodsBlock[] reward; // da aggiornare con goods block

    public SmugglersCard(String name, int level, Deck deck, int cannonStrength, int lostDays, int lossMalus, GoodsBlock[] reward) {
        super(name, level, deck, cannonStrength, lostDays);
        this.lossMalus = lossMalus;
        this.reward = reward;
    }

    @Override
    public void reward(Player player) {
        player.getSpaceshipPlance().cargoManagement(reward);
    }

    @Override
    public void penalize(Player player) {
        player.removeMostValuableCargo();
    }
}
