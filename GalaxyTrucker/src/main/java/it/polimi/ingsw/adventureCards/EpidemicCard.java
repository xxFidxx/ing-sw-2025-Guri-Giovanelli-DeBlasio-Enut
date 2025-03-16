package it.polimi.ingsw.adventureCards;

public class EpidemicCard extends AdventureCard implements Penalizable {
    private int lostCrew;

    public EpidemicCard(String name, int level, int lostCrew){
        super(name, level);
        this.lostCrew = lostCrew;
    }

    @Override
    public void activate(){

    }

    @Override
    public void penalize() {

    }

    public int getLostCrew(){
        return lostCrew;
    }
}
