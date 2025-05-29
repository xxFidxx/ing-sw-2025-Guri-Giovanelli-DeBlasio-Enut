package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.Cabin;

import java.util.ArrayList;

public class CrewManagement extends DataContainer implements java.io.Serializable {
    private ArrayList<Cabin> cabins;
    private int lostAliens;
    private int lostCrew;

    public CrewManagement(ArrayList<Cabin> cabins, int lostAliens, int lostCrew) {
        this.cabins = cabins;
        this.lostAliens = lostAliens;
        this.lostCrew = lostCrew;
    }

    public ArrayList<Cabin> getCabins() {
        return cabins;
    }
    public int getLostAliens() {
        return lostAliens;
    }
    public int getLostCrew() {
        return lostCrew;
    }
}
