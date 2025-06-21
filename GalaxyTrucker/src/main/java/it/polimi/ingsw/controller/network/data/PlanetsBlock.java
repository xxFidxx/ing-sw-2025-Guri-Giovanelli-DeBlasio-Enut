package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.resources.Planet;

import java.io.Serializable;
import java.util.ArrayList;

public class PlanetsBlock extends DataContainer implements Serializable {
    private ArrayList<Planet> planets;

    public PlanetsBlock(ArrayList<Planet> planets) {
        this.planets = planets;
    }

    public ArrayList<Planet> getPlanets() {return planets;}

}
