package it.polimi.ingsw.model;

import com.google.gson.*;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.componentTiles.AlienColor;
import it.polimi.ingsw.model.game.Game;

import java.lang.reflect.Type;

public class ComponentTileDeserializer implements JsonDeserializer<ComponentTile> {

    private final Game game;

    public ComponentTileDeserializer(Game game) {
        this.game = game;
    }

    private ConnectorType[] parseConnectors(JsonArray array) {
        ConnectorType[] connectors = new ConnectorType[4];
        for (int i = 0; i < 4; i++) {
            connectors[i] = ConnectorType.values()[array.get(i).getAsInt()];
        }
        return connectors;
    }

    @Override
    public ComponentTile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("type").getAsString();
        ConnectorType[] connectors = parseConnectors(obj.get("connectors").getAsJsonArray());

        switch (type) {
            case "Cabin":
                return new Cabin(connectors);
            case "Cannon":
                return new Cannon(connectors);
            case "CargoHolds": {
                boolean isSpecial = obj.get("isSpecial").getAsBoolean();
                return new CargoHolds(connectors, isSpecial);
            }
            case "DoubleCannon":
                return new DoubleCannon(connectors);
            case "DoubleEngine":
                return new DoubleEngine(connectors);
            case "Engine":
                return new Engine(connectors);
            case "LifeSupportSystem": {
                AlienColor color = AlienColor.values()[obj.get("color").getAsInt()]; //connectors[i] = ConnectorType.values()[array.get(i).getAsInt()];
                return new LifeSupportSystem(connectors, color);
            }
            case "PowerCenter":
                return new PowerCenter(connectors);
            case "ShieldGenerator": {
                JsonArray protArr = obj.get("protection").getAsJsonArray();
                boolean[] protection = new boolean[4];
                for (int i = 0; i < 4; i++) {
                    protection[i] = protArr.get(i).getAsBoolean();
                }
                return new ShieldGenerator(connectors, protection);
            }
            case "StructuralModule":
                return new StructuralModule(connectors);
            default:
                throw new JsonParseException("Unknown component tile type: " + type);
        }
    }
}
