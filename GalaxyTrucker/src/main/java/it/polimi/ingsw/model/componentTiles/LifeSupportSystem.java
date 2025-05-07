package it.polimi.ingsw.model.componentTiles;

public class LifeSupportSystem extends ComponentTile{
    private final AlienColor color;


    public LifeSupportSystem(AlienColor color, ConnectorType[] connectors, int id) {
        super(connectors,id);
        this.color = color;
    }

    @Override
    public String toString() {
        return super.toString() + "color=" + color;
    }
}
