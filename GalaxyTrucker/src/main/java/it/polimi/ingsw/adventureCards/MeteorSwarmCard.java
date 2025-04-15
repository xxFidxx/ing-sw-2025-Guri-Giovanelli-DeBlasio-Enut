package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;
import it.polimi.ingsw.resources.Projectile;

import java.util.ArrayList;

public class MeteorSwarmCard extends AdventureCard {
    private Projectile[] meteors;

    public MeteorSwarmCard(String name, int level, Projectile[] meteors, Deck deck) {
        super(name, level, deck);
        this.meteors = meteors;
    }
    /**
     *
     * */
    public void activate() {

        for (Projectile meteor : meteors) {
            Game game = deck.getFlightPlance().getGame();
            int position = game.throwDices();
            ArrayList<Player> players = game.getPlayers();
            for (Player player: players) {
                meteor.activate(player, position);
            }
        }
    }
}
