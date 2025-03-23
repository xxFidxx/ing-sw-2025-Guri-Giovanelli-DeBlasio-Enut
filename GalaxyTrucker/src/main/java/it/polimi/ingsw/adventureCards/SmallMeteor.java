package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.componentTiles.Direction;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

public class SmallMeteor extends Projectile{
    public SmallMeteor(Game game, Direction direction) {
        super(game, direction);
    }

    @Override
    public void activate(Player player, int position) {

        if (player.checkExposedConnector(position) == false) {
            return;
        }
        if (player.askActivateShield() == true) {
            return;
        }
        player.takeHit(position);
    }
}
