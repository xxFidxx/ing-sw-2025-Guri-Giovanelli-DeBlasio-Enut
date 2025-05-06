package it.polimi.ingsw.controller.network.data;


import java.io.Serializable;

public class DataString extends DataContainer implements Serializable {
    String text;

    public DataString(String text) {
        super();
        this.text = text;
    }

    public String getText(){
        return text;
    }

}
