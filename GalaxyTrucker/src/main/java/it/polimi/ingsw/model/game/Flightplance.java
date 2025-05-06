package it.polimi.ingsw.model.game;
import it.polimi.ingsw.model.adventureCards.*;

import java.util.*;

public class Flightplance {
    private Placeholder[] spots;
    private Deck deck;
    private Game game;
    private AdventureCard[] fakeCards;
    private Map<Player, Placeholder> placeholderByPlayer; // Mappa giocatore -> placeholder

    public Flightplance(int spots, Game game, ArrayList<Player> players) {
        this.spots = new Placeholder[spots];
        this.placeholderByPlayer = new HashMap<>();

        // Crea un placeholder per ogni giocatore e associalo al colore
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Placeholder placeholder = new Placeholder(i);
            placeholderByPlayer.put(player, placeholder);
            this.spots[i] = placeholder;
        }
        // mi creo delle carte finte per simulare il comportamento
        this.fakeCards = new AdventureCard[]{
            new AbandonedShipCard("AbShipCard1", 2, 1, 0, 4, deck),
            new AbandonedShipCard("AbShipCard2", 2, 1, 2, 3, deck),
            new EpidemicCard("EpCard1", 2, deck),
            new OpenSpaceCard("OpenSpaceCard", 2, deck)
        };

        this.deck = new Deck(fakeCards, this);
        this.game = game;

    }

    public Placeholder getPlaceholderByPlayer(Player player) {
        return placeholderByPlayer.get(player);
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
