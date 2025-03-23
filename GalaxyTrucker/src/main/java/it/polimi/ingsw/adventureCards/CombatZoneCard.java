package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

import java.util.*;

public class CombatZoneCard extends AdventureCard  {
    private int lostDays;
    private CombatZoneType type;
    private int lostOther;
    private CannonFire[] shots; //liste o array?

    public CombatZoneCard(String name, int level, int lostDays, int lostOther, CannonFire[] shots, Deck deck) {
        super(name, level, deck);
        this.lostDays = lostDays;
        this.lostOther = lostOther;
        this.shots = shots;
    }

    public void activate() {
        ArrayList<Player> players = deck.getFlightplance().getGame().getPlayers();
        if( type == type.LOSTCREW ){
            Player minEquipPlayer = players.stream().min(Comparator.comparingInt(Player::getNumEquip)).orElse(null);
            minEquipPlayer.getPlaceholder().move(-lostDays);
            Player minEnginePlayer =players.stream().min(Comparator.comparingInt(Player::getEngineStrenght)).orElse(null);
            minEnginePlayer.loseCrew(lostOther);
            Player minFirePlayer = players.stream().min(Comparator.comparing(Player::getFireStrenght)).orElse(null);
            //AGGIUNGERE METODO PER GESTIRE I COLPI

        }
        if(type == type.LOSTGOODS){
            Player minFirePlayer =players.stream().min(Comparator.comparing(Player::getFireStrenght)).orElse(null);
            minFirePlayer.getPlaceholder().move(-lostDays);
            Player minEnginePlayer = players.stream().min(Comparator.comparingInt(Player::getEngineStrenght)).orElse(null);
            minEnginePlayer.looseGoods(lostOther);
            Player minEquipPlayer = players.stream().min(Comparator.comparingInt(Player::getNumEquip)).orElse(null);
            //AGGIUNGERE METODO PER GESTIRE I COLPI(GUARDARE PIRATI)

        }



    }


    public CannonFire[] getShots() {
        return shots;
    }

    public int getLostDays() {
        return lostDays;
    }

    public int getLostOther() {
        return lostOther;
    }
}
