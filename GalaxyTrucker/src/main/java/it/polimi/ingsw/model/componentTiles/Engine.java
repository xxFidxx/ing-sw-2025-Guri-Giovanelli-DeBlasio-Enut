package it.polimi.ingsw.model.componentTiles;

public class Engine extends ComponentTile{

    protected int power;

    public Engine(ConnectorType[] connectors,String id, int power) {
        super(connectors,id);
        this.power = power;
    }

    public int getPower() {
        return power;
    }
}
