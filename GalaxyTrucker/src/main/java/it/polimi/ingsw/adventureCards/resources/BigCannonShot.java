package it.polimi.ingsw.adventureCards.resources;

import it.polimi.ingsw.componentTiles.Direction;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

public class BigCannonShot extends Projectile {
    public BigCannonShot(Game game, Direction direction) {
        super(game, direction);
    }

    @Override
    public void activate(Player player, int position) {
        player.getSpaceshipPlance().takeHit(direction, position);
    }
}
