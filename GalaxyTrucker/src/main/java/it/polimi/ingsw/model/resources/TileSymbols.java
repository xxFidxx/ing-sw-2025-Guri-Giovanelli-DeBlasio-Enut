package it.polimi.ingsw.model.resources;

import java.util.HashMap;
import java.util.Map;

public class TileSymbols {
    public static final Map<String, Character> ASCII_TILE_SYMBOLS = new HashMap<>();
    public static final Map<String, Character> CONNECTOR_SYMBOLS = new HashMap<>();

    static {
        ASCII_TILE_SYMBOLS.put("Cannon", 'C');
        ASCII_TILE_SYMBOLS.put("DoubleCannon", 'D');
        ASCII_TILE_SYMBOLS.put("Engine", 'E');
        ASCII_TILE_SYMBOLS.put("DoubleEngine", 'F');
        ASCII_TILE_SYMBOLS.put("Cabin", 'A');
        ASCII_TILE_SYMBOLS.put("CentralCabin", 'B');
        ASCII_TILE_SYMBOLS.put("StructuralModule", 'S');
        ASCII_TILE_SYMBOLS.put("LifeSupportSystem", 'L');
        ASCII_TILE_SYMBOLS.put("ShieldGenerator", 'G');
        ASCII_TILE_SYMBOLS.put("CargoHolds", 'H');
        ASCII_TILE_SYMBOLS.put("BatteryHolds", 'T');

        CONNECTOR_SYMBOLS.put("universal", '┼'); // All directions
        CONNECTOR_SYMBOLS.put("single", '─');    // Horizontal single line
        CONNECTOR_SYMBOLS.put("double", '═');    // Horizontal double line
        CONNECTOR_SYMBOLS.put("smooth", ' ');    // No connector
        CONNECTOR_SYMBOLS.put("cannon", 'C');    // Cannon-specific symbol
        CONNECTOR_SYMBOLS.put("engine", 'E');    // Engine-specific symbol
    }
}