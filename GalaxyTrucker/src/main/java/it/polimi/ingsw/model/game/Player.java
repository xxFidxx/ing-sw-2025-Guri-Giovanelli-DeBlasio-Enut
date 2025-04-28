package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.componentTiles.ComponentTile;
import it.polimi.ingsw.model.resources.Planet;
import it.polimi.ingsw.model.componentTiles.Cabin;
import it.polimi.ingsw.model.componentTiles.DoubleEngine;

import java.util.ArrayList;


public class Player {
        private String nickname;
        private Placeholder placeholder;
        private SpaceshipPlance spaceshipPlance;
        private int credits;
        private int numAstronauts;
        private int numAliens;
        private Game game;
        private ComponentTile handTile;

    public Player(String nickname, Game game, int playerNumber) {
        this.nickname = nickname;
        this.placeholder = new Placeholder(playerNumber);
        this.spaceshipPlance = new SpaceshipPlance();
        this.credits = 0;
        this.numAstronauts = 0;
        this.numAliens = 0;
        this.game = game;
        this.handTile = null;
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

        public SpaceshipPlance getSpaceshipPlance() {
            return spaceshipPlance;
        }
        public void setCredits(int credits) {
            this.credits = credits;
        }
        public int getCredits() {
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
        public void setNumEquip(int n){ numAstronauts = n; }
        public void setNumAliens(int n){ numAliens = n; }
        public void setNumAstronauts(int n){ numAstronauts = n; }
        public boolean getResponse() {
            return true;
        }

    public Game getGame() {
        return game;
    }

    public int checkStorage(){
        return 0;
        }
        public int checkCrew(){ return 0; }//CONTROLLA IL NUMERO DELL EQUIPAGGIO


        // io metterei che si chiede se si vogliono caricare i cannoni mentre si conta la potenza, anche perché sembra si faccia così dalle regole
        public int getEngineStrenght() {//è da vedere se controllare qui se ci sono batterie
            int sumPower=0;
            for(int i=0; i < spaceshipPlance.getEngines().size(); i++){
                if(spaceshipPlance.getEngines().get(i) instanceof DoubleEngine)
                    if( ((DoubleEngine) spaceshipPlance.getEngines().get(i)).isCharged())
                        if(askToUseBattery())
                            sumPower=sumPower+ spaceshipPlance.getEngines().get(i).getPower();
                    else System.out.println("no battery available");

                sumPower = sumPower + spaceshipPlance.getEngines().get(i).getPower();}
            return sumPower;
        }

        public float getFireStrenght() {
            float sumPower=0;
            for(int i=0; i < spaceshipPlance.getCannons().size(); i++)
                sumPower = sumPower + spaceshipPlance.getCannons().get(i).getPower();
            return sumPower;
        }

        public int checkExposedConnectors(){
            int sumExposed=0;
            return 0;
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

        public boolean askToUseBattery(){

            return true; //CHIEDE AL GIOCATORE SE VUOLE USARE LA BATTERIA
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

    public void askRemoveCrew(Cabin cabin) {
        // if (cabin.getFigures().isEmpty()) return;
        // invia il prompt per rimuovere un membro dell'equipaggio della cabina
    }

    public Planet choosePlanet(ArrayList<Planet> planets) {
        for (Planet planet : planets) {
            if (!planet.isBusy())
                if (getResponse())
                    return planet;
        }

        return null;
    }


    public void removeCrew(Cabin cabin1, int j1) {//METODO CHE TOGLIE UN MEMBRO DELL EQUIPAGGIO
        cabin1.getFigures()[j1] = null;

    }

    public void loseCrew(int lostOther) {
        int actualLost = 0;
        if(checkCrew() < lostOther)
            actualLost = checkCrew();
        else
            actualLost = lostOther;

        ArrayList<Cabin> playerCrew =getSpaceshipPlance().getCabins();
        for(int i = 0; i< actualLost; i++) {
            int i1=0; //INDICE CABINA
            int j1=0; //INDICE FIGURE
            if (i1 >= 0 && i1 < playerCrew.size()) {
                Cabin cabin1 = playerCrew.get(i1);
                if (j1 >= 0 && j1 < cabin1.getFigures().length) {
                    removeCrew(cabin1, j1);
                } else System.out.println("crew index is outbound");
            }else  System.out.println("cabin index is outbound");
        }
    }

    public void removeMostValuableCargo() {
        // toglie le due merci piu importanti
        // altrimenti toglie due batterie
    }


}

