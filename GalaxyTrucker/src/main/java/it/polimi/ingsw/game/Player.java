package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.CosmicCredit;

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
}

