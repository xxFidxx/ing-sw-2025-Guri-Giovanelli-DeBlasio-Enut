package it.polimi.ingsw.model.bank;

import it.polimi.ingsw.model.game.ColorType;

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
