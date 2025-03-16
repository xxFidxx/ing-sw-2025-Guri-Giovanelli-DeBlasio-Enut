package it.polimi.ingsw.componentTiles;

public class Cannon extends ComponentTile {
    private float power;

    public Cannon(ConnectorType[] connectors,Direction direction,float power) {
        super(connectors, direction);
        this.power = power;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }
}
