package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.bank.GoodsBlock;

import java.util.Arrays;

public class CargoHolds extends ComponentTile{
    private GoodsBlock[] goods;
    private boolean isSpecial;

    public CargoHolds(ConnectorType[] connectors, int id, boolean isSpecial, int capacity) {
        super(connectors,id);
        this.isSpecial = isSpecial;
        goods = new GoodsBlock[capacity];

        for(int i = 0; i < capacity; i++)
            goods[i] = null;
    }

    public GoodsBlock[] getGoods() {
        return goods;
    }

    public int getCapacity() {
        return goods.length;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    @Override
    public String toString() {
        return super.toString() + "isSpecial=" + isSpecial + "goods=" + Arrays.toString(goods);
    }
}
