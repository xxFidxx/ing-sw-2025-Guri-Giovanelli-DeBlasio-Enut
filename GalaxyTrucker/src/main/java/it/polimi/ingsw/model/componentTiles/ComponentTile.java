package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.resources.TileSymbols;

import java.io.Serializable;
import java.util.Arrays;

public abstract class ComponentTile implements Serializable {
    private int id;
    protected ConnectorType[] connectors; // ruotati insieme alla carta dovrebbero avere anche tipi speciali tipo cannone etc...// ruotati insieme alla carta dovrebbero avere anche tipi speciali tipo cannone etc...
    private boolean isWellConnected;
    private int rotation;

    public ComponentTile(ConnectorType[] connectors, int id) {
        this.connectors = connectors;
        this.isWellConnected = true;
        this.id = id;
        this.rotation = 0;
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
        rotation += 90;
    }

    public void rotateCounterClockwise() {
        ConnectorType first = connectors[0];

        for (int i = 0; i < 3; i++) {
            connectors[i] = connectors[i+1];
        }

        connectors[3] = first;
        rotation -= 90;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public ConnectorType[] getConnectors() {
        return connectors;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("\n");

        // For each of the 3 lines in a 3×3 tile
        for (int line = 0; line < 3; line++) {
            char[][] tileChars = tileCrafter(this);
            for (int c = 0; c < 3; c++) {
                result.append(tileChars[line][c]);
            }
            result.append('\n');
        }

        return result.toString();
    }

    private char[][] tileCrafter(ComponentTile tile){
        char[][] lines = {
                {'┌', '-', '┐'},
                {'|', ' ', '|'},
                {'└', '-', '┘'}
        };

        if (tile == null) return lines;

        // centro
        char center = TileSymbols.ASCII_TILE_SYMBOLS.get(tiletoString(tile));
        lines[1][1] = center;

        // connettori
        ConnectorType[] connectors = tile.getConnectors();
        lines[0][1] = connectorToChar(connectors[0]);
        lines[1][2] = connectorToChar(connectors[1]);
        lines[2][1] = connectorToChar(connectors[2]);
        lines[1][0] = connectorToChar(connectors[3]);

        // scudo
        if (tile instanceof ShieldGenerator) {
            boolean[] protection = ((ShieldGenerator) tile).getProtection();
            if (protection[0] && protection[1]) {
                lines[0][2] = 'S';
            }
            else if (protection[1] && protection[2]) {
                lines[2][2] = 'S';
            }
            else if (protection[2] && protection[3]) {
                lines[2][0] = 'S';
            }
            else {
                lines[0][0] = 'S';
            }
        }

        return lines;
    }

    private char connectorToChar(ConnectorType ct) {
        switch (ct){
            case UNIVERSAL -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("universal");
            }
            case SINGLE -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("single");
            }
            case DOUBLE -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("double");
            }
            case SMOOTH -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("smooth");
            }
            case CANNON -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("cannon");
            }
            case ENGINE -> {
                return TileSymbols.CONNECTOR_SYMBOLS.get("engine");
            }
            default -> {
                return '?';
            }
        }
    }

    private String tiletoString(ComponentTile tile) {
        if (tile != null) {
            switch (tile) {
                case DoubleCannon dc -> {
                    return "DoubleCannon";
                }

                case Cannon c -> {
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
