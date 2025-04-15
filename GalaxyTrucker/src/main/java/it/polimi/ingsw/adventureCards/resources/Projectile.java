package it.polimi.ingsw.adventureCards.resources;
import it.polimi.ingsw.componentTiles.Direction;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

public abstract class Projectile {
    protected Direction direction;
    protected Game game;

    public Projectile(Game game, Direction direction) {
        this.direction = direction;
    }

    public abstract void activate(Player player, int position);

    public Direction getDirection() {
        return direction;
    }

}
