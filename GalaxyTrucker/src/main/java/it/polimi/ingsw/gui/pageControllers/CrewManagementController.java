package it.polimi.ingsw.gui.pageControllers;

import it.polimi.ingsw.controller.network.data.TileData;
import it.polimi.ingsw.gui.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;

import static it.polimi.ingsw.gui.pageControllers.AssemblyController.SPACESHIP_IMAGE;

public class CrewManagementController extends Controller {

    @FXML private ImageView background;
    @FXML private GridPane reserveGrid;
    @FXML private GridPane spaceshipGrid;
    @FXML private ImageView spaceshipDisplay;
    TileData[][] lastSpaceship = null;
    private static final Image SPACESHIP_IMAGE;

    static {
        try (InputStream in = AssemblyController.class.getResourceAsStream("/boards/spaceship.jpg")) {
            File tempFile = File.createTempFile("spaceship", ".jpg");
            tempFile.deleteOnExit();
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            SPACESHIP_IMAGE = new Image(tempFile.toURI().toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load covered card image", e);
        }
    }

    public void initialize() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 7; col++) {
                ImageView imageView = new ImageView();
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                imageView.setPickOnBounds(true);

                final int c = col;
                final int r = row;

                imageView.setOnMouseClicked(event -> {
                    try {
                        handleSpaceshipClick(c, r);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

                spaceshipGrid.add(imageView, col, row);
            }
        }

        spaceshipDisplay.setImage(SPACESHIP_IMAGE);
    }

    public void setLastSpaceship(TileData[][] lastSpaceship) {
        this.lastSpaceship = lastSpaceship;
    }

    public void handleNext(ActionEvent actionEvent) {
    }

    private void handleSpaceshipClick( int col, int row) throws RemoteException {
        TileData selectedTile = lastSpaceship[col][row];
        clientRmi.server.removeFigure(clientRmi,selectedTile.getId());
    }
}
