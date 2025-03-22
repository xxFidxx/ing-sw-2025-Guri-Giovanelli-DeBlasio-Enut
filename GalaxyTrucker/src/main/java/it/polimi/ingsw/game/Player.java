package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.CosmicCredit;
import it.polimi.ingsw.componentTiles.Engine;

import java.util.List;




public class Player {
        private String nickname;
        private Placeholder placeholder;
        private SpaceshipPlance spaceshipPlance;
        private List<CosmicCredit> credits;
        private int numAstronauts;
        private int numAliens;

    public Player(String nickname, Placeholder placeholder, SpaceshipPlance spaceshipPlance, List<CosmicCredit> credits, int numAstronauts, int numAliens) {
        this.nickname = nickname;
        this.placeholder = placeholder;
        this.spaceshipPlance = spaceshipPlance;
        this.credits = credits;
        this.numAstronauts = numAstronauts;
        this.numAliens = numAliens;

    }

    public String getNickname() {
            return nickname;
        }

        public Placeholder getPlaceholder() {
            return placeholder;
        }

        public SpaceshipPlance getSpaceshipPlance() {
            return spaceshipPlance;
        }
        public List<CosmicCredit> getCredits() {
            return credits;
        }
        public int getNumAstronauts() {
        return numAstronauts;
        }
        public int getNumAliens() {
        return numAliens;
        }
        public boolean getResponse() {
            return true;
        }

        public boolean checkStorage(){
        return true;
        }

        public float engineStrenght(SpaceshipPlance plance) {
            float sumPower=0;
            for(int i=0; i < plance.getComponents().length; i++) {
                for (int j = 0; j < plance.getComponents()[i].length; j++) {
                    if( plance.getComponents()[i][j] instanceof Engine)
                        sumPower = sumPower + ((Engine) plance.getComponents()[i][j]).getPower();
                }
            }
            return sumPower;
        }
}

