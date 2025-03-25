package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

import java.util.*;

public class CombatZoneCard extends AdventureCard  {
    private int lostDays;
    private CombatZoneType type;
    private int lostOther;
    private SmallCannonShot[] smallShots;
    private BigCannonShot bigShot;

    public CombatZoneCard(String name, int level, int lostDays, int lostOther, CannonFire[] shots, Deck deck) {
        super(name, level, deck);
        this.lostDays = lostDays;
        this.lostOther = lostOther;
        this.smallShots = smallShots;
        this.bigShot = bigShot;
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
            position = game.throwDices();
            smallShots[0].activate(minFirePlayer,position);
            position = game.throwDices();
            bigShot.activate(minFirePlayer,position);

        }
        if(type == type.LOSTGOODS){
            Player minFirePlayer =players.stream().min(Comparator.comparing(Player::getFireStrenght)).orElse(null);
            deck.getFlightPlance().move(-lostDays,minFirePlayer);
            Player minEnginePlayer = players.stream().min(Comparator.comparingInt(Player::getEngineStrenght)).orElse(null);
            minEnginePlayer.looseGoods(lostOther);
            Player minEquipPlayer = players.stream().min(Comparator.comparingInt(Player::getNumEquip)).orElse(null);
            for(SmallCannonShot smallShot : smallShots){
                position = game.throwDices();
                smallShot.activate(minFirePlayer,position);
            }
            position = game.throwDices();
            bigShot.activate(minFirePlayer,position);
        }



    }






    public int getLostDays() {
        return lostDays;
    }

    public int getLostOther() {
        return lostOther;
    }
}
