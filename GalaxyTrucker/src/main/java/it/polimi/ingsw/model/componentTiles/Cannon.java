package it.polimi.ingsw.model.componentTiles;


public class Cannon extends ComponentTile {
    protected float power;

    public Cannon(ConnectorType[] connectors,int id) {
        super(connectors,id);
        this.power = 1.0F;
    }

    public float getPower() {
        if( connectors[0] == ConnectorType.CANNON )
            return power;
        else
            return (float) (power * 0.5);
    }

    public void setPower(float power) {
        this.power = power;
    }



}
