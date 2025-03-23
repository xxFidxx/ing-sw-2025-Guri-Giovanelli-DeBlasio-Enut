package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.Bank.CosmicCredit;
import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

import java.util.ArrayList;
import java.util.List;

public class AbandonedShipCard extends AdventureCard {
    private int lostDays;
    private int lostCrew;
    private ArrayList<CosmicCredit> credits;;

    public AbandonedShipCard(String name, int level, int lostDays, int lostCrew,  ArrayList<CosmicCredit> credits, Deck deck) {
        super(name, level,deck);
        this.lostDays = lostDays;
        this.lostCrew = lostCrew;
        this.credits = credits;
    }

    @Override
    public void activate(){
        Player p = deck.getFlightplance().getGame().choosePlayer(this);


        if (p == null) {
            System.out.println("No player selected");
            return;
        }
        p.setNumEquip(p.getNumEquip() - lostCrew);
        manageCredits(p);
    }


    public int getLostDays() {
        return lostDays;
    }

    public int getLostCrew() {
        return lostCrew;
    }

    public ArrayList<CosmicCredit> getReward(){
        return credits;
    }

    public boolean checkCondition(Player p){
        if(p.getNumEquip() >= lostCrew){
            return true;
        }

        return false;
    }

    public void manageCredits(Player p){
        ArrayList<CosmicCredit> playercredits = p.getCredits();
        playercredits.addAll(credits);
    }
}
