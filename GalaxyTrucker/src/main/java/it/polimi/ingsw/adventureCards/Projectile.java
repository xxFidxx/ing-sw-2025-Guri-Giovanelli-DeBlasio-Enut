package it.polimi.ingsw.adventureCards;
import it.polimi.ingsw.componentTiles.Direction;

public abstract class Projectile {
    private final Direction direction;
    private final ProjectileSize size;

    public Projectile(Direction direction, ProjectileSize size) {
        this.direction = direction;
        this.size = size;
    }

    public abstract void activate();

    public Direction getDirection() {
        return direction;
    }

    public boolean getSize() {
        return size;
    }
}
