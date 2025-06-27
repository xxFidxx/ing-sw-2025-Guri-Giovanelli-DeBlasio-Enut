package it.polimi.ingsw.controller.network.data;

import java.io.Serial;
import java.io.Serializable;

public class Pirates extends DataContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private int level;
    int lostDays;
    int credits;

    public Pirates(String name, int level, int lostDays,  int credits) {
            super();
            this.name = name;
            this.level = level;
            this.lostDays = lostDays;
            this.credits = credits;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public int getLostDays() { return lostDays; }

        public int getCredits() { return credits; }

}
