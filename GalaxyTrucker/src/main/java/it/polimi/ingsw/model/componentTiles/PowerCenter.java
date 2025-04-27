package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.bank.BatteryToken;

public class PowerCenter extends ComponentTile {
    private BatteryToken[] batteries;


    public PowerCenter(ConnectorType[] connectors, int id, BatteryToken[] batteries) {
        super(connectors,id);
        this.batteries = batteries;
    }

    public BatteryToken[] getBatteries() {
        return batteries;
    }

    public void setBatteries(BatteryToken[] batteries){
        this.batteries = batteries;
    }
}
