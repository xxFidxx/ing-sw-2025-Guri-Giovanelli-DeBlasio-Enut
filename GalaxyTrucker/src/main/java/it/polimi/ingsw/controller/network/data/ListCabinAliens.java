package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ListCabinAliens extends DataContainer implements Serializable {
    private ArrayList<CabinAliens> cabinAliens;

    public ListCabinAliens(ArrayList<CabinAliens> cabinAliens) {
        this.cabinAliens = cabinAliens;
    }

    public ArrayList<CabinAliens> getCabinAliens() {
        return cabinAliens;
    }
}
