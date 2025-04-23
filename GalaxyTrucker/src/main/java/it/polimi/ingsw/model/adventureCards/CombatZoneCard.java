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

    public void activate() {
        ArrayList<Player> players = deck.getFlightPlance().getGame().getPlayers();
        Game game = deck.getFlightPlance().getGame();
        int position = 0;
        if( type == type.LOSTCREW ){
            Player minEquipPlayer = players.stream().min(Comparator.comparingInt(Player::getNumEquip)).orElse(null);
            deck.getFlightPlance().move(-lostDays,minEquipPlayer);
            Player minEnginePlayer =players.stream().min(Comparator.comparingInt(Player::getEngineStrenght)).orElse(null);
            minEnginePlayer.loseCrew(lostOther);
            Player minFirePlayer = players.stream().min(Comparator.comparing(Player::getFireStrenght)).orElse(null);
            for( Projectile cannon : cannons ){
                position = game.throwDices();
                cannon.activate(minFirePlayer, position);
            }
        }
        if(type == type.LOSTGOODS){
            Player minFirePlayer =players.stream().min(Comparator.comparing(Player::getFireStrenght)).orElse(null);
            deck.getFlightPlance().move(-lostDays,minFirePlayer);
            Player minEnginePlayer = players.stream().min(Comparator.comparingInt(Player::getEngineStrenght)).orElse(null);
            minEnginePlayer.getSpaceshipPlance().looseGoods(lostOther);
            Player minEquipPlayer = players.stream().min(Comparator.comparingInt(Player::getNumEquip)).orElse(null);
            for( Projectile cannon : cannons ){
                position = game.throwDices();
                cannon.activate(minEquipPlayer, position);
            }
        }

    }

    public int getLostDays() {
        return lostDays;
    }

    public int getLostOther() {
        return lostOther;
    }
}
