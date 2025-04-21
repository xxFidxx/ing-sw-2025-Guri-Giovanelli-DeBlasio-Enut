package it.polimi.ingsw.model.game;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Flightplance {
    private Placeholder[] spots;
    private Deck[] decks;
    private Game game;

    public Flightplance(int spots, Game game) {
        this.spots = new Placeholder[spots];
        this.decks = new Deck[2];
        this.game = game;
    }

    public Deck[] getDecks() {
        return decks;
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

    }
}
