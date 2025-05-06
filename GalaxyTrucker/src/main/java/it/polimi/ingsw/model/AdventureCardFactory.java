package it.polimi.ingsw.model;

import it.polimi.ingsw.model.adventureCards.AdventureCard;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Flightplance;
import it.polimi.ingsw.model.game.Game;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AdventureCardFactory {

    public static List<AdventureCard> loadCards(String filePath, Deck deck, Game game) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AdventureCard.class, new AdventureCardDeserializer(game, deck))
                .create();

        try (FileReader reader = new FileReader(filePath)) {
            Type cardListType = new TypeToken<List<AdventureCard>>(){}.getType();
            return gson.fromJson(reader, cardListType);
        }
    }

    public static void main(String[] args) {
        String filePath = "cards.json"; // Replace with your actual path

        // Assuming you have constructors or instances of Deck and Game ready
        ArrayList<String> names = new ArrayList<>(List.of("a", "b"));
        Game game = new Game(names);     // Replace with appropriate constructor or initialization
        Flightplance flightplance = new Flightplance(4, game, null);
        Deck deck = new Deck(null, flightplance);     // Replace with appropriate constructor or initialization

        try {
            List<AdventureCard> cards = AdventureCardFactory.loadCards(filePath, deck, game);

            for (AdventureCard card : cards) {
                System.out.println(card);
            }
        } catch (IOException e) {
            System.err.println("Failed to load cards: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

