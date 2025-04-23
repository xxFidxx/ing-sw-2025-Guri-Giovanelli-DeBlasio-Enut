package it.polimi.ingsw.model.resources;
import it.polimi.ingsw.model.componentTiles.Direction;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

public abstract class Projectile {
    protected Direction direction;
    protected Game game;

    public Projectile(Game game, Direction direction) {
        this.game = game;
        this.direction = direction;
    }

    public abstract void activate(Player player, int position);

    public Direction getDirection() {
        return direction;
    }

}
