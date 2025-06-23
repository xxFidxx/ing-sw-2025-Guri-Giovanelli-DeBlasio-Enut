package it.polimi.ingsw.gui;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.data.PickedTile;
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

public class AssemblyController extends Controller {
    private static final Image COVERED_CARD_IMAGE, SPACESHIP_IMAGE;
    private int lastIndex = 0;


    static {
        try (InputStream in = AssemblyController.class.getResourceAsStream("/tiles/coveredTile.jpg")) {
            File tempFile = File.createTempFile("coveredTile", ".jpg");
            tempFile.deleteOnExit();
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            COVERED_CARD_IMAGE = new Image(tempFile.toURI().toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load covered card image", e);
        }
        try (InputStream in = AssemblyController.class.getResourceAsStream("/spaceship.jpg")) {
            File tempFile = File.createTempFile("spaceship", ".jpg");
            tempFile.deleteOnExit();
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            SPACESHIP_IMAGE = new Image(tempFile.toURI().toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load covered card image", e);
        }
    }

    @FXML private GridPane coveredTilesGrid;
    @FXML private ImageView tileDisplay;
    @FXML private GridPane spaceshipGrid;
    @FXML private ImageView spaceshipDisplay;

    public void initialize() {
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 13; col++) {
                ImageView imageView = new ImageView(COVERED_CARD_IMAGE);
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);

                int index = row * 13 + col;
                imageView.setOnMouseClicked(event -> {
                    try {
                        handleTileClick(index);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

                coveredTilesGrid.add(imageView, col, row);

            }
        }
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 7; col++) {
                ImageView imageView = new ImageView();
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPickOnBounds(true);
                final int c = col;
                final int r = row;

                imageView.setOnMouseClicked(event -> {
                    try {
                        handleSpaceshipClick((ImageView) event.getSource(), c, r);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

                spaceshipGrid.add(imageView, col, row);
            }
        }

        spaceshipDisplay.setImage(SPACESHIP_IMAGE);
        spaceshipGrid.setDisable(true);
    }

    private void handleSpaceshipClick(ImageView view, int col, int row) throws RemoteException {
        System.out.println("handled");
        clientRmi.server.addTile(clientRmi, col, row);
        if (clientRmi.getCurrentState() == GameState.ASSEMBLY) {
            tileDisplay.setImage(null);
            int index = row * 7 + col;
            loadToSpaceship(view, index);
            coveredTilesGrid.setDisable(false);
            spaceshipGrid.setDisable(true);
        }
    }

    private void handleTileClick(int index) throws RemoteException {
        clientRmi.server.pickTile(clientRmi, index);
        if (clientRmi.getCurrentState() == GameState.PICKED_TILE) {
            loadTileImage(index);
            coveredTilesGrid.setDisable(true);
            spaceshipGrid.setDisable(false);
        }
    }

    public void loadToSpaceship(ImageView view, int index) {
        String imagePath = "/tiles/tile" + index + ".jpg";

        InputStream imageStream = getClass().getResourceAsStream(imagePath);

        if (imageStream == null) {
            System.out.println("Image not found: " + imagePath);
            return;
        }

        Image image = new Image(imageStream);
        view.setImage(image);
    }

    public void loadTileImage(int index) {
        String imagePath = "/tiles/tile" + index + ".jpg";

        InputStream imageStream = getClass().getResourceAsStream(imagePath);

        if (imageStream == null) {
            System.out.println("Image not found: " + imagePath);
            return;
        }

        Image image = new Image(imageStream);
        tileDisplay.setImage(image);

        int rotation = ((PickedTile) clientRmi.getCurrentEvent().getData()).getRotation();
        tileDisplay.setRotate(rotation);

        lastIndex = index;
    }

    @FXML
    private void handleNext() {
        sceneManager.switchTo("game");
    }

    @FXML
    private void handleRotate() throws RemoteException {
        clientRmi.server.rotateClockwise(clientRmi);
        loadTileImage(lastIndex);
    }

    @FXML
    private void handlePutBack() throws RemoteException {
        clientRmi.server.putTileBack(clientRmi);
        tileDisplay.setImage(null);
        coveredTilesGrid.setDisable(false);
        spaceshipGrid.setDisable(true);
    }

    @FXML
    private void handleStore() throws RemoteException {
        clientRmi.server.addReserveSpot(clientRmi);
        coveredTilesGrid.setDisable(false);
        spaceshipGrid.setDisable(true);
    }

}