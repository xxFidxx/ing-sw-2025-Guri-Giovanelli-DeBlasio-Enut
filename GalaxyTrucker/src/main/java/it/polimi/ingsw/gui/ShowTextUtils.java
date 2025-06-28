package it.polimi.ingsw.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;
import java.util.function.Consumer;

public class ShowTextUtils {


    public static void showTextVolatile(String header, String msg) {
        PopupHandler.getInstance().enqueue(() -> {
            Alert infoBox = new Alert(Alert.AlertType.INFORMATION);
            infoBox.setTitle(header);
            infoBox.setContentText(msg);
            infoBox.setOnHidden(event -> {
                PopupHandler.getInstance().dequeue();
            });
            infoBox.show();
        });
    }

    public static void showTextVolatileImmediate(String header, String msg) {
                Alert infoBox = new Alert(Alert.AlertType.INFORMATION);
                infoBox.setTitle("Messaggio");
                infoBox.setHeaderText(header);
                infoBox.setContentText(msg);
                infoBox.show();
    }


    public static Alert buildYesNo(String header, String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(header);
        alert.setContentText(text);

        ButtonType yes = new ButtonType("Sì", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yes, no);

        return alert;
    }


    public static void askYesNo(String header, String text, Consumer<Boolean> callback) {
        PopupHandler.getInstance().enqueue(() -> {
            Alert confirmBox = buildYesNo(header, text);
            Optional<ButtonType> userChoice = confirmBox.showAndWait();
            boolean confirmed = userChoice.isPresent() &&
                    userChoice.get().getButtonData() == ButtonBar.ButtonData.YES;
            callback.accept(confirmed);
        });
    }

}