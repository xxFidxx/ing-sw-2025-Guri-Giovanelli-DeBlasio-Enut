package it.polimi.ingsw.adventureCards.resources;
import it.polimi.ingsw.componentTiles.Direction;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

// va eliminata?
public class CannonFire extends Projectile {
    private ProjectileSize size;
    public CannonFire(Game game, Direction direction, ProjectileSize size) {
        super(game, direction);
        this.size = size;
    }

    @Override
    public void activate(Player player, int position) {

    }
}
