package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.resources.CombatZoneType;
import it.polimi.ingsw.model.resources.Projectile;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

import java.util.*;

public class CombatZoneCard extends AdventureCard  {
    private int lostDays;
    private CombatZoneType type;
    private int lostOther;
    private Projectile[] cannons;

    public CombatZoneCard(String name, int level, int lostDays, int lostOther, Projectile[] cannons, Deck deck, CombatZoneType type) {
        super(name, level, deck);
        this.lostDays = lostDays;
        this.lostOther = lostOther;
        this.cannons = cannons;
        this.type = type;
    }

    public CombatZoneCard(String name, int level, int lostDays, int lostOther, Projectile[] cannons, CombatZoneType type) {
        super(name, level);
        this.lostDays = lostDays;
        this.lostOther = lostOther;
        this.cannons = cannons;
        this.type = type;
    }

    public void activate() {

    }

    public int getLostDays() {
        return lostDays;
    }

    public int getLostOther() {
        return lostOther;
    }

    public Projectile[] getCannons() {
        return cannons;
    }

    public CombatZoneType getType() { return type; }
}
