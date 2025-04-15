package it.polimi.ingsw.bank;

import java.util.ArrayList;

public class Bank {
    private ArrayList <CosmicCredit>  avaiableCredits;
    private ArrayList <GoodsBlock>  avaiableGoodsBlocks;
    private ArrayList <BatteryToken>  avaiableBatteryTokens;

    public Bank(ArrayList<CosmicCredit> avaiableCredits, ArrayList<GoodsBlock> avaiableGoodsBlocks, ArrayList<BatteryToken> avaiableBatteryTokens) {
        this.avaiableCredits = avaiableCredits;
        this.avaiableGoodsBlocks = avaiableGoodsBlocks;
        this.avaiableBatteryTokens = avaiableBatteryTokens;
    }

    public ArrayList <CosmicCredit> getAvaibleCredits(){
        return avaiableCredits;
    }
    public ArrayList <GoodsBlock> getAvaiableGoodsBlocks(){
        return avaiableGoodsBlocks;
    }
    public ArrayList <BatteryToken> getAvaiableBatteryTokens(){
        return avaiableBatteryTokens;
    }
}
