package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;

public class AdventureCardData extends DataContainer implements Serializable {
    private final String name;
    private final int level;

    public AdventureCardData(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}
