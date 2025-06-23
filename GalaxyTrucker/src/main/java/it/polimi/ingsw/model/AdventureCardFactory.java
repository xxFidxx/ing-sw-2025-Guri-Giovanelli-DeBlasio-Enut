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

    public static List<AdventureCard> loadCards(Game game) {
        try (var inputStream = AdventureCardFactory.class.getClassLoader().getResourceAsStream("cards.json")) {
            if (inputStream == null) {
                throw new FileNotFoundException("cards/cards.json non trovato nel classpath!");
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(AdventureCard.class, new AdventureCardDeserializer(game))
                    .create();

            try (var reader = new java.io.InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8)) {
                Type cardListType = new TypeToken<List<AdventureCard>>() {}.getType();
                return gson.fromJson(reader, cardListType);
            }

        } catch (Exception e) {
            System.err.println("Errore durante il caricamento delle carte: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Meglio di null, evita NullPointerException
        }
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

