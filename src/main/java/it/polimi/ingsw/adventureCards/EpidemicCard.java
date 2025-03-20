package it.polimi.ingsw.adventureCards;

public class EpidemicCard extends AdventureCard {
    private int lostCrew;

    public EpidemicCard(String name, int level, int lostCrew){
        super(name, level);
        this.lostCrew = lostCrew;
    }

    public void activate(){

    }

    public void penalize() {

    }

    public int getLostCrew(){
        return lostCrew;
    }
}
