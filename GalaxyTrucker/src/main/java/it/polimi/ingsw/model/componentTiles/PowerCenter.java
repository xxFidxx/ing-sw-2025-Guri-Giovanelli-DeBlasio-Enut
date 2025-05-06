package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.bank.BatteryToken;

import java.util.Arrays;

public class PowerCenter extends ComponentTile {
    private BatteryToken[] batteries;

    public PowerCenter(ConnectorType[] connectors, int capacity, int id) {
        super(connectors,id);
    }

    public BatteryToken[] getBatteries() {
        return batteries;
    }

    public void setBatteries(BatteryToken[] batteries){
        this.batteries = batteries;
    }

    @Override
    public String toString() {
        return super.toString() + "batteries=" + Arrays.toString(batteries);
    }
}
