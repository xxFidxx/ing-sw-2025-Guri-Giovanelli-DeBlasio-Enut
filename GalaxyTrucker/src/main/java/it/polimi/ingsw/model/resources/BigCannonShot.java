package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.componentTiles.Direction;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

public class BigCannonShot extends Projectile {
    public BigCannonShot(Game game, Direction direction) {
        super(game, direction);
    }

    @Override
    public void activate(Player player, int position) {
        player.getSpaceshipPlance().takeHit(direction, position);
    }
}
