package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.game.Deck;

import java.io.Serial;
import java.io.Serializable;

public class Card extends DataContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private int level;

    public Card(String name, int level) {
        super();
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
