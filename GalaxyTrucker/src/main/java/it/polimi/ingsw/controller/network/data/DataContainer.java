package it.polimi.ingsw.controller.network.data;

public abstract class DataContainer {
    public abstract void accept(DataContainerVisitor visitor);
}
