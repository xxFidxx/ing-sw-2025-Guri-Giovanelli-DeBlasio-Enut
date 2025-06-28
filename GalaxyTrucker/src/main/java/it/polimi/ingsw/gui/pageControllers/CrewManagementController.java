package it.polimi.ingsw.gui.pageControllers;

import it.polimi.ingsw.controller.network.data.CrewManagement;
import it.polimi.ingsw.controller.network.data.TileData;
import it.polimi.ingsw.gui.Controller;
import it.polimi.ingsw.gui.ShowTextUtils;
import it.polimi.ingsw.model.componentTiles.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

public class CrewManagementController extends Controller {

    @FXML private TextArea textBox;
    @FXML private ImageView background;
    @FXML private GridPane spaceshipGrid;
    @FXML private ImageView spaceshipDisplay;
    TileData[][] lastSpaceship = null;
    private static final Image SPACESHIP_IMAGE;
    int lostCrew;

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

                ImageView icon1 = new ImageView();
                ImageView icon2 = new ImageView();
                ImageView icon3 = new ImageView();

                double iconSize = 20;
                for (ImageView icon : new ImageView[]{icon1, icon2, icon3}) {
                    icon.setFitWidth(iconSize);
                    icon.setFitHeight(iconSize);
                    icon.setPreserveRatio(true);
                    icon.setPickOnBounds(true);
                }

                icon1.setId("icon1_" + col + "_" + row);
                icon2.setId("icon2_" + col + "_" + row);
                icon3.setId("icon3_" + col + "_" + row);

                HBox iconBox = new HBox(4);
                iconBox.setAlignment(Pos.CENTER);
                iconBox.getChildren().addAll(icon1, icon2, icon3);
                StackPane.setAlignment(iconBox, Pos.CENTER);

                tilePane.getChildren().addAll(mainImageView, iconBox);

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

    private StackPane checkPaneCabin(int col, int row) {
        String expectedId = "tile_" + col + "_" + row;
        for (Node node : spaceshipGrid.getChildren()) {
            if (node instanceof StackPane pane && expectedId.equals(pane.getId())) {
                return pane;
            }
        }
        return null;
    }

    private void handleSpaceshipClick(int col, int row) throws RemoteException {
        TileData selectedTile = lastSpaceship[row][col];
        int tileId = selectedTile.getId();

        if (clientRmi.server.removeFigure(clientRmi, tileId)) {
            StackPane tilePane = checkPaneCabin(col, row);
            if (tilePane == null) return;

            outerLoop: for (Node child : tilePane.getChildren()) {
                if (child instanceof HBox hbox) {
                    for (Node iconNode : hbox.getChildren()) {
                        if (iconNode instanceof ImageView iconView && iconView.isVisible()) {
                            iconView.setImage(null);
                            iconView.setVisible(false);
                            lostCrew--;
                            break outerLoop;
                        }
                    }
                }
            }
        } else {
            ShowTextUtils.showTextVolatileImmediate("Error", "You didn't remove anything");
        }

        if(lostCrew==0){
            System.out.println("lostCrew==0");
            clientRmi.server.endCrewManagement(clientRmi);
            textBox.setText("Wait for the other player to be done!");
            disableAllButtons();
        }else{
            textBox.setText("You have to remove " + lostCrew + " crew members,\n please type on the cabin you want to remove a crew component from");
        }

    }

    public void setLastSpaceship(TileData[][] lastSpaceship) {
        this.lastSpaceship = lastSpaceship;
    }

    public void handleNext(ActionEvent actionEvent) {
    }

    public void showShip(CrewManagement data) {
        ArrayList<Cabin> cabins = data.getCabins();
        lostCrew = data.getLostCrew();
        textBox.setDisable(true);
        textBox.setText("You have to remove " + lostCrew + " crew members, please type on the cabin you want to remove a crew component from");

        for (Node node : spaceshipGrid.getChildren()) {
            if (!(node instanceof StackPane tilePane)) continue;

            Integer col = GridPane.getColumnIndex(tilePane);
            Integer row = GridPane.getRowIndex(tilePane);
            if (col == null || row == null) continue;

            TileData tile = lastSpaceship[row][col];
            int id = tile.getId();

            Cabin matchingCabin = null;
            for (Cabin cabin : cabins) {
                if (cabin.getId() == id) {
                    matchingCabin = cabin;
                    break;
                }
            }

            if (matchingCabin != null) {
                int rotation = tile.getRotation();

                for (Node child : tilePane.getChildren()) {
                    if (child instanceof ImageView iv && iv.getId() != null && iv.getId().equals("main_" + col + "_" + row)) {
                        iv.setRotate(rotation);
                        iv.setImage(getImageFromId(id));
                        break;
                    }
                }

                Figure[] figures = matchingCabin.getFigures();
                int figureIndex = 0;

                for (Node child : tilePane.getChildren()) {
                    if (child instanceof HBox hbox) {
                        for (Node iconNode : hbox.getChildren()) {
                            if (iconNode instanceof ImageView iconView) {
                                if (figureIndex < figures.length && figures[figureIndex] != null) {
                                    iconView.setImage(getImageForFigure(figures[figureIndex]));
                                    iconView.setVisible(true);
                                } else {
                                    iconView.setImage(null);
                                    iconView.setVisible(false);
                                }
                                figureIndex++;
                            }
                        }
                    }
                }
            }
        }
    }

    private Image getImageForFigure(Figure figure) {
        String imagePath;

        switch (figure) {
            case Astronaut astronaut -> imagePath = "/symbols/astronaut.png";
            case Alien alien -> {
                if (alien.getColor() == AlienColor.BROWN)
                    imagePath = "/symbols/brownAlien.png";
                else
                    imagePath = "/symbols/purpleAlien.png";
            }
            default -> throw new IllegalStateException("Unexpected value: " + figure);
        }

        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        return new Image(imageStream);
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

        // Disabilita tutti gli altri pulsanti nella scena, se presenti
        Node sceneRoot = spaceshipGrid.getScene().getRoot();
        disableButtonsRec(sceneRoot);
    }

    // Disabilita ricorsivamente tutti i Button o nodi cliccabili
    private void disableButtonsRec(Node node) {
        node.setDisable(true);

        if (node instanceof javafx.scene.Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                disableButtonsRec(child);
            }
        }
    }
}
