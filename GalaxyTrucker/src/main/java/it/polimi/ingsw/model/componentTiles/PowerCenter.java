package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.bank.BatteryToken;

import java.util.Arrays;

public class PowerCenter extends ComponentTile {
    private final boolean[] batteries;

    public PowerCenter(ConnectorType[] connectors, int capacity, int id) {
        super(connectors,id);
        batteries = new boolean[capacity];
        Arrays.fill(batteries, true);
    }

    public boolean[] getBatteries() {
        return batteries;
    }

    @Override
    public String toString() {
        return super.toString() + "id: " + getId() + "batteries= " + Arrays.toString(batteries);
    }
}
