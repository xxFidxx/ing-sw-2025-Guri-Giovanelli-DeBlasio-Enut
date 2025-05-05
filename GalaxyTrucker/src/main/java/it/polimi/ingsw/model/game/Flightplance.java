package it.polimi.ingsw.model.game;
import it.polimi.ingsw.model.adventureCards.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Flightplance {
    private Placeholder[] spots;
    private Deck deck;
    private Game game;
    private AdventureCard[] fakeCards;

    public Flightplance(int spots, Game game) {
        this.spots = new Placeholder[spots];
        // mi creo delle carte finte per simulare il comportamento
        this.fakeCards = new AdventureCard[]{
            new AbandonedShipCard("AbShipCard1", 2, 1, 3, 4, deck),
            new AbandonedShipCard("AbShipCard2", 2, 1, 2, 3, deck),
            new EpidemicCard("EpCard1", 2, deck),
            new OpenSpaceCard("OpenSpaceCard", 2, deck)
        };

        this.deck = new Deck(fakeCards, this);
        this.game = game;

    }

    public Deck getDeck() {
        return deck;
    }

    public Optional<Player> getNext(Player player) {
            return Optional.empty();
    }

    public Optional<Player> getFirst() {
        return game.getPlayers().stream().max(Comparator.comparingInt(p -> p.getPlaceholder().getPosizione()));
    }

    public Placeholder[] getSpots() {
        return spots;
    }

    public Game getGame() {
        return game;
    }

    public void move(int num, Player chosenPlayer) {
        int extraSteps = 0;
        ArrayList<Player> Players = game.getPlayers();
        int chosenPlayerPosition = chosenPlayer.getPlaceholder().getPosizione();
        if(num>0){
            for(Player p : Players) {
                int playerPosition = p.getPlaceholder().getPosizione();
                if (playerPosition > chosenPlayerPosition && playerPosition <= chosenPlayerPosition + num)
                    extraSteps++;
            }
        }else if(num<0){
            for(Player p : Players){
                int playerPosition = p.getPlaceholder().getPosizione();
                if(playerPosition >= chosenPlayerPosition + num && playerPosition < chosenPlayerPosition)
                    extraSteps++;
            }

        }

        chosenPlayer.getPlaceholder().setPosizione(chosenPlayer.getPlaceholder().getPosizione() + num +extraSteps);

        game.orderPlayers();
    }
}
