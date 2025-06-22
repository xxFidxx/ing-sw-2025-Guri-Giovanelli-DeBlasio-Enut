package it.polimi.ingsw.gui;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AssemblyController extends Controller {
    private static final Image COVERED_CARD_IMAGE;

    static {
        try (InputStream in = AssemblyController.class.getResourceAsStream("/coveredCard.jpg")) {
            File tempFile = File.createTempFile("coveredCard", ".jpg");
            tempFile.deleteOnExit();
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            COVERED_CARD_IMAGE = new Image(tempFile.toURI().toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load covered card image", e);
        }
    }

    @FXML private GridPane coveredTilesGrid;

    public void initialize() {
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 13; col++) {
                ImageView imageView = new ImageView(COVERED_CARD_IMAGE);
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