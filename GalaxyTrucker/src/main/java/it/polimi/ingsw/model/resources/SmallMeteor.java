package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.componentTiles.Direction;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

import java.io.Serializable;

public class SmallMeteor extends Projectile {
    public SmallMeteor(Game game, Direction direction) {
        super(game, direction);
    }

    @Override
    public boolean activate(Player player, int position) {

        if (player.getSpaceshipPlance().checkExposedConnector(direction, position) == false) {
            return true; // se il player non ha dei connettori esposti in quella direzione e posizione ritorna true
        }
        return false; // altrimenti ritorna false
    }
}
