package it.polimi.ingsw.model.componentTiles;

public class Engine extends ComponentTile{

    protected int power;

    public Engine(ConnectorType[] connectors,int id) {
        super(connectors,id);
        this.power = 1;
    }

    public int getPower() {
        return power;
    }
}
