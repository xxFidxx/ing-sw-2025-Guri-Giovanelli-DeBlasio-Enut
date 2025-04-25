package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.bank.GoodsBlock;

public class CargoHolds extends ComponentTile{
    private GoodsBlock[] goods;
    private boolean isSpecial;

    public CargoHolds(ConnectorType[] connectors, boolean isSpecial) {
        super(connectors);
        this.isSpecial = isSpecial;
    }

    public GoodsBlock[] getGoods() {
        return goods;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

}
