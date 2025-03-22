package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.componentTiles.Direction;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

public class SmallMeteor extends Projectile{
    public SmallMeteor(Game game, Direction direction) {
        super(game, direction);
    }

    @Override
    public void activate() {
        int n = game.throwDices();

        for (Player player : game.getPlayers()) {
            if (player.checkExposedConnector(n) == false) {
                continue;
            }
            if (player.askActivateShield() == true) {
                continue;
            }
            player.takeHit(n);
        }


    }
}
