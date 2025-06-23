package it.polimi.ingsw.model.resources;

import java.util.HashMap;
import java.util.Map;

public class TileSymbols {
    public static final Map<String, Character> ASCII_TILE_SYMBOLS = new HashMap<>();
    public static final Map<String, Character> CONNECTOR_SYMBOLS = new HashMap<>();

    public static String symbolExplanation =
            """
                    Tile Symbols:
                    'C' – Cannon / Cabin
                    'K' – DoubleCannon
                    'E' – Engine
                    'Y' – DoubleEngine
                    'M' – StructuralModule
                    'L' – LifeSupportSystem
                    'S' – ShieldGenerator
                    'H' – CargoHolds
                    'B' – BatteryHolds
                    'P' – PowerCenter
                    
                    Connector Symbols:
                    '┼' – universal
                    '+' – single
                    '═' – double
                    ' ' – smooth
                    'C' – cannon
                    'E' – engine
                    """;

    static {
        ASCII_TILE_SYMBOLS.put("Cannon", 'C');
        ASCII_TILE_SYMBOLS.put("DoubleCannon", 'K');
        ASCII_TILE_SYMBOLS.put("Engine", 'E');
        ASCII_TILE_SYMBOLS.put("DoubleEngine", 'Y');
        ASCII_TILE_SYMBOLS.put("Cabin", 'C');
        //ASCII_TILE_SYMBOLS.put("CentralCabin", 'K');
        ASCII_TILE_SYMBOLS.put("StructuralModule", 'M');
        ASCII_TILE_SYMBOLS.put("LifeSupportSystem", 'L');
        ASCII_TILE_SYMBOLS.put("ShieldGenerator", 'S');
        ASCII_TILE_SYMBOLS.put("CargoHolds", 'H');
        ASCII_TILE_SYMBOLS.put("BatteryHolds", 'B');
        ASCII_TILE_SYMBOLS.put("PowerCenter", 'P');

        CONNECTOR_SYMBOLS.put("universal", '┼'); // All directions
        CONNECTOR_SYMBOLS.put("single", '+');    // Horizontal single line
        CONNECTOR_SYMBOLS.put("double", '═');    // Horizontal double line
        CONNECTOR_SYMBOLS.put("smooth", ' ');    // No connector
        CONNECTOR_SYMBOLS.put("cannon", 'C');    // Cannon-specific symbol
        CONNECTOR_SYMBOLS.put("engine", 'E');    // Engine-specific symbol
    }
}