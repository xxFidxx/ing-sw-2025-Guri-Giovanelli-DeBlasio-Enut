package it.polimi.ingsw.controller.network.data;

public class DoubleEngineNumber extends DataContainer {
    private final int num;
    private final int power;

    public DoubleEngineNumber(int power, int num) {
        this.power = power;
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public int getPower() {
        return power;
    }
}
