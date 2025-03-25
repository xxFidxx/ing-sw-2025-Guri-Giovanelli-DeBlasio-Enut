package it.polimi.ingsw.componentTiles;

public class ShieldGenerator extends ComponentTile {

    public ShieldGenerator(ConnectorType[] connectors,Direction direction) {
        super(connectors);
    }


    // da vedere la logica cosa fa
     public boolean checkProtection(Direction direction) {
        if(connectors[0] == ConnectorType.SHIELD && direction == Direction.NORTH) {
            return true;
        }
        if(connectors[1] == ConnectorType.SHIELD && direction == Direction.EAST) {
            return true;
        }
        if(connectors[2] == ConnectorType.SHIELD && direction == Direction.SOUTH) {
            return true;
        }
        if(connectors[3] == ConnectorType.SHIELD && direction == Direction.WEST) {
            return true;
        }
        return false;
     }



}
