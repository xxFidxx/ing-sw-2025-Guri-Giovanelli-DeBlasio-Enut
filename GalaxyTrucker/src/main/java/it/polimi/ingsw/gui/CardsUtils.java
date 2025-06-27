package it.polimi.ingsw.gui;

import it.polimi.ingsw.controller.network.data.Card;
import it.polimi.ingsw.gui.pageControllers.GameController;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CardsUtils {

    private static final Map<String, String> CARD_IMAGE_MAP = new HashMap<>();

    static{
        CARD_IMAGE_MAP.put("Abandoned Ship 1:1", "AbandonedShipCard_1_1.jpg");
        CARD_IMAGE_MAP.put("Abandoned Ship 2:1", "AbandonedShipCard_1_2.jpg");
        CARD_IMAGE_MAP.put("Abandoned Ship 1:2", "AbandonedShipCard_2_1.jpg");
        CARD_IMAGE_MAP.put("Abandoned Ship 2:2", "AbandonedShipCard_2_2.jpg");

        CARD_IMAGE_MAP.put("Abandoned Station 1:1", "AbandonedStationCard_1_1.jpg");
        CARD_IMAGE_MAP.put("Abandoned Station 2:1", "AbandonedStationCard_1_2.jpg");
        CARD_IMAGE_MAP.put("Abandoned Station 1:2", "AbandonedStationCard_2_1.jpg");
        CARD_IMAGE_MAP.put("Abandoned Station 2:2", "AbandonedStationCard_2_2.jpg");

        CARD_IMAGE_MAP.put("Combat Zone:1", "CombatZoneCard_1.jpg");
        CARD_IMAGE_MAP.put("Combat Zone:2", "CombatZoneCard_2.jpg");

        CARD_IMAGE_MAP.put("Epidemic:1", "EpidemicCard.jpg");

        CARD_IMAGE_MAP.put("Meteor Swarm 1:1", "MeteorSwarmCard_1_1.jpg");
        CARD_IMAGE_MAP.put("Meteor Swarm 2:1", "MeteorSwarmCard_1_2.jpg");
        CARD_IMAGE_MAP.put("Meteor Swarm 3:1", "MeteorSwarmCard_1_3.jpg");
        CARD_IMAGE_MAP.put("Meteor Swarm 1:2", "MeteorSwarmCard_2_1.jpg");
        CARD_IMAGE_MAP.put("Meteor Swarm 2:2", "MeteorSwarmCard_2_2.jpg");
        CARD_IMAGE_MAP.put("Meteor Swarm 3:2", "MeteorSwarmCard_2_3.jpg");

        CARD_IMAGE_MAP.put("Open Space 1:1", "OpenSpaceCard_1_1.jpg");
        CARD_IMAGE_MAP.put("Open Space 2:1", "OpenSpaceCard_1_2.jpg");
        CARD_IMAGE_MAP.put("Open Space 3:1", "OpenSpaceCard_1_3.jpg");
        CARD_IMAGE_MAP.put("Open Space 4:1", "OpenSpaceCard_1_4.jpg");
        CARD_IMAGE_MAP.put("Open Space 1:2", "OpenSpaceCard_2_1.jpg");
        CARD_IMAGE_MAP.put("Open Space 2:2", "OpenSpaceCard_2_2.jpg");
        CARD_IMAGE_MAP.put("Open Space 3:2", "OpenSpaceCard_2_3.jpg");

        CARD_IMAGE_MAP.put("Pirates:1", "PiratesCard_1.jpg");
        CARD_IMAGE_MAP.put("Pirates:2", "PiratesCard_2.jpg");

        CARD_IMAGE_MAP.put("Planets 1:1", "PlanetsCard_1_1.jpg");
        CARD_IMAGE_MAP.put("Planets 2:1", "PlanetsCard_1_2.jpg");
        CARD_IMAGE_MAP.put("Planets 3:1", "PlanetsCard_1_3.jpg");
        CARD_IMAGE_MAP.put("Planets 4:1", "PlanetsCard_1_4.jpg");
        CARD_IMAGE_MAP.put("Planets 1:2", "PlanetsCard_2_1.jpg");
        CARD_IMAGE_MAP.put("Planets 2:2", "PlanetsCard_2_2.jpg");
        CARD_IMAGE_MAP.put("Planets 3:2", "PlanetsCard_2_3.jpg");
        CARD_IMAGE_MAP.put("Planets 4:2", "PlanetsCard_2_4.jpg");

        CARD_IMAGE_MAP.put("Slavers:1", "SlaversCard_1.jpg");
        CARD_IMAGE_MAP.put("Slavers:2", "SlaversCard_2.jpg");

        CARD_IMAGE_MAP.put("Smugglers:1", "SmugglersCard_1.jpg");
        CARD_IMAGE_MAP.put("Smugglers:2", "SmugglersCard_2.jpg");

        CARD_IMAGE_MAP.put("Stardust:1", "StardustCard_1.jpg");
        CARD_IMAGE_MAP.put("Stardust:2", "StardustCard_2.jpg");
    }



    public  Image resolveCardImage(String name, int level) {
        String key = name.trim() + ":" + level;
        String filename = CARD_IMAGE_MAP.get(key);
        if (filename == null) throw new IllegalArgumentException("Card not found: " + key);

        InputStream is = getClass().getClassLoader().getResourceAsStream("cards/" + filename);
        if (is == null) throw new IllegalArgumentException("Image not found: " + filename);

        return new Image(is);
    }
}

