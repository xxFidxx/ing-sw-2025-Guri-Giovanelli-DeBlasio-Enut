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
import javafx.scene.effect.ColorAdjust;
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
                // Contenitore per la cella
                StackPane tilePane = new StackPane();
                tilePane.setPrefSize(80, 80);

                // ImageView principale
                ImageView mainImageView = new ImageView();
                mainImageView.setFitWidth(80);
                mainImageView.setFitHeight(80);
                mainImageView.setPreserveRatio(true);
                mainImageView.setPickOnBounds(false);
                mainImageView.setId("main_" + col + "_" + row); // ID unico

                final int c = col;
                final int r = row;

                mainImageView.setOnMouseClicked(event -> {
                    try {
                        handleSpaceshipClick(c, r);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

                // Piccole ImageView aggiuntive
                ImageView icon1 = new ImageView();
                ImageView icon2 = new ImageView();
                ImageView icon3 = new ImageView();

                // Imposta dimensioni e ID
                double iconSize = 20;
                icon1.setFitWidth(iconSize);
                icon1.setFitHeight(iconSize);
                icon1.setPreserveRatio(true);
                icon1.setPickOnBounds(true);
                icon1.setId("icon1_" + col + "_" + row);

                icon2.setFitWidth(iconSize);
                icon2.setFitHeight(iconSize);
                icon2.setPreserveRatio(true);
                icon2.setPickOnBounds(true);
                icon2.setId("icon2_" + col + "_" + row);

                icon3.setFitWidth(iconSize);
                icon3.setFitHeight(iconSize);
                icon3.setPreserveRatio(true);
                icon3.setPickOnBounds(true);
                icon3.setId("icon3_" + col + "_" + row);

                // Posiziona icone in fila orizzontale
                HBox iconBox = new HBox(4);
                iconBox.setAlignment(Pos.CENTER);
                iconBox.getChildren().addAll(icon1, icon2, icon3);
                StackPane.setAlignment(iconBox, Pos.CENTER);

                // Aggiungi immagini al tile
                tilePane.getChildren().addAll(mainImageView, iconBox);

                // Aggiungi il tile alla griglia
                spaceshipGrid.add(tilePane, col, row);
            }
        }

        spaceshipDisplay.setImage(SPACESHIP_IMAGE);
    }


    public void setLastSpaceship(TileData[][] lastSpaceship) {
        this.lastSpaceship = lastSpaceship;
    }

    public void handleNext(ActionEvent actionEvent) {
    }

    private void handleSpaceshipClick(int col, int row) throws RemoteException {
        TileData selectedTile = lastSpaceship[row][col];
        int tileId = selectedTile.getId();

        if(clientRmi.server.removeFigure(clientRmi, tileId)){
            StackPane tilePane = getTilePaneAt(col, row);
            if (tilePane == null) return;


            for (Node child : tilePane.getChildren()) {
                if (child instanceof HBox hbox) {
                    for (Node iconNode : hbox.getChildren()) {
                        if (iconNode instanceof ImageView iconView && iconView.isVisible()) {
                            iconView.setImage(null);
                            iconView.setVisible(false);
                            return;
                        }
                    }
                }
            }
        }else{
            ShowTextUtils.showTextVolatileImmediate("Error", "You didn't remove anything");
        }

    }

    private StackPane getTilePaneAt(int col, int row) {
        for (Node node : spaceshipGrid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return (StackPane) node;
            }
        }
        return null;
    }


    public void showShip(CrewManagement data) {
        ArrayList<Cabin> cabins = data.getCabins();
        int lostCrew = data.getLostCrew();

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

                // Trova la main imageView
                for (Node child : tilePane.getChildren()) {
                    if (child instanceof ImageView iv && iv.getId() != null && iv.getId().equals("main_" + col + "_" + row)) {
                        iv.setRotate(rotation);
                        iv.setImage(getImageFromId(id));
                        break;
                    }
                }

                // Mostra le immagini delle figure
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

        switch (figure){
            case Astronaut astronaut ->{
               imagePath =  "/symbols/astronaut.png";
            }
            case Alien alien->{
                if(alien.getColor() == AlienColor.BROWN)
                    imagePath =  "/symbols/brownAlien.png";
                else
                    imagePath =  "/symbols/purpleAlien.png";
            }
            default -> throw new IllegalStateException("Unexpected value: " + figure);
        }

        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        Image image = new Image(imageStream);

        return image;
    }

    private Image getImageFromId(int id) {
        if (id < 0) return null;

        String imagePath = "/tiles/tile" + id + ".jpg";

        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        Image image = new Image(imageStream);

        return image;
    }
}
