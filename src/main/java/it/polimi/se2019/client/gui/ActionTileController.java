package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ActionTileController {

    private static final Logger logger = Logger.getLogger(ActionTileController.class.getName());

    private View view;
    private GridPane tileGrid;
    private GridPane shootTileGrid;
    private GridPane ammoGrid;
    private GridPane playerGrid;
    private Label infoText;
    private GridPane progressBar;
    private AnchorPane actionButtons;
    private Button cancelButton;
    private Button confirmButton;
    private List<GridPane> weaponCrateList;
    private GridPane myWeapons;
    private GridPane myPowerups;
    private FlowPane actionUnitPane;

    @FXML
    private Button mmm;
    @FXML
    private Button mg;
    @FXML
    private Button s;
    @FXML
    private Button r;
    @FXML
    private Button mrs;
    @FXML
    private Button mmmm;
    @FXML
    private Button mmg;
    @FXML
    private Button mmrs;
    @FXML
    private Button mmmg;

    public void initialize() {
        // do nothing
    }

    public void setView(View view) {
        this.view = view;
    }

    public void init() {
        ((GUIView)view).getGuiController().setActionTileController(this);
        initGrids();
        initInfo();
    }

    /**
     * Gets the references of grids for maps and cards.
     */
    public void initGrids() {
        BorderPane root = (BorderPane) ((GUIView)view).getPrimaryStage().getScene().getRoot();
        VBox vBox = (VBox) (root.getCenter()).lookup("#leftVBox");
        AnchorPane map = (AnchorPane) vBox.getChildren().get(0);

        tileGrid = (GridPane) map.lookup("#tileGrid");
        shootTileGrid = (GridPane) map.lookup("#shootTileGrid");
        ammoGrid = (GridPane) map.lookup("#ammoGrid");
        playerGrid = (GridPane) map.lookup("#playerGrid");

        weaponCrateList = new ArrayList<>();
        weaponCrateList.add((GridPane) map.lookup("#blueWeapons"));
        weaponCrateList.add((GridPane) map.lookup("#redWeapons"));
        weaponCrateList.add((GridPane) map.lookup("#yellowWeapons"));

        myWeapons = (GridPane) vBox.lookup("#myWeapons");
        myPowerups = (GridPane) vBox.lookup("#myPowerups");

    }

    /**
     * Gets the references for the info objects.
     *
     */
    public void initInfo() {
        BorderPane root = (BorderPane) ((GUIView)view).getPrimaryStage().getScene().getRoot();

        infoText = (Label) root.getCenter().lookup("#infoText");
        infoText.setText("Select an action(1)");

        progressBar = (GridPane) root.getCenter().lookup(Constants.PROGRESS_BAR);

        actionButtons = (AnchorPane) root.getCenter().lookup("#actionButtons");
        confirmButton = (Button) root.getCenter().lookup("#confirmButton");
        cancelButton = (Button) root.getCenter().lookup("#cancelButton");
        actionUnitPane = (FlowPane) root.getCenter().lookup("#actionUnitPane");


    }

    @FXML
    public void handleM() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        view.getInputRequested().add(this::getTile);
        view.askInput();
    }

    @FXML
    public void handleMG() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        view.getInputRequested().add(this::getTile);
        view.getInputRequested().add(this::getCard);
        view.askInput();
    }

    @FXML
    public void handleS() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        ((GUIView)view).getGuiController().getIntermediateInput().clear();
        view.getInputRequested().add(this::getShoot);
        view.askInput();
    }

    @FXML
    public void handleR() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        ((GUIView)view).getGuiController().getIntermediateInput().clear();
        view.getInputRequested().add(this::getReload);
        view.askInput();
    }

    @FXML
    public void handleMRS() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        view.getInputRequested().add(this::getTile);
        view.getInputRequested().add(this::getReload);
        view.getInputRequested().add(this::getShoot);
        view.askInput();
    }

    public void getTile(){

        Map<String, List<String>> intermediateInput = ((GUIView)view).getGuiController().getIntermediateInput();
        if (!intermediateInput.containsKey(Constants.KEY_ORDER)) {
            List<String> lst = new ArrayList<>();
            lst.add(Constants.MOVE);
            intermediateInput.put(Constants.KEY_ORDER, lst);
        } else {
            intermediateInput.get(Constants.KEY_ORDER).add(Constants.MOVE);
        }

        //disableActionButtons();
        tileGrid.toFront();
        tileGrid.setDisable(false);
        tileGrid.setVisible(true);

        infoText.setText("Select 1 tile ");
        cancelButton.setDisable(false);

        setUpProgressBar(1);
    }

    public void getShootTile(int amount) {

        shootTileGrid.toFront();
        shootTileGrid.setDisable(false);
        shootTileGrid.setVisible(true);

        infoText.setText("Select 1 tile ");
        cancelButton.setDisable(false);

        setUpProgressBar(1);

    }

    public void getCard() {

        Map<String, List<String>> intermediateInput = ((GUIView)view).getGuiController().getIntermediateInput();
        if (!intermediateInput.containsKey(Constants.KEY_ORDER)) {
            List<String> lst = new ArrayList<>();
            lst.add(Constants.GRAB);
            intermediateInput.put(Constants.KEY_ORDER, lst);
        } else {
            intermediateInput.get(Constants.KEY_ORDER).add(Constants.GRAB);
        }

        //disableActionButtons();

        infoText.setText("Select 1 card ");
        cancelButton.setDisable(false);
        showGrabbableCards();

        setUpProgressBar(1);
    }

    public void getShoot() {

        Map<String, List<String>> intermediateInput = ((GUIView)view).getGuiController().getIntermediateInput();
        if (!intermediateInput.containsKey(Constants.KEY_ORDER)) {
            List<String> lst = new ArrayList<>();
            lst.add(Constants.SHOOT);
            intermediateInput.put(Constants.KEY_ORDER, lst);
        } else {
            intermediateInput.get(Constants.KEY_ORDER).add(Constants.SHOOT);
        }

        //disableActionButtons();
        infoText.setText("Select 1 card ");
        cancelButton.setDisable(false);

        myWeapons.setDisable(false);
        myWeapons.getStyleClass().add("my-node");
        showMyLoadedWeapons();
        view.getInputRequested().add(this::getActionUnit);

        setUpProgressBar(1);
    }

    public void getActionUnit() {

        //disableActionButtons();
        infoText.setText("Select 1 action unit: ");
        cancelButton.setDisable(false);

        Player player = view.getModel().getGame().getPlayerByColor((view.getPlayerColor()));
        Weapon weapon = player.getCharacterState().getWeaponBag().stream()
                .filter(w -> w.getName().equals(view.getPlayerInput().get(Constants.SHOOT).get(0)))
                .findFirst().orElse(null);
        if (weapon != null) {
            actionUnitPane.setVisible(true);
            IntStream.range(0, weapon.getActionUnitList().size() + weapon.getOptionalEffectList().size())
                    .forEach(i -> {
                        Button b = (Button) actionUnitPane.getChildren().get(i);

                        b.setVisible(true);
                        if (i < weapon.getActionUnitList().size()) {
                            b.setText(weapon.getActionUnitList().get(i).getName());

                            setActionUnitButton(b, weapon.getActionUnitList(), i);

                        } else {
                            b.setText(weapon.getOptionalEffectList().get(i - weapon.getActionUnitList().size()).getName());

                            setActionUnitButton(b, weapon.getOptionalEffectList(), i);
                        }

                        System.out.println(b);
                    });
        }
    }

    public void setActionUnitButton(Button b, List<ActionUnit> actionUnitList, int i) {
        b.setOnAction(event -> {
            // adds the action unit in the input list
            ((GUIView)view).getGuiController().getIntermediateInput().get(Constants.SHOOT).add(b.getText());

            view.getInputRequested().add(() -> getTarget(actionUnitList.get(i).getNumPlayerTargets()));
            view.getInputRequested().add(() -> getShootTile(actionUnitList.get(i).getNumTileTargets()));

            view.askInput();
        });
    }

    public void getReload() {

        Map<String, List<String>> intermediateInput = ((GUIView)view).getGuiController().getIntermediateInput();
        if (!intermediateInput.containsKey(Constants.KEY_ORDER)) {
            List<String> lst = new ArrayList<>();
            lst.add(Constants.RELOAD);
            intermediateInput.put(Constants.KEY_ORDER, lst);
        } else {
            intermediateInput.get(Constants.KEY_ORDER).add(Constants.RELOAD);
        }

        //disableActionButtons();
        infoText.setText("Select 1 card ");
        cancelButton.setDisable(false);
        confirmButton.setDisable(false);

        myWeapons.setDisable(false);
        myWeapons.getStyleClass().add("my-node");
        showMyUnloadedWeapons();
        ((GUIView)view).getGuiController().getIntermediateInput().putIfAbsent(Constants.RELOAD, new ArrayList<>());

        setUpProgressBar(3);

    }

    public void getTarget(int amount) {
        System.out.println("target");
        //disableActionButtons();
        playerGrid.toFront();
        playerGrid.setDisable(false);
        playerGrid.setVisible(true);

        infoText.setText("Select 3 players ");
        cancelButton.setDisable(false);

        //TODO do not let player choose himself
        playerGrid.getChildren().stream()
                .map(n -> (VBox) n)
                .forEach(vBox -> vBox.getChildren().stream()
                        .map(n -> (HBox) n)
                        .filter(hbox -> !hbox.getChildren().isEmpty())
                        .forEach(hBox -> hBox.getChildren().stream()
                                .map(n -> (Circle) n)
                                .filter(Node::isVisible)
                                .forEach(c -> {
                                    if (c.getFill() != Paint.valueOf(view.getPlayerColor().getColor())){
                                        c.setDisable(false);
                                        c.getStyleClass().add("my-shape");
                                    }
                                    else {
                                        c.setDisable(true);
                                    }
                                })
                        )
                );

        setUpProgressBar(amount);
    }

    /**
     * Deactivates the action buttons.
     *
     */
    public void disableActionButtons() {
        if (view.getModel().getGame().isFrenzy()) {
            mrs.setDisable(true);
            mmmm.setDisable(true);
            mmg.setDisable(true);
            mmrs.setDisable(true);
            mmmg.setDisable(true);
        }
        else {
            mmm.setDisable(true);
            mg.setDisable(true);
            s.setDisable(true);
            r.setDisable(true);
        }
    }

    /**
     * Prepares the number of circles indicating the max number of selections needed.
     * @param numOfTargets is the number of selections.
     */
    public void setUpProgressBar(int numOfTargets) {

        IntStream.range(0, numOfTargets)
                .forEach(i -> progressBar.getChildren().get(i).setVisible(true));
    }

    /**
     * Shows the objects that are grabbable from the player's position in the map.
     *
     */
    public void showGrabbableCards() {
        Tile t = null;
        if (view.getPlayerInput().isEmpty()){
            t  = view.getModel().getGame().getCurrentPlayer().getCharacterState().getTile();
        }
        else {
            int[] coords = Util.convertToCoords(Integer.parseInt(view.getPlayerInput().get(Constants.MOVE).get(0)));
            t = view.getModel().getGame().getBoard().getTile(coords[0], coords[1]);
        }

        System.out.println("Grab: "+t);
        try {
            int[] coords = view.getModel().getGame().getBoard().getTilePosition(t);
            if (t.isSpawnTile()) {
                System.out.println("spawn tile");
                String roomColor = t.getRoomColor().getColor();
                Optional<GridPane> optGrid = weaponCrateList.stream()
                        .filter(wc -> wc.getId().split("Weapons")[0].equalsIgnoreCase(roomColor))
                        .findFirst();
                if (optGrid.isPresent()){
                    optGrid.get().setDisable(false);
                    optGrid.get().getStyleClass().add("my-node");
                }
            }
            else {
                System.out.println("normal tile");
                ammoGrid.toFront();
                ammoGrid.setDisable(false);
                ammoGrid.setVisible(true);
                HBox hBox = (HBox) ammoGrid.getChildren().get(Util.convertToIndex(coords[0], coords[1]));
                Node n = hBox.getChildren().get(0);
                ((AnchorPane) n).getChildren().get(0).setDisable(false);

                n.getStyleClass().add("my-node");
            }
        } catch (TileNotFoundException e) {
            logger.warning(e.toString());
        }
    }

    /**
     * Shows the unloaded weapons for selection, and hides the loaded ones.
     *
     */
    public void showMyUnloadedWeapons() {
        CharacterState myCharacterState =  view.getModel().getGame().getPlayerList().stream()
                .filter(p -> p.getColor() == view.getPlayerColor())
                .collect(Collectors.toList()).get(0).getCharacterState();
        List<Weapon> myWeaponsModel = myCharacterState.getWeaponBag();

        IntStream.range(0, myWeaponsModel.size())
                .forEach(i -> {
                    ImageView iv = null;
                    iv = (ImageView) myWeapons.getChildren().get(i);
                    if (!myWeaponsModel.get(i).isLoaded()) {
                        iv.setOpacity(1.0);
                        iv.setDisable(false);
                        iv.setVisible(true);
                    }
                    else {
                        iv.setVisible(false);
                    }

                    ((GUIView)view).getGuiController().setCardSelectionBehavior(iv, myWeapons, Constants.RELOAD);
                });
    }

    /**
     * Shows the loaded weapons for selection, and hides the unloaded ones.
     *
     */
    public void showMyLoadedWeapons() {
        CharacterState myCharacterState =  view.getModel().getGame().getPlayerList().stream()
                .filter(p -> p.getColor() == view.getPlayerColor())
                .collect(Collectors.toList()).get(0).getCharacterState();
        List<Weapon> myWeaponsModel = myCharacterState.getWeaponBag();


        IntStream.range(0, myWeaponsModel.size())
                .forEach(i -> {
                    ImageView iv = null;
                    iv = (ImageView) myWeapons.getChildren().get(i);
                    if (!myWeaponsModel.get(i).isLoaded()) {
                        iv.setVisible(false);
                    }
                    else {
                        iv.setVisible(true);
                        iv.setOpacity(1.0);
                        iv.setDisable(false);
                    }

                    ((GUIView)view).getGuiController().setCardSelectionBehavior(iv, myWeapons, Constants.SHOOT);
                });
    }



}
