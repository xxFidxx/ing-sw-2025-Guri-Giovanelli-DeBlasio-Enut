package it.polimi.ingsw.model.game;

public class Placeholder {
    private final ColorType color;
    private int posizione;

    public Placeholder(int numPlayer) {
        // Assegna il colore in base all'ordine del giocatore (numPlayer)
        this.color = ColorType.values()[numPlayer];
        this.posizione = 0; // Posizione iniziale
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
