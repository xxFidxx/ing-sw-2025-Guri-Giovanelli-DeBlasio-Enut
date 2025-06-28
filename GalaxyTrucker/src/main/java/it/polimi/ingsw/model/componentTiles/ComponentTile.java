/**
 * Represents a generic component tile in a game. 
 * Tiles can have various connector types, orientations, and states of connectivity, 
 * and can be visually represented in ASCII format.
 */
package it.polimi.ingsw.model.componentTiles;

import it.polimi.ingsw.model.resources.TileSymbols;

import java.io.Serializable;
import java.util.Arrays;

public abstract class ComponentTile implements Serializable {

    /**
     * The unique identifier for the tile.
     */
    private int id;

    /**
     * Array of connectors that describe the connections on the sides of the tile.
     */
    protected ConnectorType[] connectors;

    /**
     * Indicates whether the tile is well-connected in its current state.
     */
    private boolean isWellConnected;

    /**
     * The current rotation of the tile in degrees (90-degree increments).
     */
    private int rotation;

    /**
     * Constructs a ComponentTile with the specified connectors and ID.
     * The tile is initialized with a rotation of 0 degrees.
     *
     * @param connectors The array of connectors assigned to the tile.
     * @param id The unique identifier for this tile.
     */
    public ComponentTile(ConnectorType[] connectors, int id) {
        this.connectors = connectors;
        this.isWellConnected = true;
        this.id = id;
        this.rotation = 0;
    }

    /**
     * Returns the unique identifier of the tile.
     *
     * @return The ID of the tile.
     */
    public int getId() {
        return id;
    }

    /**
     * Determines whether the tile is well-connected.
     *
     * @return {@code true} if the tile is well-connected, {@code false} otherwise.
     */
    public boolean isWellConnected() {
        return isWellConnected;
    }

    /**
     * Sets the connection status of the tile.
     *
     * @param wellConnected The new connection status.
     */
    public void setWellConnected(boolean wellConnected) {
        isWellConnected = wellConnected;
    }

    /**
     * Rotates the tile 90 degrees clockwise.
     * Updates the connectors' positions to reflect the rotation.
     */
    public void rotateClockwise() {
        ConnectorType last = connectors[3];

        for (int i = 3; i > 0; i--) {
            connectors[i] = connectors[i-1];
        }

        connectors[0] = last;
        rotation += 90;
    }

    /**
     * Rotates the tile 90 degrees counterclockwise.
     * Updates the connectors' positions to reflect the rotation.
     */
    public void rotateCounterClockwise() {
        ConnectorType first = connectors[0];

        for (int i = 0; i < 3; i++) {
            connectors[i] = connectors[i+1];
        }

        connectors[3] = first;
        rotation -= 90;
    }

    /**
     * Gets the current rotation of the tile.
     *
     * @return The rotation in degrees (increments of 90).
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Sets a specific rotation for the tile.
     *
     * @param rotation The rotation value to set.
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    /**
     * Gets the array of connectors for the tile.
     *
     * @return The connectors of the tile.
     */
    public ConnectorType[] getConnectors() {
        return connectors;
    }

    /**
     * Provides a string representation of the tile in visual ASCII format.
     * The representation includes connectors and specific module symbols.
     *
     * @return The ASCII representation of the tile.
     */
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

    /**
     * Crafts a 3x3 ASCII representation of the tile based on its properties and connectors.
     *
     * @param tile The tile to be converted into a character matrix representation.
     * @return A 2D character array representing the tile.
     */
    private char[][] tileCrafter(ComponentTile tile) {
        char[][] lines = {
                {'┌', '-', '┐'},
                {'|', ' ', '|'},
                {'└', '-', '┘'}
        };

        if (tile == null) return lines;

        // Center
        char center = TileSymbols.ASCII_TILE_SYMBOLS.get(tiletoString(tile));
        lines[1][1] = center;

        // Connectors
        ConnectorType[] connectors = tile.getConnectors();
        lines[0][1] = connectorToChar(connectors[0]);
        lines[1][2] = connectorToChar(connectors[1]);
        lines[2][1] = connectorToChar(connectors[2]);
        lines[1][0] = connectorToChar(connectors[3]);

        // Shields
        if (tile instanceof ShieldGenerator) {
            boolean[] protection = ((ShieldGenerator) tile).getProtection();
            if (protection[0] && protection[1]) {
                lines[0][2] = 'S';
            } else if (protection[1] && protection[2]) {
                lines[2][2] = 'S';
            } else if (protection[2] && protection[3]) {
                lines[2][0] = 'S';
            } else {
                lines[0][0] = 'S';
            }
        }

        return lines;
    }

    /**
     * Converts a connector type to its character representation based on predefined symbols.
     *
     * @param ct The connector type to be converted.
     * @return The character representing the connector.
     */
    private char connectorToChar(ConnectorType ct) {
        switch (ct) {
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

    /**
     * Converts the tile instance to a string by identifying its type.
     *
     * @param tile The tile instance to convert.
     * @return The string representation of the tile type.
     */
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