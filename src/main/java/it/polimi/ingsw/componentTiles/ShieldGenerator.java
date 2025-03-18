package it.polimi.ingsw.componentTiles;

public class ShieldGenerator extends ComponentTile {

    public ShieldGenerator(ConnectorType[] connectors,Direction direction) {
        super(connectors, direction);
    }


    // da vedere la logica cosa fa
     public boolean checkProtection() {
         if(this.getDirection() == Direction.NORTH)
             return true;
         return false;
     }



}
