package it.polimi.ingsw.gui.pageControllers;

import it.polimi.ingsw.Server.GameState;
import it.polimi.ingsw.controller.network.data.*;
import it.polimi.ingsw.gui.CardsUtils;
import it.polimi.ingsw.gui.Controller;
import it.polimi.ingsw.gui.ShowTextUtils;
import it.polimi.ingsw.model.componentTiles.ComponentTile;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.util.*;

public class AssemblyController extends Controller {
    private static final Image COVERED_CARD_IMAGE, SPACESHIP_IMAGE;
    private int lastIndex = 0;
    private TileData[][] lastSpaceship;
    private final CardsUtils cardsUtils = new CardsUtils();
    private int lookingDeck = -1;
    Set<Integer> ignoreIds = new HashSet<>(Arrays.asList(32, 33, 51, 60));

    private boolean isHoldingTile = false;
    static {
        try (InputStream in = AssemblyController.class.getResourceAsStream("/tiles/coveredTile.jpg")) {
            File tempFile = File.createTempFile("coveredTile", ".jpg");
            tempFile.deleteOnExit();
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            COVERED_CARD_IMAGE = new Image(tempFile.toURI().toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load covered card image", e);
        }
        try (InputStream in = AssemblyController.class.getResourceAsStream("/boards/spaceship.jpg")) {
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
    @FXML private ImageView deck1covered;
    @FXML private ImageView deck2covered;
    @FXML private ImageView deck3covered;
    @FXML private ImageView card1deck;
    @FXML private ImageView card2deck;
    @FXML private ImageView card3deck;
    @FXML private Button rotateButton;
    @FXML private Button storeButton;
    @FXML private Button putBackButton;


    public void initialize() {
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 13; col++) {
                ImageView imageView = new ImageView(COVERED_CARD_IMAGE);
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);

                int index = row * 13 + col;

                if (!ignoreIds.contains(index)) {
                    imageView.setOnMouseClicked(event -> {
                        try {
                            handleTileClick(index);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                else {
                    imageView.setDisable(true);
                }

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

        deck1covered.setImage(new Image(Objects.requireNonNull(getClass().getResource("/cardsCover/Cover1.jpg")).toExternalForm()));
        deck2covered.setImage(new Image(Objects.requireNonNull(getClass().getResource("/cardsCover/Cover1.jpg")).toExternalForm()));
        deck3covered.setImage(new Image(Objects.requireNonNull(getClass().getResource("/cardsCover/Cover1.jpg")).toExternalForm()));

        card1deck.setVisible(false);
        card2deck.setVisible(false);
        card3deck.setVisible(false);
    }

    private void handleSpaceshipClick(ImageView view, int col, int row) throws RemoteException {
        clientRmi.server.addTile(clientRmi, col, row);
    }

    private void handleTileClick(int index) throws RemoteException {
        isHoldingTile = true;
        clientRmi.server.pickTile(clientRmi, index);
        lastIndex = (index < 1000) ? index : lastSpaceship[0][5+index-1000].getId();
    }

    public void loadTileImage(int index) {
        Image image = getImageFromId(index);
        tileDisplay.setImage(image);

        int rotation = ((PickedTile) clientRmi.getCurrentEvent().getData()).getRotation();
        tileDisplay.setRotate(rotation);

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

                TileData tile = lastSpaceship[row][col];
                int id = tile.getId();
                int rotation = tile.getRotation();

                imageView.setRotate(rotation);
                Image img = getImageFromId(id);
                imageView.setImage(img);

                if (tile.isWellConnected()) {
                    imageView.setEffect(null);
                }
                else {
                    ColorAdjust effect = new ColorAdjust();
                    effect.setBrightness(-0.5);
                    imageView.setEffect(effect);
                }
            }
        }

        for (Node node : reserveGrid.getChildren()) {
            if (node instanceof ImageView imageView) {
                Integer col = GridPane.getColumnIndex(node);

                int id = lastSpaceship[0][col + 5].getId();
                int rotation = lastSpaceship[0][col + 5].getRotation();

                imageView.setRotate(rotation);
                Image img = getImageFromId(id);
                imageView.setImage(img);
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
    private void handleNext(){
        try{
            clientRmi.server.endCrafting(clientRmi);
        }catch (Exception e){
            ShowTextUtils.showTextVolatileImmediate("Exception",e.getMessage());
        }
        //sceneManager.switchTo("game");
    }

    @FXML
    private void handleRotate() throws RemoteException {
        clientRmi.server.rotateClockwise(clientRmi);
        loadTileImage(lastIndex);
    }

    @FXML
    private void handlePutBack() throws RemoteException {
        isHoldingTile = false;
        clientRmi.server.putTileBack(clientRmi);
        tileDisplay.setImage(null);
        coveredTilesGrid.setDisable(false);
        reserveGrid.setDisable(false);
        spaceshipGrid.setDisable(true);
    }

    @FXML
    private void handleStore() throws RemoteException {
        isHoldingTile = false;
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
                final int part = lastSpaceship[row][col].getPart();

                imageView.setOnMouseClicked(event -> {
                    try {
                        clientRmi.server.selectShipPart(clientRmi, part);;
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
        cabinsGrid.setDisable(false);

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                final int index = row * 5 + col;
                if (index >= cabinAliens.getCabinAliens().size()) {
                    break;
                }

                ComponentTile tile = cabinAliens.getCabinAliens().get(index).getCabin();
                final int id = tile.getId();
                int rotation = tile.getRotation();
                CabinAliens c = cabinAliens.getCabinAliens().get(index);
                final boolean purple = c.isPurple();
                final boolean brown = c.isBrown();

                ImageView imageView = new ImageView();
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                imageView.setPickOnBounds(true);

                imageView.setOnMouseClicked(event -> {
                    try {
                        handleAddAlien((ImageView) event.getSource(), id, purple, brown);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });


                Blend blend = new Blend();
                blend.setMode(BlendMode.SRC_OVER);

                ColorInput colorOverlay;


                if (purple && brown) {
                     colorOverlay= new ColorInput(0, 0, 25, 25, Color.YELLOW);
                }
                else if (purple) {
                    colorOverlay= new ColorInput(0, 0, 25, 25, Color.PURPLE);
                }
                else if (brown) {
                    colorOverlay= new ColorInput(0, 0, 25, 25, Color.BROWN);
                }
                else {
                    colorOverlay= new ColorInput(0, 0, 25, 25, Color.RED);
                }

                blend.setTopInput(colorOverlay);
                imageView.setEffect(blend);

                Image img = getImageFromId(id);
                imageView.setRotate(rotation);
                imageView.setImage(img);

                cabinsGrid.add(imageView, col, row);
            }
        }
    }

    private void handleAddAlien(ImageView imageView, int index, boolean purple, boolean brown) throws RemoteException {
        if (purple && brown) {
            ButtonType purpleButton = new ButtonType("Purple", ButtonBar.ButtonData.OK_DONE);
            ButtonType brownButton = new ButtonType("Brown", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Choose a Color");
            alert.setHeaderText("Select a color:");
            alert.getButtonTypes().setAll(purpleButton, brownButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == purpleButton) {clientRmi.server.addAlienCabin(clientRmi, index, "p");}
            else clientRmi.server.addAlienCabin(clientRmi, index, "b");
        }
        else if (purple) clientRmi.server.addAlienCabin(clientRmi, index, "p");
        else if (brown) clientRmi.server.addAlienCabin(clientRmi, index, "b");
        imageView.setDisable(true);
        imageView.setVisible(false);
    }

    @FXML
    private void handleDoneChooseAliens() throws RemoteException {
        clientRmi.server.handleEndChooseAliens(clientRmi);
        sceneManager.switchTo("game");
    }

    public void requestDeck1() throws RemoteException {
        if(isHoldingTile)
            ShowTextUtils.showTextVolatileImmediate("Illegal action", "You can't watch decks while you are holding a tile");
        else{
            if(lookingDeck!=-1)
                clientRmi.server.endShowCards(clientRmi, lookingDeck);
            showDeckCards(1);
        }
    }


    public void requestDeck2() throws RemoteException {
        if(isHoldingTile)
            ShowTextUtils.showTextVolatileImmediate("Illegal action", "You can't watch decks while you are holding a tile");
        else{
            if(lookingDeck!=-1)
                clientRmi.server.endShowCards(clientRmi, lookingDeck);
            showDeckCards(2);
        }

    }

    public void requestDeck3() throws RemoteException {
        if(isHoldingTile)
            ShowTextUtils.showTextVolatileImmediate("Illegal action", "You can't watch decks while you are holding a tile");
        else{
            if(lookingDeck!=-1)
                clientRmi.server.endShowCards(clientRmi, lookingDeck);
            showDeckCards(3);
        }

    }

    private void showDeckCards(int nDeck) throws RemoteException {
        if(!clientRmi.server.showCardsbyDeck(clientRmi, nDeck))
            ShowTextUtils.showTextVolatileImmediate("Error","Another player is looking at this deck, please retry");
        else{
            lookingDeck = nDeck;
            System.out.println("showDeckCards");
            AdventureCardsData data = (AdventureCardsData) clientRmi.getData();
            ArrayList<Card> cards= data.getAdventureCards();
            for(int i = 0; i < cards.size(); i++){
                Card card = cards.get(i);
                System.out.println(card.getName() + card.getName());
                switch (i){
                    case 0->{
                        card1deck.setImage(cardsUtils.resolveCardImage(card.getName(), card.getLevel()));
                        card1deck.setVisible(true);
                    }

                    case 1->{
                        card2deck.setImage(cardsUtils.resolveCardImage(card.getName(), card.getLevel()));
                        card2deck.setVisible(true);
                    }

                    case 2->{
                        card3deck.setImage(cardsUtils.resolveCardImage(card.getName(), card.getLevel()));
                        card3deck.setVisible(true);
                    }

                    default-> {}
                }
            }
        }
    }

    public void assembly() {
        tileDisplay.setImage(null);

        putBackButton.setDisable(true);
        storeButton.setDisable(true);
        rotateButton.setDisable(true);

        coveredTilesGrid.setDisable(false);
        reserveGrid.setDisable(false);
        spaceshipGrid.setDisable(true);
    }

    public void pickedTile() {
        loadTileImage(lastIndex);

        putBackButton.setDisable(false);
        storeButton.setDisable(false);
        rotateButton.setDisable(false);

        coveredTilesGrid.setDisable(true);
        reserveGrid.setDisable(true);
        spaceshipGrid.setDisable(false);
    }

    public void pickedReservedCard() {
        loadTileImage(lastIndex);

        putBackButton.setDisable(true);
        storeButton.setDisable(false);
        rotateButton.setDisable(false);

        coveredTilesGrid.setDisable(true);
        reserveGrid.setDisable(true);
        spaceshipGrid.setDisable(false);
    }
}