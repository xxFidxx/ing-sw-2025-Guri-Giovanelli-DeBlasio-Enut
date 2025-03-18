package it.polimi.ingsw;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AdventureCardFactory {
    private static final String JSON_FILE = "setup.json";
    private static JsonNode cardData;

    // Caricamento dei dati JSON una volta sola
    static {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            cardData = objectMapper.readTree(new File(JSON_FILE));
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento del file JSON", e);
        }
    }


