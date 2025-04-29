package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.bank.GoodsBlock;

public class CargoHolds extends ComponentTile{
    private int id;
    private GoodsBlock[] goods;
    private boolean isSpecial;

    public CargoHolds(ConnectorType[] connectors,int id, boolean isSpecial, int capacity) {
        super(connectors,id);
        this.isSpecial = isSpecial;
    }

    public GoodsBlock[] getGoods() {
        return goods;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

}
