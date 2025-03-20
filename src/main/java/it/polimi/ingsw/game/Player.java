package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.CosmicCredit;

import java.util.List;




public class Player {
        private String nickname;
        private Placeholder placeholder;
        private SpaceshipPlance spaceshipPlance;
        private List<CosmicCredit> credits;

    public Player(String nickname, Placeholder placeholder, SpaceshipPlance spaceshipPlance, List<CosmicCredit> credits) {
        this.nickname = nickname;
        this.placeholder = placeholder;
        this.spaceshipPlance = spaceshipPlance;
        this.credits = credits;

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
}

