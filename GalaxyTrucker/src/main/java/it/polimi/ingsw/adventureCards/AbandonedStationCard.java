package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

    public class AbandonedStationCard extends AdventureCard {
        private int lostDays;
        private int requiredCrew;
        private GoodsBlock[] reward;


        public AbandonedStationCard(String name, int level, Deck deck, int lostDays, int requiredCrew, GoodsBlock[] reward) {
            super(name, level, deck);
            this.lostDays = lostDays;
            this.requiredCrew = requiredCrew;
            this.reward = reward;
        }

        @Override
        public void activate() {
            Player p = deck.getFlightplance().getGame().choosePlayer(this);


            if (p == null) {
                System.out.println("No player selected");
                return;
            }

            p.cargoManagement(reward);

        }

        private void reward(Player p){
            // p.loadGoods()
        }


        public int getRequiredCrew() {
            return requiredCrew;
        }

        public GoodsBlock[] getReward() {
            return reward;
        }

        public boolean checkCondtition(Player p){
            if(p.getNumEquip() >= requiredCrew){
                return true;
            }

            return false;
        }

    }