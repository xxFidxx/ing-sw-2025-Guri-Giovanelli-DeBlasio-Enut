package it.polimi.ingsw.model;// AdventureCardDeserializer.java
import com.google.gson.*;
import it.polimi.ingsw.model.adventureCards.*;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.Direction;
import it.polimi.ingsw.model.game.ColorType;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.resources.*;
import it.polimi.ingsw.model.resources.Planet;
import it.polimi.ingsw.model.resources.Projectile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class AdventureCardDeserializer implements JsonDeserializer<AdventureCard> {

    private final Game game;

    public AdventureCardDeserializer(Game game) {
        this.game = game;
    }

    private Projectile instantiateProjectile(JsonObject p) {
        int typeCode = p.get("type").getAsInt();
        Direction dir = Direction.values()[ p.get("direction").getAsInt() ];
        switch (typeCode) {
            case 0: return new SmallCannonShot(game, dir);
            case 1: return new BigCannonShot(game, dir);
            case 2: return new SmallMeteor(game, dir);
            case 3: return new BigMeteor(game, dir);
            default: throw new JsonParseException("Unknown projectile type: " + typeCode);
        }
    }

    @Override
    public AdventureCard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("type").getAsString();
        String name = obj.get("name").getAsString();
        int level = obj.get("level").getAsInt();

        switch (type) {
            case "AbandonedShipCard": {
                int lostDays = obj.get("lostDays").getAsInt();
                int lostCrew = obj.get("lostCrew").getAsInt();
                int credits  = obj.get("credits").getAsInt();
                return new AbandonedShipCard(name, level, lostDays, lostCrew, credits);
            }
            case "AbandonedStationCard": {
                int lostDays      = obj.get("lostDays").getAsInt();
                int requiredCrew  = obj.get("requiredCrew").getAsInt();
                JsonArray rewArr = obj.get("reward").getAsJsonArray();
                GoodsBlock[] reward = new GoodsBlock[rewArr.size()];
                for (int i = 0; i < rewArr.size(); i++) {
                    reward[i] = new GoodsBlock(ColorType.values()[rewArr.get(i).getAsInt()]);
                }
                return new AbandonedStationCard(name, level, lostDays, requiredCrew, reward);
            }
            case "CombatZoneCard": {
                int lostDays    = obj.get("lostDays").getAsInt();
                int lostOther   = obj.get("lostOther").getAsInt();
                CombatZoneType combatZoneType = CombatZoneType.values()[obj.get("combatZoneType").getAsInt()];
                JsonArray cans = obj.get("cannons").getAsJsonArray();
                Projectile[] cannons = new Projectile[cans.size()];
                for (int i = 0; i < cans.size(); i++) {
                    cannons[i] = instantiateProjectile(cans.get(i).getAsJsonObject());
                }
                return new CombatZoneCard(name, level, lostDays, lostOther, cannons, combatZoneType);
            }
            case "EpidemicCard": {
                return new EpidemicCard(name, level);
            }
            case "MeteorSwarmCard": {
                JsonArray mets = obj.get("meteors").getAsJsonArray();
                Projectile[] meteors = new Projectile[mets.size()];
                for (int i = 0; i < mets.size(); i++) {
                    meteors[i] = instantiateProjectile(mets.get(i).getAsJsonObject());
                }
                return new MeteorSwarmCard(name, level, meteors);
            }
            case "OpenSpaceCard": {
                return new OpenSpaceCard(name, level);
            }
            case "PiratesCard": {
                int cannonStrength = obj.get("cannonStrength").getAsInt();
                int lostDays2       = obj.get("lostDays").getAsInt();
                JsonArray shotsArr  = obj.get("shots").getAsJsonArray();
                Projectile[] shots = new Projectile[shotsArr.size()];
                for (int i = 0; i < shotsArr.size(); i++) {
                    shots[i] = instantiateProjectile(shotsArr.get(i).getAsJsonObject());
                }
                int rewardPirates = obj.get("reward").getAsInt();
                return new PiratesCard(name, level, cannonStrength, lostDays2, shots, rewardPirates);
            }
            case "PlanetsCard": {
                int lostDays3 = obj.get("lostDays").getAsInt();
                JsonArray pls  = obj.get("planets").getAsJsonArray();
                ArrayList<Planet> planets = new ArrayList<>();
                for (int i = 0; i < pls.size(); i++) {
                    JsonArray goodsArr = pls.get(i).getAsJsonArray();
                    GoodsBlock[] goods = new GoodsBlock[goodsArr.size()];
                    for (int j = 0; j < goodsArr.size(); j++) {
                        goods[j] = new GoodsBlock(ColorType.values()[goodsArr.get(j).getAsInt()]);
                    }
                    Planet planet = new Planet(goods, false);
                    planets.add(planet);
                }
                return new PlanetsCard(name, level, planets, lostDays3);
            }
            case "SlaversCard": {
                int lostCrew2 = obj.get("lostCrew").getAsInt();
                int lostDays2       = obj.get("lostDays").getAsInt();
                int reward2   = obj.get("reward").getAsInt();
                int cannonStrength = obj.get("cannonStrength").getAsInt();

                return new SlaversCard(name, level, cannonStrength, lostDays2, lostCrew2, reward2);
            }
            case "SmugglersCard": {
                int lossMalus = obj.get("lossMalus").getAsInt();
                int cannonStrength = obj.get("cannonStrength").getAsInt();
                int lostDays       = obj.get("lostDays").getAsInt();
                JsonArray rewArr = obj.get("reward").getAsJsonArray();
                GoodsBlock[] reward3 = new GoodsBlock[rewArr.size()];
                for (int i = 0; i < rewArr.size(); i++) {
                    reward3[i] = new GoodsBlock(ColorType.values()[rewArr.get(i).getAsInt()]);
                }
                return new SmugglersCard(name, level, cannonStrength, lostDays, lossMalus, reward3);
            }
            case "StardustCard": {
                int lostDays4 = obj.get("lostDays").getAsInt();
                return new StardustCard(name, level, lostDays4);
            }
            default:
                throw new JsonParseException("Unknown card type: " + type);
        }
    }
}
