package it.polimi.ingsw.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.game.Game;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ComponentTileFactory {

    public static List<ComponentTile> loadTiles(Game game) {
        try (var inputStream = ComponentTileFactory.class.getClassLoader().getResourceAsStream("tiles.json")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("tiles/tiles.json non trovato nel classpath!");
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ComponentTile.class, new ComponentTileDeserializer(game))
                    .create();

            try (var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Type tileListType = new TypeToken<List<ComponentTile>>() {}.getType();
                return gson.fromJson(reader, tileListType);
            }

        } catch (Exception e) {
            System.err.println("Errore nel caricamento delle tiles: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        ArrayList<String> names = new ArrayList<>(List.of("a", "b"));
        Game game = new Game(names);

        try {
            List<ComponentTile> tiles = ComponentTileFactory.loadTiles(game);
            for (ComponentTile tile : tiles) {
                System.out.println(tile);
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento delle tiles: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
