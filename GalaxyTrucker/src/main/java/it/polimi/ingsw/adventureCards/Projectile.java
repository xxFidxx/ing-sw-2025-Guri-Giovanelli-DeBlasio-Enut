package it.polimi.ingsw.adventureCards;
import it.polimi.ingsw.componentTiles.Direction;
import it.polimi.ingsw.game.Game;

public abstract class Projectile {
    private Direction direction;
    protected Game game;

    public Projectile(Game game, Direction direction) {
        this.direction = direction;
    }

    public abstract void activate();

    public Direction getDirection() {
        return direction;
    }

}
