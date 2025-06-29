package it.polimi.ingsw.gui.pageControllers;

import it.polimi.ingsw.Rmi.ClientRmi;
import it.polimi.ingsw.gui.Controller;
import it.polimi.ingsw.gui.ShowTextUtils;
import it.polimi.ingsw.model.bank.GoodsBlock;
import it.polimi.ingsw.model.resources.GoodsContainer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CargoManagementController extends Controller {
    @FXML private ImageView goodsOrder;
    @FXML private Button addGoodButton;
    @FXML private Button swapGoodsButton;
    @FXML private Button removeGoodButton;
    @FXML private Button endButton;
    @FXML private ImageView background;
    @FXML private AnchorPane rewardCargoPane;
    @FXML private ImageView goodReward0;
    @FXML private ImageView goodReward1;
    @FXML private ImageView goodReward2;
    @FXML private ImageView goodReward3;
    @FXML private ImageView goodReward4;
    @FXML private Button rewardButton;
    @FXML private AnchorPane cargo0Pane;
    @FXML private Button cargo0Button;
    @FXML private Button cargo1Button;
    @FXML private Button cargo2Button;
    @FXML private Button cargo3Button;
    @FXML private Button cargo4Button;
    @FXML private Button cargo5Button;
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

    private int rewardIndex;
    private ImageView firstSwapGood;
    private ImageView selectedRewardGood;
    private ImageView pendingSecondGood;
    private ImageView removeTargetGood;
    private boolean isAddOperation = false;


    private static final Image ORDER_IMAGE = new Image(
            Objects.requireNonNull(
                    CargoManagementController.class.getResource("/goodsBlocks/order.png")
            ).toExternalForm(),
            true
    );


    @FXML
    private void initialize() {
        cargoPanes = List.of(
                cargo1Pane, cargo2Pane, cargo3Pane,
                cargo4Pane, cargo5Pane, cargo6Pane
        );
    }

    private enum State {
        IDLE,
        SELECT_REWARD_GOOD,
        SELECT_DESTINATION,
        SWAP_FIRST_SELECTED,
        CONFIRM_PENDING,
        REMOVE_SELECTED


    }
    private State state = State.IDLE;


    public void handleOnGoodClicked(MouseEvent event) {
        ImageView clickedGood = (ImageView) event.getSource();
        onGoodClicked(clickedGood);
    }

    public void setCargos(ArrayList<GoodsContainer> cargos) {
        for (int i = 0; i < cargos.size(); i++) {
            GoodsContainer container = cargos.get(i);
            GoodsBlock[] blocks = container.getGoods();

            AnchorPane cargoPane;
            if (i == 0) {
                cargoPane = rewardCargoPane;
            } else {
                cargoPane = cargoPanes.get(i);
                cargoPane.setId(String.valueOf(i));
            }

            List<ImageView> slots = cargoPane.getChildren()
                    .stream()
                    .filter(n -> n instanceof ImageView)
                    .map(n -> (ImageView) n)
                    .toList();

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

        goodsOrder.setImage(ORDER_IMAGE);
        goodsOrder.preserveRatioProperty();
        goodsOrder.setVisible(true);
    }

    private Image getImageByValue(int value) {
        return switch (value) {
            case 1 -> new Image(Objects.requireNonNull(getClass().getResource("/goodsBlocks/blue.png")).toExternalForm());
            case 2 -> new Image(Objects.requireNonNull(getClass().getResource("/goodsBlocks/green.png")).toExternalForm());
            case 3 -> new Image(Objects.requireNonNull(getClass().getResource("/goodsBlocks/yellow.png")).toExternalForm());
            case 4 -> new Image(Objects.requireNonNull(getClass().getResource("/goodsBlocks/red.png")).toExternalForm());
            default -> new Image(Objects.requireNonNull(getClass().getResource("/goodsBlocks/default.png")).toExternalForm());
        };
    }


    public void onAddGoodButtonClicked() {
        state = State.SELECT_REWARD_GOOD;
        isAddOperation = true;
        updateButtonStates();
        System.out.println("[STATE] Awaiting reward good selection");
    }

    public void onSwapGoodsButtonClicked() {
        state = State.SWAP_FIRST_SELECTED;
        isAddOperation = false;
        updateButtonStates();
        System.out.println("[STATE] Awaiting first good for swap");
    }

    public void onRemoveGoodButtonClicked() {
        state = State.REMOVE_SELECTED;
        isAddOperation = false;
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
            case SELECT_REWARD_GOOD -> {
                if (parent != null && "rewardCargoPane".equals(parent.getId())) {
                    rewardIndex = Integer.parseInt(goodId);
                    selectedRewardGood = clickedGood;
                    selectedRewardGood.setOpacity(0.5);
                    state = State.SELECT_DESTINATION;
                    System.out.println("[STATE] Reward good " + rewardIndex + " selected, awaiting destination");
                } else {
                    System.out.println("[ERROR] You must select a reward good from the rewardCargoPane");
                }
            }
            case SELECT_DESTINATION -> {
                if (parent != null && !"rewardCargoPane".equals(parent.getId())) {
                    clickedGood.setOpacity(0.5);
                    pendingSecondGood = clickedGood;
                    state = State.CONFIRM_PENDING;
                    updateButtonStates();
                    System.out.println("[STATE] Destination selected, confirm to add");
                } else {
                    System.out.println("[ERROR] You must select a destination inside a cargo pane");
                }
            }
            case SWAP_FIRST_SELECTED -> {
                firstSwapGood = clickedGood;
                firstSwapGood.setOpacity(0.5);
                state = State.CONFIRM_PENDING;
                updateButtonStates();
                System.out.println("[STATE] First good selected for swap, confirm with second");
            }
            case CONFIRM_PENDING -> {
                if (!isAddOperation && firstSwapGood != null && firstSwapGood != clickedGood) {
                    pendingSecondGood = clickedGood;
                    pendingSecondGood.setOpacity(0.5);
                    System.out.println("[STATE] Second good selected, ready to confirm swap");
                } else {
                    System.out.println("[ERROR] Action already selected, confirm or cancel");
                }
            }
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

        if (isAddOperation && selectedRewardGood != null && pendingSecondGood != null) {
            AnchorPane parent = (AnchorPane) pendingSecondGood.getParent();
            int cargoPaneIndex = Integer.parseInt(parent.getId());
            int goodIndex = Integer.parseInt(pendingSecondGood.getId());
            try {
                clientRmi.server.addGood(clientRmi ,cargoPaneIndex, goodIndex, rewardIndex);
            } catch (Exception e) {
                ShowTextUtils.showTextVolatileImmediate("Error", e.getMessage());
            }
            System.out.println("[INFO] Added reward good " + rewardIndex + " to cargo pane " + cargoPaneIndex + ", slot " + goodIndex);
        } else if (!isAddOperation && firstSwapGood != null && pendingSecondGood != null) {
            swapGoods(firstSwapGood, pendingSecondGood);
            System.out.println("[INFO] Swapped goods successfully");
        } else if (!isAddOperation && removeTargetGood != null) {
            AnchorPane parent = (AnchorPane) removeTargetGood.getParent();
            int cargoPaneIndex = Integer.parseInt(parent.getId());
            int goodIndex = Integer.parseInt(removeTargetGood.getId());
            try {
                clientRmi.server.removeGood(clientRmi ,cargoPaneIndex, goodIndex);
            } catch (Exception e) {
                ShowTextUtils.showTextVolatileImmediate("Error", e.getMessage());
            }
            System.out.println("[INFO] Removed good from cargo pane " + cargoPaneIndex + ", slot " + goodIndex);
        } else {
            System.out.println("[ERROR] Incomplete selection for confirmation");
        }

        resetState();
    }

    public void onCancelClicked() {
        System.out.println("[STATE] Action cancelled by user");
        resetState();
    }

    private void swapGoods(ImageView good1, ImageView good2) {
        String id1 = good1.getId();
        String id2 = good2.getId();
        AnchorPane pane1 = (AnchorPane) good1.getParent();
        AnchorPane pane2 = (AnchorPane) good2.getParent();
        System.out.println("[DEBUG] Swapping good " + id1 + " in pane " + pane1.getId() + " with good " + id2 + " in pane " + pane2.getId());
        try {
            clientRmi.server.swapGoods(clientRmi , Integer.parseInt(pane1.getId()), Integer.parseInt(pane2.getId()), Integer.parseInt(id1) , Integer.parseInt(id2));
        } catch (Exception e) {
            ShowTextUtils.showTextVolatileImmediate("Error", e.getMessage());
        }
    }

    private void resetState() {
        state = State.IDLE;
        isAddOperation = false;
        if (selectedRewardGood != null) {
            selectedRewardGood.setOpacity(1.0);
            selectedRewardGood = null;
        }
        if (firstSwapGood != null) {
            firstSwapGood.setOpacity(1.0);
            firstSwapGood = null;
        }
        if (pendingSecondGood != null) {
            pendingSecondGood.setOpacity(1.0);
            pendingSecondGood = null;
        }
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

        addGoodButton.setDisable(confirmVisible);
        swapGoodsButton.setDisable(confirmVisible);
        removeGoodButton.setDisable(confirmVisible);
    }




    public void handleNext(ActionEvent actionEvent) {
    }

    public void endCargoManagement(ActionEvent actionEvent) {
        try {
            clientRmi.server.endCargoManagement(clientRmi);

        } catch (Exception e) {
            ShowTextUtils.showTextVolatileImmediate("Error", e.getMessage());
        }
        disableAllButtons();
    }

    private void disableAllButtons() {
        endButton.setVisible(false);
        endButton.setDisable(true);
        addGoodButton.setVisible(false);
        addGoodButton.setDisable(true);
        swapGoodsButton.setVisible(false);
        swapGoodsButton.setDisable(true);
        removeGoodButton.setVisible(false);
        removeGoodButton.setDisable(true);
    }
}
