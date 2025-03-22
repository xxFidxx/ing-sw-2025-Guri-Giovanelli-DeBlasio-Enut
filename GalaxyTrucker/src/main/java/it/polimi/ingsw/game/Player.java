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
        public int getNumEquip(){
            return numAstronauts + numAliens;
        }
        public boolean getResponse() {
            return true;
        }

        public boolean checkStorage(){
        return true;
        }

        public int getEngineStrenght() {
            int sumPower=0;
            for(int i=0; i < spaceshipPlance.getEngines().size(); i++)
                sumPower = sumPower + spaceshipPlance.getEngines().get(i).getPower();
            return sumPower;
        }

        public float getFireStrenght() {
            float sumPower=0;
            for(int i=0; i < spaceshipPlance.getCannons().size(); i++)
                sumPower = sumPower + spaceshipPlance.getCannons().get(i).getPower();
            return sumPower;
        }

        public boolean checkExposedConnector(int n) {
            // controlla se c'è un connettore esposto li
            return true;
        }

        public boolean askActivateShield() {
            // controlla se ha uno scudo
            // chiede al giocatore se vuole usare lo scudo
            return true;
        }

        public void takeHit(int n) {
            // distrugge cio che viene impattato
            // update della navicella
        }

        public boolean useCannon(int n) {
            // controlla se c'è un cannone su quella riga
            // se è singolo return true
            // se è doppio chiede se lo vuole attivare e returna in base a quello
            return true;
        }
}

