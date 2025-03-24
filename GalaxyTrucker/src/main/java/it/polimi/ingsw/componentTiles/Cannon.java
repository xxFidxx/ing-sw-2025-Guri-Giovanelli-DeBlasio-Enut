package it.polimi.ingsw.componentTiles;

public class Cannon extends ComponentTile {
    protected float power;

    public Cannon(ConnectorType[] connectors,Direction direction,float power) {
        super(connectors);
        this.power = power;
    }

    public float getPower() {
        if( direction == direction.NORTH )
            return power;
        else
            return (float) (power * 0.5);
    }

    public void setPower(float power) {
        this.power = power;
    }
}
