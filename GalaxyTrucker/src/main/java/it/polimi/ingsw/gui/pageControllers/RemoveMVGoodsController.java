package it.polimi.ingsw.gui.pageControllers;

import it.polimi.ingsw.Rmi.ClientRmi;
import it.polimi.ingsw.controller.network.data.RemoveMostValuable;
import it.polimi.ingsw.gui.Controller;
import it.polimi.ingsw.gui.ShowTextUtils;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.resources.GoodsContainer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.*;

public class RemoveMVGoodsController extends Controller {

    int nGoods;
    int nBatteries;


    @FXML private AnchorPane rootPane;
    @FXML private  ImageView goodsOrder;
    @FXML private TextArea textBox;
    @FXML private Button removeGoodButton;
    @FXML private Button endButton;
    @FXML private ImageView background;
    @FXML private AnchorPane cargo1Pane;
    @FXML private ImageView cargo0Good0;
    @FXML private ImageView cargo0Reward1;
    @FXML private ImageView cargo0Good2;
    @FXML private ImageView cargo0Good3;
    @FXML private ImageView cargo0Good4;
    @FXML private AnchorPane cargo2Pane;
    @FXML private ImageView cargo1Good0;
    @FXML private ImageView cargo1Good1;
    @FXML private ImageView cargo1Good2;
    @FXML private ImageView cargo1Good3;
    @FXML private ImageView cargo1Good4;
    @FXML private AnchorPane cargo3Pane;
    @FXML private ImageView cargo2Good0;
    @FXML private ImageView cargo2Good1;
    @FXML private ImageView cargo2Good2;
    @FXML private ImageView cargo2Good3;
    @FXML private ImageView cargo2Good4;
    @FXML private AnchorPane cargo4Pane;
    @FXML private ImageView cargo3Good0;
    @FXML private ImageView cargo3Good1;
    @FXML private ImageView cargo3Good2;
    @FXML private ImageView cargo3Good3;
    @FXML private ImageView cargo3Good4;
    @FXML private AnchorPane cargo5Pane;
    @FXML private ImageView cargo4Good0;
    @FXML private ImageView cargo4Good1;
    @FXML private ImageView cargo4Good2;
    @FXML private ImageView cargo4Good3;
    @FXML private ImageView cargo4Good4;
    @FXML private AnchorPane cargo6Pane;
    @FXML private ImageView cargo5Good0;
    @FXML private ImageView cargo5Good1;
    @FXML private ImageView cargo5Good2;
    @FXML private ImageView cargo5Good3;
    @FXML private ImageView cargo5Good4;



    @FXML private Button confirmButton;
    @FXML private Button cancelButton;


    @FXML private List<AnchorPane> cargoPanes;


    private ImageView removeTargetGood;

    private static final Image ORDER_IMAGE = new Image(
            Objects.requireNonNull(
                    CargoManagementController.class.getResource("/goodsBlocks/order.png")
            ).toExternalForm(),
            true
    );

    private final Map<Integer, Image> goodsImageCache = new HashMap<>();


    @FXML
    private void initialize() {
        cargoPanes = List.of(
                cargo1Pane, cargo2Pane, cargo3Pane,
                cargo4Pane, cargo5Pane, cargo6Pane
        );

        goodsOrder.setImage(ORDER_IMAGE);
        goodsOrder.preserveRatioProperty();
        goodsOrder.setVisible(true);
    }

    public void onCancelClicked(ActionEvent actionEvent) {
        System.out.println("[STATE] Action cancelled by user");
        resetState();
    }

    private enum State {
        IDLE,
        CONFIRM_PENDING,
        REMOVE_SELECTED
    }
    private State state = State.IDLE;


    public void handleOnGoodClicked(MouseEvent event) {
        ImageView clickedGood = (ImageView) event.getSource();
        onGoodClicked(clickedGood);
    }

    public void startRemoveMVGoods(RemoveMostValuable data){
        rootPane.setDisable(false);
        nGoods = data.getNGoods();
        nBatteries = data.getBatteriesToRemove();
        textBox.setText("You have to remove " + nGoods + " goods.\nRemove your most valuables one");
        ArrayList<GoodsContainer> cargos = data.getCargos();
        setCargos(cargos);
        enableButtons();
    }

    public void setCargos(ArrayList<GoodsContainer> cargos) {
        for (int i = 0; i < cargos.size(); i++) {
            GoodsContainer container = cargos.get(i);
            GoodsBlock[] blocks = container.getGoods();

            AnchorPane cargoPane;
                cargoPane = cargoPanes.get(i);
                cargoPane.setId(String.valueOf(i));


            List<ImageView> slots = cargoPane.getChildren().stream().filter(n -> n instanceof ImageView).map(n -> (ImageView) n).toList();

            for (int j = 0; j < blocks.length && j < slots.size(); j++) {
                ImageView slot = slots.get(j);
                slot.setId(String.valueOf(j));
                GoodsBlock block = blocks[j];

                if (block != null) {
                    int value = block.getValue();
                    slot.setImage(getImageByValue(value));
                } else {
                    slot.setImage(getImageByValue(-1));
                }
            }

            String style = container.isSpecial() ? "-fx-background-color: #ffcccc" : "-fx-background-color: lightgrey";
            cargoPane.setStyle(style);
        }
    }

