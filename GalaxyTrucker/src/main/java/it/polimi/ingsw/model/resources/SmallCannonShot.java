package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.componentTiles.Direction;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

import java.io.Serializable;

public class SmallCannonShot extends Projectile{

    public SmallCannonShot(Game game, Direction direction) {
        super(game, direction);
    }

    @Override
    public boolean activate(Player player, int position) {
//        if (player.getSpaceshipPlance().getShieldActivation(direction) == true) {
//            return true;
//        }
//        player.getSpaceshipPlance().takeHit(direction, position);
        return false;
    }
}
