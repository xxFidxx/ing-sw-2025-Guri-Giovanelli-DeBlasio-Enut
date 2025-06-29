package it.polimi.ingsw.controller.network.data;

import it.polimi.ingsw.model.componentTiles.Direction;

import java.io.Serializable;

public class SmallCannonDirPos extends DataContainer implements Serializable, ProjectileDirPos {
    Direction direction;
    int position;

    public SmallCannonDirPos(Direction direction, int position) {
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
