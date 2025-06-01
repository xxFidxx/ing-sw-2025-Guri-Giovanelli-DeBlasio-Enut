package it.polimi.ingsw.controller.network.data;

import java.io.Serializable;

public class ServerResponse extends DataContainer implements Serializable {
    private final boolean response;
    private final boolean isError;
    private final String error;
    public ServerResponse(boolean response, boolean isError, String error) {
        this.response = response;
        this.isError = isError;
        this.error = error;
    }

    public boolean getResponse() {
        return response;
    }

    public boolean isError() {
        return isError;
    }

    public String getError() {
        return error;
    }
}
