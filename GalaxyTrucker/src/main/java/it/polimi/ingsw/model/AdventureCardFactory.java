package it.polimi.ingsw.model;

import com.sun.tools.javac.Main;
import it.polimi.ingsw.model.adventureCards.AdventureCard;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
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

public class AdventureCardFactory {

    public static List<AdventureCard> loadCards(Game game) throws IOException {
        try {
            URL resourceUrl = Main.class.getClassLoader().getResource("testCards.json");
            if (resourceUrl == null) {
                throw new FileNotFoundException("cards.json not found in classpath");
            }
            String filePath = Paths.get(resourceUrl.toURI()).toFile().getAbsolutePath();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(AdventureCard.class, new AdventureCardDeserializer(game))
                    .create();

            try (FileReader reader = new FileReader(filePath)) {
                Type cardListType = new TypeToken<List<AdventureCard>>(){}.getType();
                return gson.fromJson(reader, cardListType);
            }

        } catch (Exception e) {
            System.err.println("Failed to load cards: " + e.getMessage());
            e.printStackTrace();

        }

        return null;
    }

    public static void main(String[] args) {

        ArrayList<String> names = new ArrayList<>(List.of("a", "b"));
        Game game = new Game(names);

        try {
            List<AdventureCard> cards = AdventureCardFactory.loadCards(game);

            for (AdventureCard card : cards) {
                System.out.println(card);
            }
        } catch (Exception e) {
            System.err.println("Failed to load cards: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

