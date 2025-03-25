package it.polimi.ingsw.componentTiles;

public abstract class ComponentTile {
    protected final ConnectorType[] connectors; // ruotati insieme alla carta dovrebbero avere anche tipi speciali tipo cannone etc...

    public ComponentTile(ConnectorType[] connectors){
        this.connectors = connectors;
    }

    public void rotateClockwise() {
        ConnectorType last = connectors[3];

        for (int i = 3; i > 0; i--) {
            connectors[i] = connectors[i-1];
        }

        connectors[0] = last;
    }

    public void rotateCounterClockwise() {
        ConnectorType first = connectors[0];

        for (int i = 0; i < 3; i++) {
            connectors[i] = connectors[i+1];
        }

        connectors[3] = first;
    }


    public ConnectorType[] getConnectors() {
        return connectors;
    }

}
