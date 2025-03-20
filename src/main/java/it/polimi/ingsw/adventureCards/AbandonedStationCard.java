package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.Bank.GoodsBlock;

import java.util.List;

public class AbandonedStationCard extends AdventureCard{
    private int lostDays;
    private int lostCrew;
    private List<GoodsBlock> goods;

    public AbandonedStationCard(String name, int level, int lostDays, int lostCrew, List<GoodsBlock> goods) {
        super(name, level);
        this.lostDays = lostDays;
        this.lostCrew = lostCrew;
        this.goods = goods;
    }

    @Override
    public void activate(){

    }

    public void reward(){

    }

    public void penalize(){

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
