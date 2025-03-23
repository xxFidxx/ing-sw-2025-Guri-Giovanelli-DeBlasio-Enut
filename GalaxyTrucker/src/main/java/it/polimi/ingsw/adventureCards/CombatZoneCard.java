package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        Player[] players = deck.getFlightPlance().getGame().getPlayers();
        Arrays.sort(players, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));
        if( type == type.LOSTCREW ){
            Player minEquipPlayer = Collections.min(List.of(players), Comparator.comparingInt(Player::getNumEquip));
            deck.getFlightPlance().move(-lostDays, minEquipPlayer);
            Player minEnginePlayer = Collections.min(List.of(players), Comparator.comparingInt(Player::getEngineStrenght));
            minEnginePlayer.loseCrew(lostOther);
            Player minFirePlayer = Collections.min(List.of(players), Comparator.comparing(Player::getFireStrenght));
            //AGGIUNGERE METODO PER GESTIRE I COLPI

        }
        if(type == type.LOSTGOODS){
            Player minFirePlayer = Collections.min(List.of(players), Comparator.comparing(Player::getFireStrenght));
            minFirePlayer.getPlaceholder().move(-lostDays);
            Player minEnginePlayer = Collections.min(List.of(players), Comparator.comparingInt(Player::getEngineStrenght));
            minEnginePlayer.looseGoods(lostOther);
            Player minEquipPlayer = Collections.min(List.of(players), Comparator.comparingInt(Player::getNumEquip));
            //AGGIUNGERE METODO PER GESTIRE I COLPI

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
