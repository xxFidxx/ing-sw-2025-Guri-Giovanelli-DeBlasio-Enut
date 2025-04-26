package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.bank.GoodsBlock;

public class CargoHolds extends ComponentTile{
    private String id;
    private GoodsBlock[] goods;
    private boolean isSpecial;

    public CargoHolds(ConnectorType[] connectors, GoodsBlock[] goods,String id, boolean isSpecial) {
        super(connectors,id);
        this.goods = goods;
        this.isSpecial = isSpecial;
    }

    public GoodsBlock[] getGoods() {
        return goods;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

}
