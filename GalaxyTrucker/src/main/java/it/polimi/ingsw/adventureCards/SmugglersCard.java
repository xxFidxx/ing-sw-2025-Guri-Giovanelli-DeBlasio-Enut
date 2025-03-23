package it.polimi.ingsw.adventureCards;
import it.polimi.ingsw.Bank.GoodsBlock;
import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

import java.util.*;

public class SmugglersCard extends EnemyCard {
    private int lossMalus;
    private GoodsBlock[] reward; // da aggiornare con goods block

    public SmugglersCard(String name, int level, Deck deck, int cannonStrength, int lostDays, int lossMalus, GoodsBlock[] reward) {
        super(name, level, deck, cannonStrength, lostDays);
        this.lossMalus = lossMalus;
        this.reward = reward;
    }

    public void activate() {
        Game game = deck.getFlightplance().getGame();
        ArrayList<Player> tmp = game.getPlayers();
        Collections.sort(tmp, Comparator.comparingInt(player -> player.getPlaceholder().getPosizione()));

        Stack<Player> playerStack = new Stack<>();

        for (Player player : tmp) {
            playerStack.push(player);
        }

        while (!playerStack.isEmpty()) {
            Player fightingPlayer = playerStack.pop();
            int out = getFightOutcome(fightingPlayer);

            switch (out) {
                case 1:
                    // vittoria
                    return;
                case -1:
                    // perdita
                    break;
                case 0:
                    // pareggio
                    continue;
            }

        }

    }





    public int getLossMalus() {
        return lossMalus;
    }

    public GoodsBlock[] getReward() {
        return reward;
    }
}
