package it.polimi.ingsw.resources;

import it.polimi.ingsw.componentTiles.Direction;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

public class BigMeteor extends Projectile {

    public BigMeteor(Game game, Direction direction) {
        super(game, direction);
    }

    @Override
    public void activate(Player player, int position) {

        if (player.getSpaceshipPlance().getCannonActivation(direction, position) == true) {
            return;
        }
        player.getSpaceshipPlance().takeHit(direction, position);
    }
}
