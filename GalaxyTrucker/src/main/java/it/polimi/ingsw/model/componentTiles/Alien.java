package it.polimi.ingsw.model.componentTiles;

public class Alien extends Figure {
    private AlienColor color;

    public Alien(int slots, AlienColor color) {
        super(slots);
        this.color = color;
    }

    public AlienColor getColor() {
        return color;
    }
}
