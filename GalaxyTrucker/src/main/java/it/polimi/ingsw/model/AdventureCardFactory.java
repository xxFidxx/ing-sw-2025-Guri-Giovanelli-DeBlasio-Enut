package it.polimi.ingsw.model;

import it.polimi.ingsw.model.adventureCards.AdventureCard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;


public class AdventureCardFactory {

    public static AdventureCard createCardFromJson(String filePath) {
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader(filePath);

            Type listType = new TypeToken<List<AdventureCard>>() {}.getType();
            List<AdventureCard> cards = gson.fromJson(reader, listType);

            for (AdventureCard card : cards) {
                System.out.println(card);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}


