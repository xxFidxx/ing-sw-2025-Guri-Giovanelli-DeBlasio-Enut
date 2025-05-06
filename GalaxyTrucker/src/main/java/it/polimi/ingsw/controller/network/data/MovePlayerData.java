package it.polimi.ingsw.controller.network.data;

public class MovePlayerData extends DataContainer {
    private final int steps;

    public MovePlayerData(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }
}
