package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.PowerCenter;

import java.io.Serializable;
import java.util.ArrayList;

public class BatteriesManagement extends DataContainer implements Serializable {
    private int nBatteries;
    private ArrayList<PowerCenter> powerCenters;
    public BatteriesManagement(int nBatteries, ArrayList<PowerCenter> powerCenters) {
        this.nBatteries = nBatteries;
        this.powerCenters = powerCenters;
    }
    public int getNBatteries() {
        return nBatteries;
    }
    public ArrayList<PowerCenter> getPowerCenters() {
        return powerCenters;
    }
}
