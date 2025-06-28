package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.bank.GoodsBlock;

import java.util.Arrays;

/**
 * The {@code CargoHolds} class represents a specific type of {@link ComponentTile} designed
 * to store goods during gameplay. It is characterized by its capacity to hold goods blocks
 * and whether it is marked as special.
 */
public class CargoHolds extends ComponentTile {
    private GoodsBlock[] goods;
    private boolean isSpecial;

    /**
     * Constructs a {@code CargoHolds} object with the specified attributes.
     *
     * @param connectors An array of {@link ConnectorType} defining the connectors of the component tile.
     * @param id         The unique identifier of the {@code CargoHolds}.
     * @param isSpecial  A boolean indicating whether this cargo hold is special.
     * @param capacity   The maximum capacity of goods this cargo hold can contain.
     */
    public CargoHolds(ConnectorType[] connectors, int id, boolean isSpecial, int capacity) {
        super(connectors, id);
        this.isSpecial = isSpecial;
        goods = new GoodsBlock[capacity];

        for (int i = 0; i < capacity; i++)
            goods[i] = null;
    }

    /**
     * Returns the array of {@link GoodsBlock} currently stored in the cargo hold.
     *
     * @return An array of {@link GoodsBlock} instances representing the goods in the cargo hold.
     */
    public GoodsBlock[] getGoods() {
        return goods;
    }

    /**
     * Returns the maximum capacity of the cargo hold.
     *
     * @return An integer representing the capacity of the cargo hold.
     */
    public int getCapacity() {
        return goods.length;
    }

    /**
     * Checks whether the cargo hold is marked as special.
     *
     * @return {@code true} if the cargo hold is special, {@code false} otherwise.
     */
    public boolean isSpecial() {
        return isSpecial;
    }

    /**
     * Returns a string representation of the {@code CargoHolds} object.
     * Includes information about its parent class, and its {@code isSpecial} flag and contents.
     *
     * @return A string describing this {@code CargoHolds}.
     */
    @Override
    public String toString() {
        return super.toString() + "isSpecial=" + isSpecial + "goods=" + Arrays.toString(goods);
    }
}