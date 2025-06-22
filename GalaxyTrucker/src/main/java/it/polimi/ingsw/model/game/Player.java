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



    public void setResponded(boolean responded) {
            this.responded = responded;
        }

    public Game getGame() {
        return game;
    }




        // io metterei che si chiede se si vogliono caricare i cannoni mentre si conta la potenza, anche perché sembra si faccia così dalle regole
        public int getEngineStrenght() {//è da vedere se controllare qui se ci sono batterie
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

