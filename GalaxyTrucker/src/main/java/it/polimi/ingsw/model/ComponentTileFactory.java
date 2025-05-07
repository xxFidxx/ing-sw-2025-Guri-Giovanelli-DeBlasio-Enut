package it.polimi.ingsw.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.tools.javac.Main;
import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Game;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public static void main(String[] args) {

        ArrayList<String> names = new ArrayList<>(List.of("a", "b"));
        Game game = new Game(names);

        try {
            URL resourceUrl = Main.class.getClassLoader().getResource("tiles.json");
            if (resourceUrl == null) {
                throw new FileNotFoundException("tiles.json not found in classpath");
            }
            String filePath = Paths.get(resourceUrl.toURI()).toFile().getAbsolutePath();

            List<ComponentTile> tiles = ComponentTileFactory.loadTiles(filePath, game);

            for (ComponentTile tile : tiles) {
                System.out.println(tile);
            }
        } catch (Exception e) {
            System.err.println("Failed to load tiles: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
