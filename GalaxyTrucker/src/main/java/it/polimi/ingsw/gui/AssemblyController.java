package it.polimi.ingsw.gui;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class AssemblyController extends Controller {

    @FXML private GridPane coveredTilesGrid;

    public void initialize() {
        Image tileImage = new Image(getClass().getResourceAsStream("/coveredCard.jpg"));

        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 13; col++) {
                ImageView imageView = new ImageView(tileImage);
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);
                coveredTilesGrid.add(imageView, col, row);
            }
        }
    }

    @FXML
    private void handleNext() {
        sceneManager.switchTo("game");
    }
}
