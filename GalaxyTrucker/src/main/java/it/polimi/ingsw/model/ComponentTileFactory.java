package it.polimi.ingsw.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.game.Game;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ComponentTileFactory {

    public static List<ComponentTile> loadTiles(String filePath, Game game) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ComponentTile.class, new ComponentTileDeserializer(game))
                .create();

        try (FileReader reader = new FileReader(filePath)) {
            Type tileListType = new TypeToken<List<ComponentTile>>(){}.getType();
            return gson.fromJson(reader, tileListType);
        }
    }
}
