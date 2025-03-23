package it.polimi.ingsw.adventureCards;
import it.polimi.ingsw.componentTiles.Direction;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

public abstract class Projectile {
    private Direction direction;
    protected Game game;

    public Projectile(Game game, Direction direction) {
        this.direction = direction;
    }

    public abstract void activate(Player player, int position);

    public Direction getDirection() {
        return direction;
    }

}
