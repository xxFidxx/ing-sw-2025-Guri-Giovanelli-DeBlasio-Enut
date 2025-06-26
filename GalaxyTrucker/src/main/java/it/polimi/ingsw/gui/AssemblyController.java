package it.polimi.ingsw.gui;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.data.DataString;
import it.polimi.ingsw.controller.network.data.ListCabinAliens;
import it.polimi.ingsw.controller.network.data.PickedTile;
import it.polimi.ingsw.controller.network.data.TileData;
import it.polimi.ingsw.model.componentTiles.AlienColor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
    private TileData[][] lastSpaceship;

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
    @FXML private GridPane reserveGrid;
    @FXML private ImageView spaceshipDisplay;
    @FXML private Button endCraftingButton;
    @FXML private AnchorPane assemblyPane;
    @FXML private AnchorPane chooseAliensPane;
    @FXML private GridPane cabinsGrid;

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
                imageView.setPreserveRatio(true);
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

        for (int col = 0; col < 2; col++) {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            imageView.setPickOnBounds(true);

            final int c = col + 1000;

            imageView.setOnMouseClicked(event -> {
                try {
                    handleTileClick(c);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            reserveGrid.add(imageView, col, 0);
        }
    }

    private void handleSpaceshipClick(ImageView view, int col, int row) throws RemoteException {
        clientRmi.server.addTile(clientRmi, col, row);
        if (clientRmi.getCurrentState() == GameState.ASSEMBLY) {
            tileDisplay.setImage(null);
            //loadToSpaceship(view, lastIndex);
            coveredTilesGrid.setDisable(false);
            reserveGrid.setDisable(false);
            spaceshipGrid.setDisable(true);
        }
    }

    private void handleTileClick(int index) throws RemoteException {
        clientRmi.server.pickTile(clientRmi, index);
        if (clientRmi.getCurrentState() == GameState.PICKED_TILE) {
            loadTileImage(index);
            coveredTilesGrid.setDisable(true);
            reserveGrid.setDisable(true);
            spaceshipGrid.setDisable(false);
        }
        else if (clientRmi.getCurrentState() == GameState.PICK_RESERVED_CARD) {
            int id = lastSpaceship[0][5 + index - 1000].getId();
            lastSpaceship[0][5 + index - 1000] = new TileData(-1, 0);
            loadTileImage(id);
            coveredTilesGrid.setDisable(true);
            reserveGrid.setDisable(true);
            spaceshipGrid.setDisable(false);
        }
    }

    public void loadTileImage(int index) {
        Image image = getImageFromId(index);
        tileDisplay.setImage(image);

        int rotation = ((PickedTile) clientRmi.getCurrentEvent().getData()).getRotation();
        tileDisplay.setRotate(rotation);

        lastIndex = index;
    }

    public void setLastSpaceship(TileData[][] tileIds) {
        this.lastSpaceship = tileIds;
        Platform.runLater(() -> {
            updateSpaceship();
        });
    }

    private void updateSpaceship() {
        for (Node node : spaceshipGrid.getChildren()) {
            if (node instanceof ImageView imageView) {
                Integer col = GridPane.getColumnIndex(node);
                Integer row = GridPane.getRowIndex(node);

                int id = lastSpaceship[row][col].getId();
                int rotation = lastSpaceship[row][col].getRotation();

                imageView.setRotate(rotation);
                Image img = getImageFromId(id);
                if (img != null) imageView.setImage(img);
            }
        }

        for (Node node : reserveGrid.getChildren()) {
            if (node instanceof ImageView imageView) {
                Integer col = GridPane.getColumnIndex(node);

                int id = lastSpaceship[0][col + 5].getId();
                int rotation = lastSpaceship[0][col + 5].getRotation();

                imageView.setRotate(rotation);
                Image img = getImageFromId(id);
                if (img != null) imageView.setImage(img);
            }
        }
    }


    private static void printTileGrid(TileData[][] grid) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                TileData tile = grid[row][col];
                if (tile != null) {
                    System.out.printf("[id=%d, rot=%d] ", tile.getId(), tile.getRotation());
                } else {
                    System.out.print("[null] ");
                }
            }
            System.out.println();
        }
    }

    private Image getImageFromId(int id) {
        if (id < 0) return null;

        String imagePath = "/tiles/tile" + id + ".jpg";

        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        Image image = new Image(imageStream);

        return image;
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
        reserveGrid.setDisable(false);
        spaceshipGrid.setDisable(true);
    }

    @FXML
    private void handleStore() throws RemoteException {
        clientRmi.server.addReserveSpot(clientRmi);
        tileDisplay.setImage(null);
        coveredTilesGrid.setDisable(false);
        reserveGrid.setDisable(false);
        spaceshipGrid.setDisable(true);
    }

    @FXML
    private void handleEndCrafting() throws Exception {
        clientRmi.server.endCrafting(clientRmi);
    }

    public void adjustShip(TileData[][] tileIds) {
        this.lastSpaceship = tileIds;
        updateSpaceship();

        coveredTilesGrid.setDisable(true);
        reserveGrid.setDisable(true);
        spaceshipGrid.setDisable(false);
        endCraftingButton.setVisible(true);

        for (Node node : spaceshipGrid.getChildren()) {
            if (node instanceof ImageView imageView) {
                Integer col = GridPane.getColumnIndex(node);
                Integer row = GridPane.getRowIndex(node);

                imageView.setOnMouseClicked(event -> {
                    try {
                        clientRmi.server.removeAdjust(clientRmi, col, row);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public void selectShip(TileData[][] tileIds) {
        this.lastSpaceship = tileIds;
        updateSpaceship();

        coveredTilesGrid.setDisable(true);
        reserveGrid.setDisable(true);
        spaceshipGrid.setDisable(false);

        for (Node node : spaceshipGrid.getChildren()) {
            if (node instanceof ImageView imageView) {
                Integer col = GridPane.getColumnIndex(node);
                Integer row = GridPane.getRowIndex(node);

                imageView.setOnMouseClicked(event -> {
                    try {
                        clientRmi.server.selectShipPart(clientRmi, 0);;
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public void chooseAlien(ListCabinAliens cabinAliens) {
        assemblyPane.setVisible(false);
        chooseAliensPane.setVisible(true);

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                final int index = row * 5 + col;
                if (index > cabinAliens.getCabinAliens().size()) {
                    break;
                }

                ImageView imageView = new ImageView();
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                imageView.setPickOnBounds(false);

                imageView.setOnMouseClicked(event -> {
                    try {
                        clientRmi.server.addAlienCabin(clientRmi, index, "b");
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });


                int id = cabinAliens.getCabinAliens().get(index).getCabin().getId();
                int rotation = cabinAliens.getCabinAliens().get(index).getCabin().getRotation();
                Image img = getImageFromId(id);
                imageView.setRotate(rotation);
                imageView.setImage(img);

                cabinsGrid.add(imageView, col, row);
            }
        }
    }

    @FXML
    private void handleDoneChooseAliens() throws RemoteException {
        clientRmi.server.handleEndChooseAliens(clientRmi);
        sceneManager.switchTo("game");
    }
}