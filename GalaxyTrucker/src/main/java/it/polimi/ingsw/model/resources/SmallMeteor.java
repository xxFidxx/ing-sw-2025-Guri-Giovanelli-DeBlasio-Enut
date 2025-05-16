package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.componentTiles.Direction;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

public class SmallMeteor extends Projectile {
    public SmallMeteor(Game game, Direction direction) {
        super(game, direction);
    }

    @Override
    public boolean activate(Player player, int position) {

        if (player.getSpaceshipPlance().checkExposedConnector(direction, position) == false) {
            return true;
        }
        /*if (player.getSpaceshipPlance().getShieldActivation(direction) == true) {
            return false;
        }*/
        return false;
        // player.getSpaceshipPlance().takeHit(direction, position);
    }
}
