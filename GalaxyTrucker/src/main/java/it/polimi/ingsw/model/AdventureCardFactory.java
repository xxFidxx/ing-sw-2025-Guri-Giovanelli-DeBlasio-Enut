package it.polimi.ingsw.model;

import it.polimi.ingsw.model.adventureCards.AdventureCard;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
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
}

