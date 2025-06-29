package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.componentTiles.*;
import it.polimi.ingsw.model.resources.Planet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;


public class Player {
    private String nickname;
    private Placeholder placeholder;
    private SpaceshipPlance spaceshipPlance;
    private int credits;
    private int numAstronauts;
    private int numAliens;
    private Game game;
    private ComponentTile handTile;
    private boolean responded;
    private GoodsBlock[] reward;
    private boolean surrended;

    public Player(String nickname, Game game, int playerNumber) {
        this.nickname = nickname;
        this.placeholder = new Placeholder(playerNumber);
        this.spaceshipPlance = new SpaceshipPlance();
        this.credits = 0;
        this.numAstronauts = 0;
        this.numAliens = 0;
        this.game = game;
        this.handTile = null;
        this.responded = false;
        this.reward = null;
        this.surrended = false;
    }

    public void setSpaceshipPlance(SpaceshipPlance spaceshipPlance) {
        this.spaceshipPlance = spaceshipPlance;
    }

    public void setSurrended(boolean surrended) {
        this.surrended = surrended;
    }

    public boolean isSurrended() {
        return surrended;
    }

    public GoodsBlock[] getReward(){
        return reward;
    }

    public void setReward(GoodsBlock[] reward) {
        this.reward = reward;
    }

    public String getNickname() {
        return nickname;
    }

    public ComponentTile getHandTile() {
        return handTile;
    }

    public void setHandTile(ComponentTile handTile) {
        this.handTile = handTile;
    }

    public Placeholder getPlaceholder() {
        return placeholder;
    }
    public void setPlaceholder(Placeholder placeholder){this.placeholder = placeholder;}


    public SpaceshipPlance getSpaceshipPlance() {
        return spaceshipPlance;
    }
    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getCredits() {
        return credits;
    }



    public int getNumEquip(){
        return numAstronauts + numAliens;
    }
    public void setNumEquip(int n){ numAstronauts = n; }

    public boolean hasResponded() {
        return responded;
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
    }

    public Game getGame() {
        return game;
    }





    /**
     * Calculates and returns the total strength of the spaceship's engines.
     * The engine strength is computed by summing the power of all engines
     * present in the spaceship. For engines of type {@code DoubleEngine},
     * they are only included in the total if they are charged. If there is
     * exactly one brown alien on the spaceship and the total calculated
     * power is greater than zero, an extra strength bonus of 2 is added.
     *
     * @return the total engine strength as an integer
     */
    public int getEngineStrenght() {
        int sumPower=0;



        for(int i=0; i < spaceshipPlance.getEngines().size(); i++){
            Engine e = spaceshipPlance.getEngines().get(i);
            if(e instanceof DoubleEngine){
                if(((DoubleEngine) e).isCharged())
                    sumPower=sumPower+ spaceshipPlance.getEngines().get(i).getPower();
            }
            else
                sumPower = sumPower + spaceshipPlance.getEngines().get(i).getPower();
        }
        if(spaceshipPlance.getBrownAliens() == 1 && sumPower > 0)
            sumPower += 2;
        return sumPower;
    }

    /**
     * Calculates the total firepower of the spaceship, taking into account the power of all
     * cannon components and any applicable bonuses for specific conditions.
     *
     * The method iterates over all cannons in the spaceship's cannon list and adds their
     * power to the total firepower. For DoubleCannon instances, the cannon contributes to
     * the firepower only if it is charged. Additionally, if there is exactly one purple alien
     * present on the spaceship and the total firepower is greater than zero, a bonus of 2
     * is added to the firepower.
     *
     * @return the total firepower as a float value, calculated based on the spaceship's firearms
     *         and any relevant bonuses.
     */
    public float getFireStrenght() {
        float sumPower=0;


        for(int i=0; i < spaceshipPlance.getCannons().size(); i++){
            Cannon c = spaceshipPlance.getCannons().get(i);
            if(c instanceof DoubleCannon) {
                if (((DoubleCannon) c).isCharged())
                    sumPower = sumPower + spaceshipPlance.getCannons().get(i).getPower();
            }
            else
                sumPower = sumPower + spaceshipPlance.getCannons().get(i).getPower();
        }
        if(spaceshipPlance.getPurpleAliens() == 1 && sumPower > 0)
            sumPower += 2;
        return sumPower;
    }


}