    private Image getImageByValue(int value) {
        if (goodsImageCache.containsKey(value)) return goodsImageCache.get(value);

        String filename = switch (value) {
            case 1 -> "/goodsBlocks/blue.png";
            case 2 -> "/goodsBlocks/green.png";
            case 3 -> "/goodsBlocks/yellow.png";
            case 4 -> "/goodsBlocks/red.png";
            default -> "/goodsBlocks/default.png";
        };

        Image img = loadImageTmp(filename);
        if (img != null) goodsImageCache.put(value, img);
        return img;
    }

    private Image loadImageTmp(String pathInResources) {
        try (InputStream in = getClass().getResourceAsStream(pathInResources)) {
            if (in == null) return null;

            File tempFile = File.createTempFile("img_", ".tmp");
            tempFile.deleteOnExit();
            Files.copy(in, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return new Image(tempFile.toURI().toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public void onRemoveGoodButtonClicked() {
        state = State.REMOVE_SELECTED;
        updateButtonStates();
        System.out.println("[STATE] Select good to remove from cargo (reward goods cannot be removed)");
    }

    public void onGoodClicked(ImageView clickedGood) {
        String goodId = clickedGood.getId();
        if (goodId == null) {
            System.out.println("[ERROR] Clicked good has no ID");
            return;
        }

        AnchorPane parent = (AnchorPane) clickedGood.getParent();

        switch (state) {
            case REMOVE_SELECTED -> {
                if (parent != null && !"rewardCargoPane".equals(parent.getId())) {
                    clickedGood.setOpacity(0.5);
                    removeTargetGood = clickedGood;
                    state = State.CONFIRM_PENDING;
                    updateButtonStates();
                    System.out.println("[STATE] Good selected for removal, confirm to proceed");
                } else {
                    System.out.println("[ERROR] Cannot remove reward goods");
                }
            }
            case IDLE -> {
                System.out.println("[ERROR] Click a command first (Add/Swap/Remove)");
            }
        }
    }

    public void onConfirmClicked() {
        if (state != State.CONFIRM_PENDING) {
            System.out.println("[ERROR] Nothing to confirm");
            return;
        }

         if (removeTargetGood != null) {
            AnchorPane parent = (AnchorPane) removeTargetGood.getParent();
            int cargoPaneIndex = Integer.parseInt(parent.getId());
            int goodIndex = Integer.parseInt(removeTargetGood.getId());
            try {
                if(clientRmi.server.removeMVGood(clientRmi ,cargoPaneIndex, goodIndex)){
                    removeTargetGood.setVisible(false);
                    removeTargetGood.setMouseTransparent(true);
                    removeTargetGood.setDisable(true);
                    nGoods--;
                }else
                    ShowTextUtils.showTextVolatileImmediate("Illegal action", "You didn't choose among your most valuables goods");

            } catch (Exception e) {
                ShowTextUtils.showTextVolatileImmediate("Error", e.getMessage());
            }
            System.out.println("[INFO] Removed good from cargo pane " + cargoPaneIndex + ", slot " + goodIndex);
        } else {
            System.out.println("[ERROR] Incomplete selection for confirmation");
        }

        resetState();

         if(nGoods== 0){
             try {
                 clientRmi.server.fromMvGoodstoBatteries(clientRmi,nBatteries);
             } catch (RemoteException e) {
                 ShowTextUtils.showTextVolatileImmediate("Error", e.getMessage());
             }
         }
    }

    private void resetState() {
        state = State.IDLE;
        if (removeTargetGood != null) {
            removeTargetGood.setOpacity(1.0);
            removeTargetGood = null;
        }
        updateButtonStates();
        System.out.println("[STATE] Reset to IDLE");
    }

    private void updateButtonStates() {
        boolean confirmVisible = (state == State.CONFIRM_PENDING);
        confirmButton.setVisible(confirmVisible);
        cancelButton.setVisible(confirmVisible);
    }




    public void handleNext(ActionEvent actionEvent) {
    }

    public void endCargoManagement(ActionEvent actionEvent) {
        try {
            clientRmi.server.endCargoManagement(clientRmi);

        } catch (Exception e) {
            ShowTextUtils.showTextVolatileImmediate("Error", e.getMessage());
        }
        rootPane.setDisable(true);
        //disableAllButtons();
    }

    private void disableAllButtons() {
        endButton.setVisible(false);
        endButton.setDisable(true);
        removeGoodButton.setVisible(false);
        removeGoodButton.setDisable(true);
    }

    public void enableButtons() {
        endButton.setVisible(true);
        endButton.setDisable(false);
        removeGoodButton.setVisible(true);
        removeGoodButton.setDisable(false);
        confirmButton.setVisible(false);
        cancelButton.setVisible(false);
        confirmButton.setDisable(false);
        cancelButton.setDisable(false);

        resetState();
        System.out.println("[STATE] Remove Most Valuable Goods phase started");
    }
}
