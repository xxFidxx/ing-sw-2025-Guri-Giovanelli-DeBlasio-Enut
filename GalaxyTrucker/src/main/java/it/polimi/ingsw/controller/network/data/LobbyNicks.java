package it.polimi.ingsw.controller.network.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class LobbyNicks extends DataContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    ArrayList<String> nicks;

    public LobbyNicks(ArrayList<String> nicks) {
        super();
        this.nicks = nicks;
    }

    public ArrayList<String> getNicks() {
        return nicks;
    }
}
