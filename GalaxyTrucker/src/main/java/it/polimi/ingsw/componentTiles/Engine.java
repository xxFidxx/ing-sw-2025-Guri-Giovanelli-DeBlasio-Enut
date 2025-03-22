package it.polimi.ingsw.componentTiles;

public class Engine extends ComponentTile{

    protected float power;

    public Engine(ConnectorType[] connectors,Direction direction, float power) {
        super(connectors, direction);
        this.power = power;
    }

    public float getPower() {
        return power;
    }
}
