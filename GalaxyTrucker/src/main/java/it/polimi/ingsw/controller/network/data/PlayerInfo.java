package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.game.Placeholder;
import it.polimi.ingsw.model.game.SpaceshipPlance;

import java.io.Serial;
import java.io.Serializable;

public class PlayerInfo extends DataContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String nickname;
    private int position;
    private int credits;
    private int numAstronauts;
    private int numAliens;

    public PlayerInfo(String nickname, int position, int credits, int numAstronauts, int numAliens) {
        super();
        this.nickname = nickname;
        this.position = position;
        this.credits = credits;
        this.numAstronauts = numAstronauts;
        this.numAliens = numAliens;
    }
}
