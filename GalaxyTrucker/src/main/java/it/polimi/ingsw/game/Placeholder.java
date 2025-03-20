package it.polimi.ingsw.game;

public class Placeholder {
   private ColorType color;
   private int posizione;

    public Placeholder(ColorType color,int posizione) {
        this.color = color;
        this.posizione = posizione;
    }

    public ColorType getColor() {
        return color;
    }

    public void move(int num){
        this.posizione = this.getPosizione() + num;
    }

    public int getPosizione() {
        return posizione;
    }

}
