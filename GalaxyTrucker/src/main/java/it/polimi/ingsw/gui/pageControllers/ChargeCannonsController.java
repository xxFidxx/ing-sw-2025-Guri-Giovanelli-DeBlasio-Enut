package it.polimi.ingsw.gui.pageControllers;

import it.polimi.ingsw.controller.network.data.DoubleCannonList;
import it.polimi.ingsw.controller.network.data.TileData;
import it.polimi.ingsw.gui.Controller;
import it.polimi.ingsw.gui.ShowTextUtils;
import it.polimi.ingsw.model.componentTiles.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ChargeCannonsController extends Controller {

    @FXML private TextArea textBox;
    @FXML private ImageView background;
    @FXML private GridPane spaceshipGrid;
    @FXML private ImageView spaceshipDisplay;
    TileData[][] lastSpaceship = null;
    private static final Image SPACESHIP_IMAGE;
    ArrayList<Integer> chosenIndices = new ArrayList<>();

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
                StackPane tilePane = new StackPane();
                tilePane.setPrefSize(80, 80);
                tilePane.setId("tile_" + col + "_" + row);

                ImageView mainImageView = new ImageView();
                mainImageView.setFitWidth(80);
                mainImageView.setFitHeight(80);
                mainImageView.setPreserveRatio(true);
                mainImageView.setPickOnBounds(true);
                mainImageView.setId("main_" + col + "_" + row);

                final int c = col;
                final int r = row;

                tilePane.getChildren().addAll(mainImageView);

                tilePane.setOnMouseClicked(event -> {
                    try {
                        handleSpaceshipClick(c, r);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

                spaceshipGrid.add(tilePane, col, row);
            }
        }

        spaceshipDisplay.setImage(SPACESHIP_IMAGE);
    }

    private void handleSpaceshipClick(int col, int row) throws RemoteException {
        TileData selectedTile = lastSpaceship[row][col];
        int tileId = selectedTile.getId();
            chosenIndices.add(tileId);

            //quando esci clear
    }

    public void setLastSpaceship(TileData[][] lastSpaceship) {
        this.lastSpaceship = lastSpaceship;
    }

    public void handleNext(ActionEvent actionEvent) {
    }

    public void showShip(DoubleCannonList data) {
        ArrayList<DoubleCannon> powerCenters = data.getDoubleCannons();
        textBox.setDisable(true);
        textBox.setText("Choose which double cannons you want to charge:");

        for (Node node : spaceshipGrid.getChildren()) {
            if (!(node instanceof StackPane tilePane)) continue;

            Integer col = GridPane.getColumnIndex(tilePane);
            Integer row = GridPane.getRowIndex(tilePane);
            if (col == null || row == null) continue;

            TileData tile = lastSpaceship[row][col];
            int id = tile.getId();

            DoubleCannon matchingDoubleCannon = null;
            for (DoubleCannon doubleCannon : powerCenters) {
                if (doubleCannon.getId() == id) {
                    matchingDoubleCannon = doubleCannon;
                    break;
                }
            }

            if (matchingDoubleCannon != null) {
                int rotation = tile.getRotation();

                for (Node child : tilePane.getChildren()) {
                    if (child instanceof ImageView iv && iv.getId() != null && iv.getId().equals("main_" + col + "_" + row)) {
                        iv.setRotate(rotation);
                        iv.setImage(getImageFromId(id));
                        break;
                    }
                }

            }
        }
    }

    private Image getImageFromId(int id) {
        if (id < 0) return null;
        String imagePath = "/tiles/tile" + id + ".jpg";
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        return new Image(imageStream);
    }

    private void disableAllButtons() {

        for (Node node : spaceshipGrid.getChildren()) {
            if (node instanceof StackPane tilePane) {
                tilePane.setDisable(true);


                for (Node child : tilePane.getChildren()) {
                    if (child instanceof ImageView imageView) {
                        imageView.setDisable(true);
                    } else if (child instanceof HBox hbox) {
                        for (Node iconNode : hbox.getChildren()) {
                            if (iconNode instanceof ImageView iconView) {
                                iconView.setDisable(true);
                            }
                        }
                    }
                }
            }
        }


        Node sceneRoot = spaceshipGrid.getScene().getRoot();
        disableButtonsRec(sceneRoot);
    }


    private void disableButtonsRec(Node node) {
        node.setDisable(true);

        if (node instanceof javafx.scene.Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                disableButtonsRec(child);
            }
        }
    }
}
