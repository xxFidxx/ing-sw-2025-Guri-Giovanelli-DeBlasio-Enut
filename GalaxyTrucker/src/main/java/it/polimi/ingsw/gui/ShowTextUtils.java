package it.polimi.ingsw.gui;

import javafx.scene.control.Alert;

public class ShowTextUtils {

    public static void showTextWait(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Messaggio");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void showTextVolatile(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Messaggio");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.show();
    }

}
