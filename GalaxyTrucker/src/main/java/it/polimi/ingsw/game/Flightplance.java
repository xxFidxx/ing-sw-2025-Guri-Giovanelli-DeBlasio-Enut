package it.polimi.ingsw.game;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Flightplance {
    private Placeholder[] spots;
    private Deck[] decks;
    private Game game;

    public Flightplance(Placeholder[] spots, Deck[] decks, Game game) {
        this.spots = spots;
        this.decks = decks;
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
        if(num>0){
            for(Player p : Players){
                if(p.getPlaceholder().getPosizione() > chosenPlayer.getPlaceholder().getPosizione() + 1 && p.getPlaceholder().getPosizione() <= chosenPlayer.getPlaceholder().getPosizione() + num)
                    extraSteps++;
                else if(chosenPlayer.getPlaceholder().getPosizione() + num > chosenPlayer.getPlaceholder().getPosizione() + 1 && p.getPlaceholder().getPosizione() <= chosenPlayer.getPlaceholder().getPosizione())
                    extraSteps++;


            }
        }else if(num<0){
            for(Player p : Players){
                 if(chosenPlayer.getPlaceholder().getPosizione() + num > chosenPlayer.getPlaceholder().getPosizione() + 1 && p.getPlaceholder().getPosizione() <= chosenPlayer.getPlaceholder().getPosizione())
                    extraSteps++;
            }
            }

        chosenPlayer.getPlaceholder().setPosizione(chosenPlayer.getPlaceholder().getPosizione() + num +extraSteps);

    }
}
