package it.polimi.ingsw.game;

import java.util.Random;

public class Dice {
    private int number;
    private Random random;

    public Dice() {
        this.number = 6;
        this.random = new Random();
    }

    public int getNumber() {
        return number;
    }

    public int thr(){
        number = random.nextInt(6) + 1;
        return number;
    }
}
