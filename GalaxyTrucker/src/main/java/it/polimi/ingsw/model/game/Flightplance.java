package it.polimi.ingsw.model.game;
import it.polimi.ingsw.model.AdventureCardFactory;
import it.polimi.ingsw.model.adventureCards.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class  Flightplance {
    final private Placeholder[] spots;
    private Deck deck;
    final private Game game;
    final private Map<Player, Placeholder> placeholderByPlayer; // Mappa giocatore -> placeholder

    public Flightplance(int spots, Game game, ArrayList<Player> players) {
        this.spots = new Placeholder[spots];
        this.placeholderByPlayer = new HashMap<>();
        this.game = game;
        // Crea un placeholder per ogni giocatore e associalo al colore
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            placeholderByPlayer.put(player, player.getPlaceholder());
            this.spots[i] = player.getPlaceholder();
        }
//        try {
//            List<AdventureCard> cards = AdventureCardFactory.loadCards(
//                    "C:\\Users\\fidel\\OneDrive\\Desktop\\Ing Software\\ing-sw-2025-Guri-Giovanelli-DeBlasio-Enut\\GalaxyTrucker\\src\\main\\resources\\cards.json",
//                    null,
//                    this.game
//            );
//            this.deck = new Deck(cards, this);
//        }
//        catch (Exception e) {  // Cattura tutte le eccezioni
//            System.err.println("Fallback a carte fake: " + e.getMessage());
//        }

        try {
            List<AdventureCard> cards = AdventureCardFactory.loadCards(this.game);
            this.deck = new Deck(cards, this);
        }
        catch (Exception e) {
            System.err.println("Failed to load cards: " + e.getMessage());
        }
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
