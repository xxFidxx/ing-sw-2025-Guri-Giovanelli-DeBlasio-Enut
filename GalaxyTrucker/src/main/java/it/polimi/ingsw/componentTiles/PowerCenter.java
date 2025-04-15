package it.polimi.ingsw.componentTiles;

import it.polimi.ingsw.bank.BatteryToken;

public class PowerCenter extends ComponentTile {
    private BatteryToken[] batteries;


    public PowerCenter(ConnectorType[] connectors, Direction direction, BatteryToken[] batteries) {
        super(connectors);
        this.batteries = batteries;
    }

    public BatteryToken[] getBatteries() {
        return batteries;
    }

    public void setBatteries(BatteryToken[] batteries){
        this.batteries = batteries;
    }
}
