package it.polimi.ingsw.Bank;

import it.polimi.ingsw.ColorType;

public class GoodsBlock {
    private int value;
    private ColorType type;

    public GoodsBlock(int value, ColorType type) {
        this.value = value;
        this.type = type;
    }
    public int getValue() {
        return value;
    }

    public ColorType getType() {
        return type;
    }
}
