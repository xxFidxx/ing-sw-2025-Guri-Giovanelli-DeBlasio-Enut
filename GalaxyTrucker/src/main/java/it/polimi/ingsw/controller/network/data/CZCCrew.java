package it.polimi.ingsw.controller.network.data;

import java.io.Serial;
import java.io.Serializable;

public class CZCCrew extends DataContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private int level;
    int lostDays;
    int lostCrew;


    public CZCCrew(String name, int level, int lostDays, int lostCrew) {
        super();
        this.name = name;
        this.level = level;
        this.lostDays = lostDays;
        this.lostCrew = lostCrew;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getLostDays() { return lostDays; }

    public int getLostCrew() { return lostCrew; }

}
