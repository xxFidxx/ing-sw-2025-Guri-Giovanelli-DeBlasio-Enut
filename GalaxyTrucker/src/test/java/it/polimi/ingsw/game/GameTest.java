package it.polimi.ingsw.game;

import it.polimi.ingsw.model.adventureCards.AdventureCard;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameTest {

    private Game game;
    private AdventureCard card;

    private Player player1;
    private Player player2;
    private Player player3;

    @Before
    public void setUp() {
        // Creiamo normalmente il Game (passiamo qualsiasi lista di nomi, non importa perché la sovrascriviamo)
        game = new Game(new ArrayList<>());

        card = mock(AdventureCard.class);

        player1 = mock(Player.class);
        player2 = mock(Player.class);
        player3 = mock(Player.class);

        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);

        // Usiamo il nuovo setter
        game.setPlayers(players);
    }

    @Test
    public void testChoosePlayer_FindsCorrectPlayer() {
        // Configuriamo i comportamenti: solo player2 soddisfa la condizione e risponde sì
        when(card.checkCondition(player1)).thenReturn(false);
        when(card.checkCondition(player2)).thenReturn(true);
        when(card.checkCondition(player3)).thenReturn(false);

        when(player2.getResponse()).thenReturn(true);

        Player chosen = game.choosePlayer(card);

        assertEquals(player2, chosen);
    }

    @Test
    public void testChoosePlayer_NoPlayerSatisfiesCondition() {
        // Nessun player soddisfa la condizione
        when(card.checkCondition(player1)).thenReturn(false);
        when(card.checkCondition(player2)).thenReturn(false);
        when(card.checkCondition(player3)).thenReturn(false);

        Player chosen = game.choosePlayer(card);

        assertNull(chosen);
    }

    @Test
    public void testChoosePlayer_PlayerSatisfiesButRespondsNo() {
        // Player1 soddisfa la condizione ma risponde no
        when(card.checkCondition(player1)).thenReturn(true);
        when(player1.getResponse()).thenReturn(false);

        when(card.checkCondition(player2)).thenReturn(false);
        when(card.checkCondition(player3)).thenReturn(false);

        Player chosen = game.choosePlayer(card);

        assertNull(chosen);
    }

    @Test
    public void testChoosePlayer_LastPlayerChosen() {
        // Simuliamo che player1 e player3 soddisfano entrambi la condizione
        when(card.checkCondition(player1)).thenReturn(true);
        when(player1.getResponse()).thenReturn(true);

        when(card.checkCondition(player2)).thenReturn(false);

        when(card.checkCondition(player3)).thenReturn(true);
        when(player3.getResponse()).thenReturn(true);

        // Poiché si itera dall'ultimo verso il primo, deve scegliere player3
        Player chosen = game.choosePlayer(card);

        assertEquals(player3, chosen);
    }
}

