package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.componentTiles.Direction;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

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
