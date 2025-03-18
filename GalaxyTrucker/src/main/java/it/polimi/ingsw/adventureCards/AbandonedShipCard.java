package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Player;

public class AbandonedShipCard extends AdventureCard {
    private int lostDays;
    private int requiredCrew;
    private int reward;

    public AbandonedShipCard(String name, int level, Deck deck, int lostDays, int requiredCrew, int reward) {
        super(name, level,deck);
        this.lostDays = lostDays;
        this.requiredCrew = requiredCrew;
        this.reward = reward;
    }

    @Override
    public void activate(){
        Player p = deck.getFlightplance().getGame().choosePlayer();

        if(p.getNumAstronauts() >= requiredCrew){

        }else{
            System.out.println("NOT ENOUGH CREW");
        }
    }

    public void reward(){

    }

    public void penalize(){

    }

    public int getLostDays(){
        return lostDays;
    }

    public int getRequiredCrew(){
        return requiredCrew;
    }

    public int getReward(){
        return reward;
    }
}
