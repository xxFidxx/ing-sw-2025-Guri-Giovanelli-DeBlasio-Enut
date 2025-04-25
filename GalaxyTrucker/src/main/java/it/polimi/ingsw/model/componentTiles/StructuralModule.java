package it.polimi.ingsw.model.componentTiles;

public class StructuralModule extends ComponentTile{

    // non ci sarebbe bisogno, perchè in automatico usa quella del padre, ma lo metto per ordine
    public StructuralModule(ConnectorType[] connectors,String id) {
        super(connectors,id);
    }
}
