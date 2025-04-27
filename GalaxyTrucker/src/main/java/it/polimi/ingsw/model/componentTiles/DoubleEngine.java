package it.polimi.ingsw.model.componentTiles;

public class DoubleEngine extends Engine {
    private boolean isCharged;


    public DoubleEngine(ConnectorType[] connectors,int id){
        super(connectors,id);
        this.isCharged = false;
    }

    public boolean isCharged() {
        return isCharged;
    }

    public int getPower() {

        return power*2;

    }

    public void setCharged(boolean charged) {
        isCharged = charged;
    }
}
