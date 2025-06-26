package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

public class AbandonedShipCard extends AdventureCard {
    private int lostDays;
    private int lostCrew;
    private int credits;
    private Player activatedPlayer;

    public AbandonedShipCard(String name, int level, int lostDays, int lostCrew,  int credits) {
        super(name, level);
        this.lostDays = lostDays;
        this.lostCrew = lostCrew;
        this.credits = credits;
    }

    public void activate(){

        activatedPlayer.setCredits(activatedPlayer.getCredits() + credits);
        deck.getFlightPlance().move(-lostDays, activatedPlayer);
    }

    public void setActivatedPlayer(Player activatedPlayer) {
        this.activatedPlayer = activatedPlayer;
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
        if(p.getSpaceshipPlance().getCrew() >= lostCrew){
            return true;
        }
        return false;
    }

}
