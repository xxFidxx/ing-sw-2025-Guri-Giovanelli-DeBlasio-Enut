package it.polimi.ingsw.model.bank;

public class BatteryToken {
    private boolean isPlaced;

    public BatteryToken(boolean isPlaced) {
        this.isPlaced = isPlaced;
    }

    public boolean isPlaced() {
        return isPlaced;
    }
}
