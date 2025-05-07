package it.polimi.ingsw.model.bank;

import it.polimi.ingsw.model.game.ColorType;

public class GoodsBlock {
    private int value;
    private ColorType type;

    public GoodsBlock(ColorType type) {
        this.value = type.ordinal()+1; // blue (= 0) + 1 = 1 (valore giusto)
        this.type = type;
    }
    public int getValue() {
        return value;
    }

    public ColorType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "GoodsBlock{" +
                "type=" + type +
                '}';
    }
}
