package it.polimi.ingsw.adventureCards;

import it.polimi.ingsw.componentTiles.Cabin;
import it.polimi.ingsw.game.Deck;
import it.polimi.ingsw.game.Game;
import it.polimi.ingsw.game.Player;

import java.util.ArrayList;

public class EpidemicCard extends AdventureCard  {
    private int lostCrew;

    public EpidemicCard(String name, int level, Deck deck, int lostCrew){
        super(name, level, deck);
        this.lostCrew = lostCrew;
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

    public int getLostCrew(){
        return lostCrew;
    }
}
