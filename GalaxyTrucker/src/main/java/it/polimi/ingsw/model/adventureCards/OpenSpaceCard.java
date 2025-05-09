package it.polimi.ingsw.model.adventureCards;

import it.polimi.ingsw.model.game.Deck;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OpenSpaceCard extends AdventureCard {
    private Player activatedPlayer;

    public OpenSpaceCard(String name, int level, Deck deck) {
        super(name, level, deck);
    }

    public OpenSpaceCard(String name, int level) {
        super(name, level);
    }

    @Override
    public void activate() {
        int power = activatedPlayer.getEngineStrenght();
        deck.getFlightPlance().move(power, activatedPlayer);
    }

    public void setActivatedPlayer(Player activatedPlayer) {
        this.activatedPlayer = activatedPlayer;
    }
}
