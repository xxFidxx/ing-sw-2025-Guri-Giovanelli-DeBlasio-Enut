package it.polimi.ingsw.model.componentTiles;

import java.util.Arrays;

public abstract class ComponentTile{
    private int id;
    protected ConnectorType[] connectors; // ruotati insieme alla carta dovrebbero avere anche tipi speciali tipo cannone etc...// ruotati insieme alla carta dovrebbero avere anche tipi speciali tipo cannone etc...
    private boolean isWellConnected;

    public ComponentTile(ConnectorType[] connectors, int id) {
        this.connectors = connectors;
        this.isWellConnected = true;
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public boolean isWellConnected() {
        return isWellConnected;
    }

    public void setWellConnected(boolean wellConnected) {
        isWellConnected = wellConnected;
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

    @Override
    public String toString() {
        return  tiletoString(this) + ": " +
                "connectors=" + Arrays.toString(connectors) + " id=" + id + " ";
    }

    private String tiletoString(ComponentTile tile){
        if (tile != null) {
            switch (tile) {
                case DoubleCannon dc -> {
                    return "DoubleCannon";
                }

                case Cannon c-> {
                    return "Cannon";
                }

                case DoubleEngine de -> {
                    return "DoubleEngine";
                }
                case Engine e -> {
                    return "Engine";
                }
                case Cabin cab -> {
                    return "Cabin";
                }
                case CargoHolds ch -> {
                    return "CargoHolds";
                }

                case ShieldGenerator sg -> {
                    return "ShieldGenerator";
                }

                case LifeSupportSystem lfs -> {
                    return "LifeSupportSystem";
                }

                case PowerCenter pc -> {
                    return "PowerCenter";
                }

                case StructuralModule sm -> {
                    return "StructuralModule";
                }

                default -> {
                    return "not Catched in tiletoString";
                }
            }
        }
        return null;
    }
}
