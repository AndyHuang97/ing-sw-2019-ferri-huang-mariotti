package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.NamedImage;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.polimi.se2019.client.util.Constants.*;

public class GUIController {

    private static final Logger logger = Logger.getLogger(GUIController.class.getName());
    private static final int ACTIONUNIT_POSITION = 0;

    private View view;
    private Map<PlayerColor, PlayerBoardController> playerBoardControllerMap;
    private List<PlayerBoardController> pbControllerList;
    private MapController mapController;
    private Map<String, List<String>> intermediateInput = new HashMap<>();
    private boolean initialized;

    private GridPane progressBar;

    @FXML
    private VBox leftVBox;
    @FXML
    private AnchorPane map;
    @FXML
    private AnchorPane playerBoard;
    @FXML
    private VBox rightVBox;
    @FXML
    private HBox infoPane;
    @FXML
    private Label infoText;
    @FXML
    private Label myName;
    @FXML
    private GridPane myAmmo;
    @FXML
    private Label myScore;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private GridPane myWeapons;
    @FXML
    private GridPane myPowerUps;
    @FXML
    private AnchorPane actionButtons;
    @FXML
    private GridPane rankingGrid;
    @FXML
    private FlowPane actionUnitPane;
    @FXML
    private AnchorPane normalActionTile;
    @FXML
    private AnchorPane frenzyActionTile;
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
    @FXML
    private Button ms;
    @FXML
    private Button mmgAdr;
    @FXML
    private Button pass;

    /**
     * The main game board initializer which is called when the GameBoard.fxml file is loaded.
     */
    @FXML
    private void initialize() {
        playerBoardControllerMap = new HashMap<>();
        pbControllerList = new ArrayList<>();
        initialized = false;
    }

    /**
     *  Is called by the main application to set itself.
     *
     * @param view
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Initializes all parameters.
     *
     */
    public void init() {
        setInfoPaneStyle();
        initMap();
        initPlayerBoards();
        initMyCards();

        initialized = false;
        //showRanking();
    }

    /**
     * Sets the style of info panes.
     *
     */
    public void setInfoPaneStyle() {
        infoPane.getStyleClass().add("info-pane");
    }

    public void setInfoText(String info) {
        infoText.setText(info);
    }

    /**
     * Initializes the map by loading the fxml containing the decorated map.
     *
     */
    public void initMap() {

        if (!initialized) {
            try {
                FXMLLoader mloader = new FXMLLoader();
                mloader.setLocation(getClass().getResource("/fxml/Map.fxml"));
                AnchorPane decoratedMap = mloader.load();
                mapController = mloader.getController();
                mapController.setView(view);

                // removes the old anchor and adds the new one
                leftVBox.getChildren().remove(0);
                leftVBox.getChildren().add(0, decoratedMap);
                mapController.handleMapLoading();
                progressBar = mapController.getProgressBar();
                initialized = true;
            } catch (IOException e) {
                logger.warning("Error loading map.");
            }
        }
    }

    /**
     * Calls the showMap methos of the mapController, which renders the kill shot track, the players and the
     */
    public void showMap() {
        mapController.showKillShotTrack();
        mapController.showPlayers();
        mapController.showAmmoGrid();
        mapController.showWeaponCrates();
    }

    /**
     * Initializes the players' boards with all their info.
     *
     */
    public void initPlayerBoards() {
        Font.loadFont(PlayerBoardController.class.getResource(SOFACHROMERG_FONT).toExternalForm(),10);

        Game game = view.getModel().getGame();
        AnchorPane playerBoardPane;
        int i = 0;
        for (PlayerColor pc : game.getActiveColors()) {
            try {
                FXMLLoader playerLoader = new FXMLLoader();
                playerLoader.setLocation(getClass().getResource("/fxml/PlayerBoard.fxml"));
                // gets the decorated pane
                AnchorPane decoratedPane = playerLoader.load();

                PlayerBoardController playerController = playerLoader.getController();
                playerBoardControllerMap.put(pc, playerController);
                playerController.setView(view);
                playerController.setPlayerColor(pc);
                playerController.initMarkerPane(pc);

                if (pc != view.getPlayerColor()) {
                    // gets the anchorPane containing the imageview
                    AnchorPane box = (AnchorPane) rightVBox.getChildren().get(i);
                    box.setVisible(true);
                    playerBoardPane = (AnchorPane) box.getChildren().get(1);
                    i++;
                }else {
                    playerBoardPane = playerBoard;
                }

                // removes the static image view and loads the decorated one
                playerBoardPane.getChildren().remove(0);
                playerBoardPane.getChildren().add(decoratedPane);

            }
            catch (IOException e) {
                logger.warning(e.toString());
            }
        }
    }

