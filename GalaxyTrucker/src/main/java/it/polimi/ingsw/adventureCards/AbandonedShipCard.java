package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.game.Deck;

import java.util.List;

public class AbandonedShipCard extends AdventureCard {
    private int lostDays;
    private int lostCrew;
    private List<GoodsBlock> goods;

    public AbandonedShipCard(String name, int level, int lostDays, int lostCrew, List<GoodsBlock> goods, Deck deck) {
        super(name, level,deck);
        this.lostDays = lostDays;
        this.lostCrew = lostCrew;
        this.goods = goods;
    }

    @Override
    public void activate(){

    }


    public int getLostDays() {
        return lostDays;
    }

    public int getLostCrew() {
        return lostCrew;
    }

    public List<GoodsBlock> getReward(){
        return goods;
    }
}
