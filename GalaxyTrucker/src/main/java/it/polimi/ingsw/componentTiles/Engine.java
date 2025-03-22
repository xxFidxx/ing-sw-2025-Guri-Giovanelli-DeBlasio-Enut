package it.polimi.ingsw.componentTiles;

public class Engine extends ComponentTile{

    protected int power;

    public Engine(ConnectorType[] connectors,Direction direction, int power) {
        super(connectors, direction);
        this.power = power;
    }

    public int getPower() {
        return power;
    }
}
