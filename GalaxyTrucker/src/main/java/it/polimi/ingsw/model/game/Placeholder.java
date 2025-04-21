package it.polimi.ingsw.model.game;

public class Placeholder {
   private ColorType color;
   private int posizione;

    public Placeholder(int numPlayer) {
        // it choses the color of the player by their login order
        this.color = ColorType.values()[numPlayer];
        this.posizione = 0;
    }

    public ColorType getColor() {
        return color;
    }

    public int getPosizione() {
        return posizione;
    }

    public void setPosizione(int posizione) {
        this.posizione = posizione;
    }

}
