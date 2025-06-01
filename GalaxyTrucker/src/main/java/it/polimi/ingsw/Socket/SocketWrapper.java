package it.polimi.ingsw.Socket;

import java.io.Serializable;

public class SocketWrapper implements Serializable {
        private final String command;
        private final Object[] parameters;

        public SocketWrapper(String command, Object[] params) {
            this.command = command;
            this.parameters = params;
        }

        public String getCommand() {
            return command;
        }

        public Object[] getParameters() {
            return parameters;
        }
    }
