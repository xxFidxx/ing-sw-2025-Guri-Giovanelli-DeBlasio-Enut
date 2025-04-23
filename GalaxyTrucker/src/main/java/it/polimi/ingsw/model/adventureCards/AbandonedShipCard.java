package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

public class AbandonedShipCard extends AdventureCard {
    private int lostDays;
    private int lostCrew;
    private int credits;

    public AbandonedShipCard(String name, int level, int lostDays, int lostCrew,  int credits, Deck deck) {
        super(name, level, deck);
        this.lostDays = lostDays;
        this.lostCrew = lostCrew;
        this.credits = credits;
    }

    @Override
    public void activate(){
        Player p = deck.getFlightPlance().getGame().choosePlayer(this);
        if (p == null) {
            System.out.println("No player selected");
            return;
        }
        p.setNumEquip(p.getNumEquip() - lostCrew);
        p.setCredits(p.getCredits() + credits);
        deck.getFlightPlance().move(-lostDays, p);
    }


    public int getLostDays() {
        return lostDays;
    }

    public int getLostCrew() {
        return lostCrew;
    }

    public int getReward(){
        return credits;
    }

    public boolean checkCondition(Player p){
        if(p.getNumEquip() >= lostCrew){
            return true;
        }
        return false;
    }

}
