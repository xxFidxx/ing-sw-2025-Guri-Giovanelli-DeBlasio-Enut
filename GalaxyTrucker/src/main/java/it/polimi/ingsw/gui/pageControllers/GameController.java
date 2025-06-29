    package it.polimi.ingsw.gui.pageControllers;

    import it.polimi.ingsw.controller.ControllerExceptions;
    import it.polimi.ingsw.controller.network.data.DoubleEngineNumber;
    import it.polimi.ingsw.controller.network.data.TileData;
    import it.polimi.ingsw.gui.Controller;
    import it.polimi.ingsw.gui.ShowTextUtils;
    import it.polimi.ingsw.model.componentTiles.Direction;
    import javafx.application.Platform;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.scene.Node;
    import javafx.scene.control.*;
    import javafx.scene.effect.ColorAdjust;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.AnchorPane;

    import java.io.File;
    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.file.Files;
    import java.nio.file.StandardCopyOption;
    import java.rmi.RemoteException;
    import java.util.Arrays;
    import java.util.HashMap;
    import java.util.Objects;
    import java.util.Optional;

    import it.polimi.ingsw.gui.CardsUtils;
    import javafx.scene.layout.GridPane;

    public class GameController extends Controller {


        @FXML private AnchorPane rootPane;
        @FXML private ChoiceBox planetsChoice;
        @FXML private Button confirmPlanetButton;
        @FXML private ImageView background;
        @FXML private ImageView cardPlaceHolder;
        @FXML private TextArea playerColorArea;
        @FXML private ImageView pos_0;
        @FXML private ImageView pos_1;
        @FXML private ImageView pos_2;
        @FXML private ImageView pos_3;
        @FXML private ImageView pos_4;
        @FXML private ImageView pos_5;
        @FXML private ImageView pos_6;
        @FXML private ImageView pos_7;
        @FXML private ImageView pos_8;
        @FXML private ImageView pos_9;
        @FXML private ImageView pos_10;
        @FXML private ImageView pos_11;
        @FXML private ImageView pos_12;
        @FXML private ImageView pos_13;
        @FXML private ImageView pos_14;
        @FXML private ImageView pos_15;
        @FXML private ImageView pos_16;
        @FXML private ImageView pos_17;
        @FXML private ImageView pos_18;
        @FXML private ImageView pos_19;
        @FXML private ImageView pos_20;
        @FXML private ImageView pos_21;
        @FXML private ImageView pos_22;
        @FXML private ImageView pos_23;
        @FXML private GridPane spaceshipGrid;
        @FXML private GridPane reserveGrid;
        @FXML private ImageView spaceshipDisplay;

        @FXML private AnchorPane boardPane;

        @FXML private Label spaceshipStateLabel;

        private ImageView[] tileViews;

        private final CardsUtils utils = new CardsUtils();

        private TileData[][] lastSpaceship;
        private Direction lastDir;
        private int lastPos;

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

        @FXML
        private void initialize() {
            tileViews = new ImageView[] {
                    pos_0, pos_1, pos_2, pos_3, pos_4, pos_5,
                    pos_6, pos_7, pos_8, pos_9, pos_10, pos_11,
                    pos_12, pos_13, pos_14, pos_15, pos_16, pos_17,
                    pos_18, pos_19, pos_20, pos_21, pos_22, pos_23
            };

            cardPlaceHolder.setImage(new Image(Objects.requireNonNull(getClass().getResource("/cardsCover/Cover1.jpg")).toExternalForm()));

            playerColorArea.setEditable(false);

            background.setImage(new Image(Objects.requireNonNull(getClass().getResource("/backgrounds/game.png")).toExternalForm()));

            confirmPlanetButton.setVisible(false);
            planetsChoice.setVisible(true);

            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 7; col++) {
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(80);
                    imageView.setFitHeight(80);
                    imageView.setPreserveRatio(true);
                    imageView.setPickOnBounds(true);

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

                reserveGrid.add(imageView, col, 0);
            }

        }

        public void setLastSpaceship(TileData[][] tileIds) {
            this.lastSpaceship = tileIds;
            Platform.runLater(() -> {
                updateSpaceship();
            });
        }

        public void setLastProjectile(Direction dir, int pos) {
            this.lastDir = dir;
            this.lastPos = pos;
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

        private Image getImageFromId(int id) {
            if (id < 0) return null;

            String imagePath = "/tiles/tile" + id + ".jpg";

            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            Image image = new Image(imageStream);

            return image;
        }

        @FXML
        private void handleNext() {
            sceneManager.switchTo("end");
        }

        public void updateBoard(int[] infos) {

            System.out.println("Chiamato updateBoard");
            System.out.println(Arrays
                    .toString(infos));


            for (ImageView iv : tileViews) {
                iv.setOpacity(0.0);
            }


            for (int i = 0; i < infos.length; i += 2) {
                int color = infos[i];
                int pos = infos[i + 1];

                if (pos >= 0 && pos < tileViews.length) {
                    ImageView tile = tileViews[pos];
                    tile.setImage(getImageForColor(color));
                    tile.setOpacity(1.0);
                }
            }
        }

        public void setPlayerColorArea(HashMap<String,Integer> playerColor){
            playerColorArea.clear();
            for(String s: playerColor.keySet()){
                String color = switch (playerColor.get(s)) {
                    case 1 -> "blue";
                    case 2 -> "green";
                    case 3 -> "yellow";
                    case 4 -> "red";
                    default -> "default";
                };
                playerColorArea.appendText(s + ": " + color + "\n");
            }
        }

        public void showPlanetsChoice(int n) {
            System.out.println("Show planets choice");
            planetsChoice.getItems().clear();

            for (int i = 1; i <= n; i++) {
                planetsChoice.getItems().add("Planet " + i);
            }

            planetsChoice.getSelectionModel().selectFirst();
            planetsChoice.setVisible(true);
            planetsChoice.setDisable(false);
            confirmPlanetButton.setDisable(false);
            confirmPlanetButton.setVisible(true);
        }

        public void setCard(String name, Integer level){
            spaceshipGrid.setDisable(true);
            spaceshipStateLabel.setText("your spaceship");

            Image image = utils.resolveCardImage(name,level);
            cardPlaceHolder.setImage(image);
        }

        public void onLoad() {
            System.out.println("Chiamato updateBoard");
            int[] boardInfo = null;
            try {
                boardInfo = clientRmi.server.guiBoardInfo();
            } catch (Exception e) {
                ShowTextUtils.showTextVolatileImmediate("Error", e.getMessage());
            }
            System.out.println(Arrays.toString(boardInfo));
            updateBoard(boardInfo);
        }

        private Image getImageForColor(int color) {
            String path = switch (color) {
                case 1 -> "/placeholders/blue.png";
                case 2 -> "/placeholders/green.png";
                case 3 -> "/placeholders/yellow.png";
                case 4 -> "/placeholders/red.png";
                default -> "/placeholders/default.png";
            };
            return new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
        }


        @FXML
        private void onConfirmPlanetClicked() {
            int selectedIndex = planetsChoice.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                try {
                    clientRmi.server.choosePlanets(clientRmi ,selectedIndex);
                    confirmPlanetButton.setDisable(true);
                    planetsChoice.setDisable(true);
                } catch (Exception e) {
                    ShowTextUtils.showTextVolatileImmediate("Error", e.getMessage());
                }
            } else {
                ShowTextUtils.showTextVolatileImmediate("Error", "No planet selected.");
            }
        }

        public void askCannon() throws RemoteException {
            Optional<ButtonType> result = ShowTextUtils.askYesNoImmediate(
                    "USE CANNON",
                    "Do you want to charge a cannon to protect yourself?\nProjectile\ndirection: " + lastDir.toString() + "\nposition: " + lastPos
            );

            if (result.get() == ButtonType.YES) clientRmi.server.playerProtected(clientRmi);
            else clientRmi.server.playerHit(clientRmi);
        }

        public void askShield() throws RemoteException {
            Optional<ButtonType> result = ShowTextUtils.askYesNoImmediate(
                    "USE SHIELD",
                    "Do you want to charge a shield to protect yourself?\nProjectile\ndirection: " + lastDir.toString() + "\nposition: " + lastPos
            );

            if (result.get() == ButtonType.YES) clientRmi.server.playerProtected(clientRmi);
            else clientRmi.server.playerHit(clientRmi);
        }

        public void chooseEngine(DoubleEngineNumber data) {
            while (true) {
                Optional<Integer> res = ShowTextUtils.askNumberImmediate(
                        "Insert a number",
                        "Insert the number of double engines you want to charge"
                );

                if (res.isEmpty()) {
                    ShowTextUtils.showTextVolatileImmediate("Error", "Please insert a valid number.");
                    continue;
                }

                int number = res.get();

                try {
                    clientRmi.server.chargeEngines(clientRmi, number);
                    break;
                } catch (RemoteException e) {
                    ShowTextUtils.showTextVolatileImmediate("Remote Error", e.getMessage());
                }catch (ControllerExceptions e){
                    ShowTextUtils.showTextVolatileImmediate("Error", e.getMessage());
                }
            }
        }


        public void selectShip(TileData[][] tileIds) {
            this.lastSpaceship = tileIds;
            updateSpaceship();

            spaceshipStateLabel.setText("select spaceship part");

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
    }
