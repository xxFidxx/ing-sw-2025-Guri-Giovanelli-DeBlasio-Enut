package it.polimi.ingsw.adventureCards;

public class Planet {
    private List<GoodsBlock> goods;
    boolean isBusy;

    public Planet(List<GoodsBlock> goods, boolean isBusy) {
        this.goods = goods;
        this.isBusy = isBusy;
    }

    public List<GoodsBlock> getReward(){
        return goods;
    }

    public boolean isBusy(){
        return isBusy;
    }
}