    public void showPlayerBoards() {
        Game game = view.getModel().getGame();
        int i = 0;
        for (PlayerColor pc : game.getActiveColors()) {
            PlayerBoardController playerController = playerBoardControllerMap.get(pc);
            if (pc != view.getPlayerColor()) {
                // gets the anchorPane containing the imageview
                AnchorPane box = (AnchorPane) rightVBox.getChildren().get(i);
                box.setVisible(true);

                HBox scoreBox = (HBox)((AnchorPane)rightVBox.getChildren().get(i)).getChildren().get(2);
                HBox nameBox = (HBox)((AnchorPane)rightVBox.getChildren().get(i)).getChildren().get(3);
                GridPane ammoPane = (GridPane)((AnchorPane)rightVBox.getChildren().get(i)).getChildren().get(4);

                showName(game.getPlayerByColor(pc), (Label) nameBox.getChildren().get(0));
                showScore(game.getPlayerByColor(pc),(Label) scoreBox.getChildren().get(0));
                showAmmo(game.getPlayerByColor(pc), ammoPane);
                showOpponentUnloadedWeapons(game.getPlayerByColor(pc), i);
                i++;
            }else {
//                showActionButtons(game.getPlayerByColor(pc));
                actionButtons.setDisable(true);
                showName(game.getPlayerByColor(pc), myName);
                showAmmo(game.getPlayerByColor(pc), myAmmo);
                showScore(game.getPlayerByColor(pc), myScore);
            }
            playerController.showPlayerBoard(game.getPlayerByColor(pc));
            playerController.showDamageBar(game.getPlayerByColor(pc));
            playerController.showMarkerBar(game.getPlayerByColor(pc));
            playerController.showSkullBar(game.getPlayerByColor(pc));
            playerController.showActionTile(game.getPlayerByColor(pc));
        }


    }

    /**
     * Initialize the client's cards to respond to mouse click events.
     *
     */
    public void initMyCards() {

        Arrays.asList(myPowerUps,myWeapons).stream()
                .forEach(myCards -> {
                    myCards.setDisable(true);
                    myCards.getChildren().stream()
                            .map(n -> (ImageView) n)
                            .forEach(c -> {
                                c.setVisible(false);
                                c.setImage(new NamedImage(DEFAULT_CARD_PATH,WEAPON_PATH));
                                c.setOnMouseClicked(event -> {
                                    c.setOpacity(Constants.CLICKED_OPACITY);

                                    Util.ifFirstSelection(confirmButton, progressBar);
                                    Util.updateCircle(progressBar);

                                    if(Util.isLastSelection(progressBar)) {
                                        myCards.setDisable(true);
                                    }

                                    NamedImage image = (NamedImage) c.getImage();
                                    addInput(SHOOT_WEAPON, image.getName());
                                });
                            });
                });
    }

    /**
     * Plainly shows the client player's weapon and powerup cards.
     *
     */
    public void showMyCards() {
        CharacterState myCharacterState =  view.getModel().getGame().getPlayerList().stream()
                .filter(p -> p.getColor() == view.getPlayerColor())
                .collect(Collectors.toList()).get(0).getCharacterState();
        List<Weapon> myWeaponsModel = myCharacterState.getWeaponBag();
        List<PowerUp> myPowerUpsModel = myCharacterState.getPowerUpBag();

        myWeapons.setDisable(true);
        if (!myWeapons.getStyleClass().isEmpty()) {
            myWeapons.getStyleClass().remove(0);
        }
        IntStream.range(0, myWeaponsModel.size())
                .forEach(i -> {
                    ImageView iv = null;
                    iv = (ImageView) myWeapons.getChildren().get(i);
                    if (!myWeaponsModel.get(i).isLoaded()) {
                        iv.setOpacity(Constants.CLICKED_OPACITY);
                        iv.setDisable(true);
                    }
                    else {
                        iv.setOpacity(1.0);
                        iv.setDisable(false);
                    }
                    iv.setImage(new NamedImage(Constants.WEAPON_PATH + myWeaponsModel.get(i).getName() + ".png",
                            Constants.WEAPON_PATH));
                    iv.setVisible(true);
                });

        myPowerUps.setDisable(true);
        if (!myPowerUps.getStyleClass().isEmpty()) {
            myPowerUps.getStyleClass().remove(0);
        }
        IntStream.range(0, myPowerUpsModel.size())
                .forEach(i -> {
                    ImageView iv = (ImageView) myPowerUps.getChildren().get(i);
                    iv.setImage(new NamedImage(Constants.POWERUP_PATH+myPowerUpsModel.get(i).getName()+".png",
                            Constants.POWERUP_PATH));
                    iv.setVisible(true);
                    iv.setOpacity(1.0);
                });
    }

