package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;

public class PlayerColor extends DataContainer implements Serializable {
    private String color;

    public PlayerColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
