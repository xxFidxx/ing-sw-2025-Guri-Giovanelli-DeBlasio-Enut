package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;

public class BoardView extends DataContainer implements Serializable {
    private String[] board;

    public BoardView(String[] board) {
        this.board = board;
    }

    public String[] getBoard() {
        return board;
    }
}
