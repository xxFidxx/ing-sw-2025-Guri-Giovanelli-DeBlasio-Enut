    package it.polimi.ingsw.gui;

    import javafx.fxml.FXML;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.AnchorPane;

    import java.rmi.RemoteException;
    import java.util.Arrays;
    import java.util.Objects;

    public class GameController extends Controller {


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


        @FXML private AnchorPane boardPane;

        private ImageView[] tileViews;

        @FXML
        private void initialize() {
            tileViews = new ImageView[] {
                    pos_0, pos_1, pos_2, pos_3, pos_4, pos_5,
                    pos_6, pos_7, pos_8, pos_9, pos_10, pos_11,
                    pos_12, pos_13, pos_14, pos_15, pos_16, pos_17,
                    pos_18, pos_19, pos_20, pos_21, pos_22, pos_23
            };
        }

        @FXML
        private void handleNext() {
            sceneManager.switchTo("end");
        }

        public void updateBoard(int[] infos) {

            System.out.println("Chiamato updateBoard");

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

        public void onLoad() throws RemoteException {
            System.out.println("Chiamato updateBoard");
            int[] boardInfo = clientRmi.server.guiBoardInfo();
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


    }
