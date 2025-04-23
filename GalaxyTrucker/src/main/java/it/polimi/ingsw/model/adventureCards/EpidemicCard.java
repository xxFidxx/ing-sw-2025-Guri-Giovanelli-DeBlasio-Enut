package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.componentTiles.Cabin;
import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;

public class EpidemicCard extends AdventureCard  {

    public EpidemicCard(String name, int level, Deck deck){
        super(name, level, deck);
    }

    @Override
    public void activate(){
        // toglie un membro da ogni cabina interconnessa
        Game game = deck.getFlightPlance().getGame();
        for (Player player : game.getPlayers()) {
            ArrayList<Cabin> cabins = player.getSpaceshipPlance().getConnectedCabins();
            for (Cabin cabin : cabins) {
                player.askRemoveCrew(cabin);
            }
        }
    }

}
