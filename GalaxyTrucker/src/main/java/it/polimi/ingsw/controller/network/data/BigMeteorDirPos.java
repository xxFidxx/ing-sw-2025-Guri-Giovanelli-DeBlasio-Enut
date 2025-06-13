package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.Direction;

import java.io.Serializable;

public class BigMeteorDirPos extends DataContainer implements Serializable {
    Direction direction;
    int position;

    public BigMeteorDirPos(Direction direction, int position) {
        this.direction = direction;
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getPosition() {
        return position;
    }
}
