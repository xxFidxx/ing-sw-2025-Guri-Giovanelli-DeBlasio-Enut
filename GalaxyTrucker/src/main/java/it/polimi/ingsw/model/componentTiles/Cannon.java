package it.polimi.ingsw.model.componentTiles;


public class Cannon extends ComponentTile {
    protected float power;

    public Cannon(ConnectorType[] connectors,String id,float power) {
        super(connectors,id);
        this.power = power;
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

    public boolean checkProtection (Direction direction, int position) {
        return false;
    }

}
