package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.componentTiles.Direction;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

public class BigMeteor extends Projectile {

    public BigMeteor(Game game, Direction direction) {
        super(game, direction);
    }

    @Override
    public boolean activate(Player player, int position) {

        if (player.getSpaceshipPlance().checkProtection(direction, position) == true) {
            return true;
        }
        return false;
        //player.getSpaceshipPlance().takeHit(direction, position);
    }
}