    /**
     * Shows the player's score.
     *
     * @param player can be either the client or an opponent.
     * @param score is the label containing the player's score.
     */
    public void showScore(Player player, Label score) {
        score.setText(player.getCharacterState().getScore().toString() + " pts");
    }

    /**
     * Shows the player's name.
     *
     * @param player can be either the client or an opponent.
     * @param name is the label containing the name of the player.
     */
    public void showName(Player player, Label name) {
        name.setText(player.getUserData().getNickname());
        name.getStyleClass().add("name");
        Util.setLabelColor(name, player.getColor());
    }

    /**
     * Shows the player's ammo.
     *
     * @param player can be either the client or an opponent.
     * @param gridPane  is the grid pane containing the ammo images and text.
     */
    public void showAmmo(Player player, GridPane gridPane) {
        Label blueAmmo = (Label) ((HBox) gridPane.getChildren().get(0)).getChildren().get(0);
        blueAmmo.setText(player.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).toString());
        Label redAmmo = (Label) ((HBox) gridPane.getChildren().get(1)).getChildren().get(0);
        redAmmo.setText(player.getCharacterState().getAmmoBag().get(AmmoColor.RED).toString());
        Label yellowAmmo = (Label) ((HBox) gridPane.getChildren().get(2)).getChildren().get(0);
        yellowAmmo.setText(player.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).toString());
    }

    /**
     * Shows opponents' unloaded weapons.
     *
     * @param player is an opponent player.
     * @param i the i-th player in the right half of the window.
     */
    public void showOpponentUnloadedWeapons(Player player, int i) {
        GridPane unloadedWeapons = (GridPane)((AnchorPane)rightVBox.getChildren().get(i)).getChildren().get(0);
        List<Weapon> weaponBag =player.getCharacterState().getWeaponBag();
        IntStream.range(0, weaponBag.size())
                .forEach(x -> {
                    if (weaponBag.get(x) != null) {
                        if (weaponBag.get(x).isLoaded()) {
                            unloadedWeapons.getChildren().get(x).setVisible(false);
                        }else {
                            ImageView iv = (ImageView) unloadedWeapons.getChildren().get(x);
                            iv.setVisible(true);
                            iv.setImage(new Image(Constants.WEAPON_PATH + weaponBag.get(x).getName() + ".png"));
                        }
                    }
                });
    }

    /**
     * Adds the buttons to the action tile according to player's color and
     * game's current mode.
     *
     */
    public void showActionButtons() {
        Player player = view.getModel().getGame().getPlayerByColor(view.getPlayerColor());
        actionButtons.setDisable(false);

        if (view.getModel().getGame().isFrenzy()) {
            normalActionTile.setVisible(false);
            frenzyActionTile.setVisible(true);
            frenzyActionTile.toFront();
        } else {
            normalActionTile.setVisible(true);
            frenzyActionTile.setVisible(false);
            normalActionTile.toFront();
        }

        // shows adrenaline buttons
        if (Util.getCorrectPlayerBoardMode(player).equalsIgnoreCase(Constants.NORMAL)) {

            if (player.getCharacterState().getDamageBar().size() > 1 && player.getCharacterState().getDamageBar().size() < 5){
                mmgAdr.setVisible(true);
                ms.setVisible(false);
            }
            else if (player.getCharacterState().getDamageBar().size() >= 5){
                mmgAdr.setVisible(true);
                ms.setVisible(true);
            }
            else {
                mmgAdr.setVisible(false);
                ms.setVisible(false);
            }
        }
    }



    /**
     * Show the player's raking with their scores.
     *
     */
    public void showRanking() {
        List<Player> ranking = view.getModel().getGame().getRanking();
        rankingGrid.getChildren().removeAll(rankingGrid.getChildren());
        ranking.stream()
                .forEach(p -> {
                    Label name = new Label(p.getUserData().getNickname());
                    Label score = new Label(p.getCharacterState().getScore().toString());
                    Util.setLabelColor(name, p.getColor());
                    Util.setLabelColor(score, p.getColor());
                    rankingGrid.add(name, 0,ranking.indexOf(p));
                    rankingGrid.add(score, 1, ranking.indexOf(p));
                });
    }


    /**
     * Handles the confirm button, it prepares the interface to receive the next input.
     * Finally, it refreshes the interface as the cancel button.
     *
     */
    @FXML
    public void handleConfirm() {
        intermediateInput.keySet().stream()
                .forEach(k -> view.getPlayerInput().put(k, intermediateInput.get(k)));
        if (view.getInputRequested().isEmpty())
        {
            view.sendInput();
            handleCancel();
        }
        else {
            handleReset();
            view.askInput();
        }
    }

    /**
     * Handles the cancel button, it refreshes the interface to a state prior to the input
     * selection.
     *
     */
    @FXML
    public void handleCancel() {
        System.out.println(">>> Input new action:");
        handleReset();
        view.getInputRequested().clear();
        intermediateInput.clear();
        view.showMessage(((GUIView)view).getMessage());
    }

    public void handleReset() {
        progressBar.getChildren().stream()
                .map(n -> (Circle) n)
                .forEach(c -> {
                    c.setFill(Paint.valueOf("white"));
                    c.setVisible(false);
                });
        infoText.setText("Select an action");
        actionButtons.setDisable(false);
        cancelButton.setDisable(true);
        confirmButton.setDisable(true);
        actionUnitPane.setVisible(false);

        showMyCards();

        actionButtons.setDisable(false);
        mapController.resetGrids();
    }

    /**
     * Getter for the player's board.
     *
     * @return player board the player.
     */
    public AnchorPane getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Setter for the player's board.
     *
     * @param playerBoard is the new player's board.
     */
    public void setPlayerBoard(AnchorPane playerBoard) {
        this.playerBoard = playerBoard;
    }

    /**
     * Getter for the map of the game.
     *
     * @return the map of the game.
     */
    public AnchorPane getMap() {
        return map;
    }

    /**
     * Setter for the map of the game.
     *
     * @param map is the new map of the game.
     */
    public void setMap(AnchorPane map) {
        this.map = map;
    }

    /**
     * Getter for the left Vbox of the game board.
     *
     * @return left Vbox of the game board.
     */
    public VBox getLeftVBox() {
        return leftVBox;
    }

    /**
     * Gets the list of the player boards' controller.
     *
     * @return list of player boards' controller
     */
    public List<PlayerBoardController> getPbControllerList() {
        return pbControllerList;
    }

    /**
     * Gets the player board corresponding to the player's color
     *
     * @param playerColor the player's color associated to the board
     * @return the player board with the correct player color
     */
    public PlayerBoardController getPlayerBoardController(PlayerColor playerColor) {
        Optional<PlayerBoardController> optional = getPbControllerList().stream()
                .filter(pc -> pc.getPlayerColor() == playerColor)
                .findFirst();

        return optional.orElse(null);
    }


    public Map<String, List<String>> getIntermediateInput() {
        return intermediateInput;
    }

    public void setIntermediateInput(Map<String, List<String>> intermediateInput) {
        this.intermediateInput = intermediateInput;
    }


    public void setCardSelectionBehavior(ImageView iv, Node myCards, String action, Runnable additionalBehaviour) {

        iv.setOnMouseClicked(event -> {
            addKeyOrderAction(action);
            iv.setOpacity(Constants.CLICKED_OPACITY);
            iv.setDisable(true);

            Util.ifFirstSelection(confirmButton, progressBar);
            Util.updateCircle(progressBar);

            if(Util.isLastSelection(progressBar)) {
                myCards.setDisable(true);
            }

            NamedImage image = (NamedImage) iv.getImage();
            addInput(action, image.getName());

            additionalBehaviour.run();
        });
    }

    public void addInput(String key, String id) {
        intermediateInput.putIfAbsent(key, new ArrayList<>());
        intermediateInput.get(key).add(id);
        System.out.println("Added: " + key + " " + id);
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
        view.getInputRequested().add(this::getShootWeapon);
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
        view.getInputRequested().add(this::getShootWeapon);
        view.askInput();
    }

    @FXML
    public void handleMS() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        view.getInputRequested().add(this::getTile);
        view.getInputRequested().add(this::getShootWeapon);
        view.askInput();
    }

    @FXML
    public void handlePass() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        addKeyOrderAction(NOP);
        intermediateInput.put(NOP, new ArrayList<>());
        intermediateInput.keySet().stream()
                .forEach(k -> view.getPlayerInput().put(k, intermediateInput.get(k)));
        view.sendInput();
        intermediateInput.clear();
        pass.setDisable(true);
    }

    private void addKeyOrderAction(String key) {
        if (!intermediateInput.containsKey(Constants.KEY_ORDER)) {
            List<String> lst = new ArrayList<>();
            lst.add(key);
            intermediateInput.put(Constants.KEY_ORDER, lst);
        } else {
            if (!intermediateInput.get(Constants.KEY_ORDER).contains(key)) {
                intermediateInput.get(Constants.KEY_ORDER).add(key);
            }
        }
    }

    public void getTile(){

        addKeyOrderAction(MOVE);

        mapController.getTileGrid().toFront();
        mapController.getTileGrid().setDisable(false);
        mapController.getTileGrid().setVisible(true);

        infoText.setText("Select 1 tile ");
        cancelButton.setDisable(false);

        setUpProgressBar(1);
    }

    public void getShootTile(String playerAction, int amount) {

        mapController.getShootTileGrid().toFront();
        mapController.getShootTileGrid().setDisable(false);
        mapController.getShootTileGrid().setVisible(true);

        Tile[][] tileMap = view.getModel().getGame().getBoard().getTileMap();
        IntStream.range(0, tileMap[0].length)
                .forEach(y -> IntStream.range(0, tileMap.length)
                        .forEach(x -> {
                            if (tileMap[x][y] != null) {
                                AnchorPane shootAncorPane = (AnchorPane) mapController.getShootTileGrid().getChildren().get(Util.convertToIndex(x, y));

                                mapController.setButtonTile(mapController.getShootTileGrid(), (Button) shootAncorPane.getChildren().get(0), () -> ((GUIView) view).getGuiController().addInput(playerAction, String.valueOf(Util.convertToIndex(x, y))));
                            }
                        }));


        infoText.setText("Select 1 tile ");
        cancelButton.setDisable(false);

        setUpProgressBar(amount);
    }

    public void getTarget(String playerAction, int amount) {

        mapController.getPlayerGrid().toFront();
        mapController.getPlayerGrid().setDisable(false);
        mapController.getPlayerGrid().setVisible(true);

        infoText.setText("Select " + amount + " players");
        cancelButton.setDisable(false);

        mapController.getPlayerGrid().getChildren().stream()
                .map(n -> (VBox) n)
                .forEach(vBox -> vBox.getChildren().stream()
                        .map(n -> (HBox) n)
                        .filter(hbox -> !hbox.getChildren().isEmpty())
                        .forEach(hBox -> hBox.getChildren().stream()
                                .map(n -> (Circle) n)
                                .filter(Node::isVisible)
                                .forEach(c -> {
                                    if (c.getFill() != Paint.valueOf(view.getPlayerColor().getColor())) {
                                        c.setDisable(false);
                                        c.getStyleClass().add(Constants.CIRCLE);
                                        c.getStyleClass().add(Constants.CSS_HOVERING);
                                        c.setOnMouseClicked(event -> {
                                            c.setDisable(true);
                                            c.setOpacity(Constants.CLICKED_OPACITY);

                                            Util.ifFirstSelection(confirmButton, progressBar);
                                            Util.updateCircle(progressBar);

                                            if (Util.isLastSelection(progressBar)) {
                                                mapController.getPlayerGrid().setDisable(true);
                                            }

                                            handlePlayerSelected(playerAction, c.getFill().toString());
                                        });
                                    } else {
                                        c.setDisable(true);
                                    }
                                })
                        )
                );

        setUpProgressBar(amount);
    }

    public void getCard() {

        addKeyOrderAction(GRAB);

        infoText.setText("Select 1 card ");
        cancelButton.setDisable(false);
        showGrabbableCards();

        setUpProgressBar(1);
    }

    public void getPowerUpForRespawn() {

//        addKeyOrderAction(RESPAWN);

        infoText.setText("Select 1 powerup ");
        cancelButton.setDisable(true);
        confirmButton.setDisable(true);

        myPowerUps.setDisable(false);
        myPowerUps.getStyleClass().add(Constants.SELECTION_NODE);
        myPowerUps.getChildren().stream().forEach(node -> node.getStyleClass().add(Constants.CSS_HOVERING));
        showMyPowerups(RESPAWN);
//        ((GUIView)view).getGuiController().getIntermediateInput().putIfAbsent(Constants.RESPAWN, new ArrayList<>());

        setUpProgressBar(1);
    }

    public void getReload() {

//        addKeyOrderAction(RELOAD);

        infoText.setText("Select 1 weapon ");
        cancelButton.setDisable(false);
        confirmButton.setDisable(false);

        myWeapons.setDisable(false);
        myWeapons.getStyleClass().add(Constants.SELECTION_NODE);
        myWeapons.getChildren().stream().forEach(node -> node.getStyleClass().add(Constants.CSS_HOVERING));

        showMyUnloadedWeapons();
//        ((GUIView)view).getGuiController().getIntermediateInput().putIfAbsent(Constants.RELOAD, new ArrayList<>());

        setUpProgressBar(3);

    }

    public void getShootWeapon() {

        addKeyOrderAction(SHOOT_WEAPON);

        infoText.setText("Select 1 weapon");
        cancelButton.setDisable(false);

        myWeapons.setDisable(false);
        myWeapons.getStyleClass().add(Constants.SELECTION_NODE);
        myWeapons.getChildren().stream().forEach(node -> node.getStyleClass().add(Constants.CSS_HOVERING));

        showMyLoadedWeapons();

        setUpProgressBar(1);
    }

    public void getActionUnit() {

        addKeyOrderAction(SHOOT);

        infoText.setText("Select 1 effect: ");
        cancelButton.setDisable(false);

        Weapon weapon = view.getModel().getGame().getCurrentWeapon();
        if (weapon != null) {
            actionUnitPane.setVisible(true);
            addInput(SHOOT, weapon.getId());
            IntStream.range(0, weapon.getActionUnitList().size() + weapon.getOptionalEffectList().size())
                    .forEach(i -> {
                        Button b = (Button) actionUnitPane.getChildren().get(i);

                        b.setVisible(true);
                        if (i < weapon.getActionUnitList().size()) {
                            b.setText(weapon.getActionUnitList().get(i).getName());

                            setActionUnitButton(b, weapon.getActionUnitList(), i);

                        } else {
                            b.setText(weapon.getOptionalEffectList().get(i - weapon.getActionUnitList().size()).getName());

                            setActionUnitButton(b, weapon.getOptionalEffectList(), weapon.getActionUnitList().size()-i);
                        }
                    });
        }
    }

    public void setActionUnitButton(Button b, List<ActionUnit> actionUnitList, int i) {
        b.setOnAction(event -> {
            // adds the action unit in the input list
            ((GUIView)view).getGuiController().getIntermediateInput().get(Constants.SHOOT).add(b.getText());

            view.getInputRequested().add(() -> getTarget(SHOOT, actionUnitList.get(i).getNumPlayerTargets()));
            view.getInputRequested().add(() -> getShootTile(SHOOT, actionUnitList.get(i).getNumTileTargets()));

            view.askInput();
        });
    }



    public void handlePlayerSelected(String playerActon, String color) {
        String id = view.getModel().getGame().getPlayerList().stream()
                .filter(p -> Paint.valueOf(color).equals(Paint.valueOf(p.getColor().getColor())))
                .collect(Collectors.toList()).get(0).getId();
        ((GUIView) view).getGuiController().addInput(playerActon, id);
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

        Logger.getGlobal().info("Grab: "+t);
        try {
            int[] coords = view.getModel().getGame().getBoard().getTilePosition(t);
            if (t.isSpawnTile()) {
                Logger.getGlobal().info("spawn tile");
                String roomColor = t.getRoomColor().getColor();
                Optional<GridPane> optGrid = mapController.getWeaponCrateList().stream()
                        .filter(wc -> wc.getId().split("Weapons")[0].equalsIgnoreCase(roomColor))
                        .findFirst();
                if (optGrid.isPresent()){
                    optGrid.get().setDisable(false);
                    optGrid.get().getStyleClass().add(Constants.SELECTION_NODE);
                    optGrid.get().getChildren().stream().forEach(node -> node.getStyleClass().add(CSS_HOVERING));
                    optGrid.get().getChildren().stream().forEach(node -> ((GUIView)view).getGuiController().setCardSelectionBehavior((ImageView)node, optGrid.get(), Constants.GRAB, () -> {}));
                }
            }
            else {
                Logger.getGlobal().info("normal tile");
                mapController.getAmmoGrid().toFront();
                mapController.getAmmoGrid().setDisable(false);
                mapController.getAmmoGrid().setVisible(true);
                HBox hBox = (HBox) mapController.getAmmoGrid().getChildren().get(Util.convertToIndex(coords[0], coords[1]));
                Node node = hBox.getChildren().get(0);
                ImageView iv = (ImageView) ((AnchorPane) node).getChildren().get(0);
                iv.setDisable(false);

                node.getStyleClass().add(Constants.SELECTION_NODE);
                node.getStyleClass().add(CSS_HOVERING);
                ((GUIView)view).getGuiController().setCardSelectionBehavior(iv, hBox, Constants.GRAB, () -> {});
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

                    ((GUIView)view).getGuiController().setCardSelectionBehavior(iv, myWeapons, Constants.RELOAD, () -> {});
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
                    } else {
                        iv.setVisible(true);
                        iv.setOpacity(1.0);
                        iv.setDisable(false);
                    }

                    ((GUIView)view).getGuiController().setCardSelectionBehavior(iv, myWeapons, SHOOT_WEAPON, () -> {});
                });
    }

    /**
     * Shows the powerups for selection.
     *
     */
    public void showMyPowerups(String playerAction) {
        CharacterState myCharacterState =  view.getModel().getGame().getPlayerByColor(view.getPlayerColor()).getCharacterState();
        List<PowerUp> myPowerUpsModel = myCharacterState.getPowerUpBag();

        myPowerUps.getChildren().forEach(node -> node.setVisible(false));
        IntStream.range(0, myPowerUpsModel.size())
                .forEach(i -> {
                    ImageView iv = (ImageView) myPowerUps.getChildren().get(i);
                    iv.setVisible(true);
                    iv.setOpacity(1.0);
                    iv.setDisable(false);

                    ((GUIView)view).getGuiController().setCardSelectionBehavior(iv, myPowerUps, playerAction, () -> {});
                });
    }

    public void showPowerUps(List<String> powerUpList) {
        CharacterState myCharacterState =  view.getModel().getGame().getPlayerByColor(view.getPlayerColor()).getCharacterState();
        List<PowerUp> myPowerUpsModel = myCharacterState.getPowerUpBag();

        myPowerUps.setDisable(false);
        myPowerUps.getStyleClass().add(Constants.SELECTION_NODE);
        myPowerUps.getChildren().forEach(node -> {
            node.setDisable(true);
            node.setVisible(false);
            node.setOpacity(CLICKED_OPACITY);
        });
        myPowerUps.getChildren().stream().map(n -> (ImageView) n)
                .filter(iv -> powerUpList.contains(((NamedImage)iv.getImage()).getName().split("_")[1]))
                .forEach(iv -> {
                    iv.setVisible(true);
                    iv.setOpacity(1.0);
                    iv.setDisable(false);
                    iv.getStyleClass().add(Constants.CSS_HOVERING);
                    ((GUIView) view).getGuiController().setCardSelectionBehavior(iv, myPowerUps, POWERUP, () -> {
                        PowerUp powerUp = myPowerUpsModel.stream().filter(pU -> pU.getName().equals(((NamedImage)iv.getImage()).getName())).findFirst().orElse(null);
                        if (powerUp != null) {
                            if (powerUp.getActionUnitList().get(ACTIONUNIT_POSITION).getNumPlayerTargets() > 0) {
                                view.getInputRequested().add(() -> getTarget(POWERUP, powerUp.getActionUnitList().get(ACTIONUNIT_POSITION).getNumPlayerTargets()));
                            }
                            if (powerUp.getActionUnitList().get(ACTIONUNIT_POSITION).getNumTileTargets() > 0) {
                                view.getInputRequested().add(() -> getShootTile(POWERUP, powerUp.getActionUnitList().get(ACTIONUNIT_POSITION).getNumTileTargets()));
                            }
                        }

                        view.askInput();
                    });
                });
    }


    public void showPass() {
        pass.setDisable(false);
    }
}
