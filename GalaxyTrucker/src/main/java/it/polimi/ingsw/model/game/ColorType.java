package it.polimi.ingsw.model.game;

public enum ColorType {
    BLUE(1),GREEN(2),YELLOW(3),RED(4);

    private final int value;

    ColorType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
