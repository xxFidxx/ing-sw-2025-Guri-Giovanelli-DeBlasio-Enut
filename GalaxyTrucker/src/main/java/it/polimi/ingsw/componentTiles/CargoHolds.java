package it.polimi.ingsw.componentTiles;

import it.polimi.ingsw.bank.GoodsBlock;

public class CargoHolds extends ComponentTile{
    private GoodsBlock[] goods;
    private boolean isSpecial;

    public CargoHolds(ConnectorType[] connectors, Direction direction, GoodsBlock[] goods, boolean isSpecial) {
        super(connectors);
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
