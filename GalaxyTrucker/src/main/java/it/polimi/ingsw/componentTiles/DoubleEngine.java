package it.polimi.ingsw.componentTiles;

public class DoubleEngine extends Engine {
    private boolean isCharged;


    public DoubleEngine(ConnectorType[] connectors,Direction direction, int power,boolean isCharged) {
        super(connectors, direction,power);
        this.isCharged = isCharged;
    }

    public boolean isCharged() {
        return isCharged;
    }

    public int getPower() {
        if(isCharged)
            return power;//CAMBIARE IL METODO E CHIEDERE SE SI VOGLIA USARE IL MOTORE DOPPIO
        return 0;
    }
}
