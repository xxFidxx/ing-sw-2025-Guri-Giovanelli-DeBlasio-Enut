package it.polimi.ingsw.componentTiles;

import java.awt.*;

public abstract class ComponentTile {
    protected ConnectorType[] connectors; // ruotati insieme alla carta dovrebbero avere anche tipi speciali tipo cannone etc...// ruotati insieme alla carta dovrebbero avere anche tipi speciali tipo cannone etc...
    protected Direction direction;// reso inutile
    private boolean IsWellConnected;

    public ComponentTile(ConnectorType[] connectors,Direction direction, boolean isWellConnected) {
        this.connectors = connectors;
        this.direction = direction;
        this.IsWellConnected = true;
    }

    public boolean isWellConnected() {
        return IsWellConnected;
    }

    public void setWellConnected(boolean wellConnected) {
        IsWellConnected = wellConnected;
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

    public Direction getDirection() {
        return direction;
    }

}
