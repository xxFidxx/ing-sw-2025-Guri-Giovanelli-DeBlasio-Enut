package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.componentTiles.Direction;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

public class BigMeteor extends Projectile {

    public BigMeteor(Game game, Direction direction) {
        super(game, direction);
    }

    @Override
    public void activate() {
        int n = game.throwDices();

        for (Player player : game.getPlayers()) {
            if (player.useCannon(n) == true) {
                continue;
            }
            player.takeHit(n);
        }
    }
}
