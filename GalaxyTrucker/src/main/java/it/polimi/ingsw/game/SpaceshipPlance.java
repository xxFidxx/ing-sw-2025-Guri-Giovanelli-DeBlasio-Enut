package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.CosmicCredit;
import it.polimi.ingsw.componentTiles.ComponentTile;

import java.util.List;

public class SpaceshipPlance {
    private ComponentTile[][] components;
    private ComponentTile[] reserveSpot;

    public SpaceshipPlance() {

    }

    public boolean checkCorrectness(){

    }

    public static class Player {
        private String nickname;
        private Placeholder placeholder;
        private SpaceshipPlance spaceshipPlance;
        private List<CosmicCredit> credits;

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
}
