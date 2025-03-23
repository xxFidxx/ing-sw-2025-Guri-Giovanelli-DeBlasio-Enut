package it.polimi.ingsw.game;

import it.polimi.ingsw.Bank.CosmicCredit;
import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.componentTiles.Cabin;
import it.polimi.ingsw.adventureCards.Planet;
import it.polimi.ingsw.componentTiles.CargoHolds;
import it.polimi.ingsw.componentTiles.Engine;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.game.ColorType.RED;


public class Player {
        private String nickname;
        private Placeholder placeholder;
        private SpaceshipPlance spaceshipPlance;
        private int credits;
        private int numAstronauts;
        private int numAliens;

    public Player(String nickname, Placeholder placeholder, SpaceshipPlance spaceshipPlance, int credits, int numAstronauts, int numAliens) {
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




        // io metterei che si chiede se si vogliono caricare i cannoni mentre si conta la potenza, anche perché sembra si faccia così dalle regole
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

    public void cargoManagement(GoodsBlock[] cardReward) {

        if (getSpaceshipPlance().checkStorage()==0) {
            System.out.println("Not enough space");
            return;
        }

        ArrayList<CargoHolds> playerCargos = getSpaceshipPlance().getCargoHolds();


        while ("player is done" == false) {
            int i1 = 0; // cargo index
            int i2 = 0;
            int j1 = 0; // good index
            int j2 = 0;
            int k = 0; //card reward's good index

            if ("player input is swap" == true) {


                if (i1 >= 0 && i1 < playerCargos.size() && i2 >= 0 && i2 < playerCargos.size()) {

                    CargoHolds cargo1 = playerCargos.get(i1);
                    CargoHolds cargo2 = playerCargos.get(i2);

                    if (j1 >= 0 && j1 < cargo1.getGoods().length && j2 >= 0 && j2 < cargo2.getGoods().length) {
                        GoodsBlock good1 = cargo1.getGoods()[j1];
                        GoodsBlock good2 = cargo2.getGoods()[j2];


                        if (checkSpecialGoods(cargo1,cargo2,good1,good2))
                            swapGoods(cargo1, cargo2, j1, j2);
                    } else {
                        System.out.println("At least one goods index is outbound");
                    }


                } else {
                    System.out.println("At least one cargo index is outbound");

                }
            } else if ("player input is remove" == true) {
                if (i1 >= 0 && i1 < playerCargos.size()) {
                    CargoHolds cargo1 = playerCargos.get(i1);
                    if(j1 >= 0 && j1 < cargo1.getGoods().length) {
                        removeGoods(cargo1, j1);
                    }else
                        System.out.println("goods index is outbound");
                } else
                    System.out.println("cargo index is outbound");

            } else if ("player input is add" == true) {
                if(i1 >= 0 && i1 < playerCargos.size()) {
                    CargoHolds cargo1 = playerCargos.get(i1);
                    if(j1 >= 0 && j1 < cargo1.getGoods().length && k>=0 && k < cardReward.length) {
                        GoodsBlock good1 = cargo1.getGoods()[j1];
                        GoodsBlock good2 = cardReward[k];
                        if (good1 == null) {
                            if (checkSpecialGoods(cargo1,good2))
                                addGoods(cargo1,cardReward,j1,k);
                        } else {
                            System.out.println("You can't add on a busy spot");

                        }
                    }else
                        System.out.println("At least one goods index is outbound");
                }else
                    System.out.println("cargo index is outbound");
            } else
                System.out.println("player input is incorrect");
        }
    }






    private void swapGoods(CargoHolds cargo1, CargoHolds cargo2, int j1, int j2) {

        GoodsBlock[] goods1 = cargo1.getGoods();
        GoodsBlock[] goods2 = cargo2.getGoods();

        GoodsBlock temp = goods1[j1];
        goods1[j1] = goods2[j2];
        goods2[j2] = temp;

    }

    private boolean checkSpecialGoods(CargoHolds cargo1, CargoHolds cargo2, GoodsBlock good1, GoodsBlock good2) {

        if ((good1.getType() == RED && !cargo2.isSpecial()) || (good2.getType() == RED && !cargo1.isSpecial())) {
            System.out.println("Can't put a Red block in grey cargo");
            return true;

        }
        return false;

    }

    private boolean checkSpecialGoods(CargoHolds cargo, GoodsBlock good) {

        if ((good.getType() == RED && !cargo.isSpecial())) {
            System.out.println("Can't put a Red block in grey cargo");
            return true;

        }
        return false;

    }

    private void removeGoods(CargoHolds cargo1, int j1) {

        cargo1.getGoods()[j1] = null;

    }

    private void addGoods(CargoHolds cargo1,GoodsBlock[] cardReward,int j1, int k) {

        cargo1.getGoods()[j1] = cardReward[k];
        cardReward[k] = null;

    }

    public void askRemoveCrew(Cabin cabin) {
        if (cabin.getCrew().isEmpty()) return;
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

    public void looseGoods(int lostOther) {
        int actualLost = 0;
        if(getSpaceshipPlance().checkStorage() < lostOther)
            actualLost = getSpaceshipPlance().checkStorage();
        else
            actualLost = lostOther;

        ArrayList<CargoHolds> playerCargos = getSpaceshipPlance().getCargoHolds();

        for(int i = 0; i< actualLost; i++) {
            int i1=0; // indice cargo
            int j1=0; // indice good
            if (i1 >= 0 && i1 < playerCargos.size()) {
                CargoHolds cargo1 = playerCargos.get(i1);
                if(j1 >= 0 && j1 < cargo1.getGoods().length) {
                    removeGoods(cargo1, j1);
                }else
                    System.out.println("goods index is outbound");
            } else
                System.out.println("cargo index is outbound");
        }
    }

    public void removeMostValuableCargo() {
        // toglie le due merci piu importanti
        // altrimenti toglie due batterie
    }
}

