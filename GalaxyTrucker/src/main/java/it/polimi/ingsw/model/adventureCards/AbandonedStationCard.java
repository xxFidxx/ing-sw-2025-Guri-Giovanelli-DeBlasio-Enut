package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.Arrays;

public class AbandonedStationCard extends AdventureCard {
        private int lostDays;
        private int requiredCrew;
        private GoodsBlock[] reward;
        private Player activatedPlayer;


        public AbandonedStationCard(String name, int level, Deck deck, int lostDays, int requiredCrew, GoodsBlock[] reward) {
            super(name, level, deck);
            this.lostDays = lostDays;
            this.requiredCrew = requiredCrew;
            this.reward = reward;
        }

        public AbandonedStationCard(String name, int level, int lostDays, int requiredCrew, GoodsBlock[] reward) {
            super(name, level);
            this.lostDays = lostDays;
            this.requiredCrew = requiredCrew;
            this.reward = reward;
        }
    public void setActivatedPlayer(Player activatedPlayer) {
        this.activatedPlayer = activatedPlayer;
    }

        @Override
        public void activate() {

            activatedPlayer.setReward(reward);
            deck.getFlightPlance().move(-lostDays, activatedPlayer);
        }

        public int getRequiredCrew() {
            return requiredCrew;
        }

        public GoodsBlock[] getReward() {
            return reward;
        }

        public boolean checkCondition(Player p){
            if(p.getNumEquip() >= requiredCrew){
                return true;
            }
            return false;
        }

    @Override
    public String toString() {
        return "AbandonedStationCard{" +
                "lostDays=" + lostDays +
                ", requiredCrew=" + requiredCrew +
                ", reward=" + Arrays.toString(reward) +
                '}';
    }
}