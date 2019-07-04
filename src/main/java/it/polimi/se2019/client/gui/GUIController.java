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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.polimi.se2019.client.util.Constants.*;

/**
 * The GUIController is the main gui controller, it intializes all the graphic elements from their fxml files. Then
 * it provides all the methods for game rendering and player input building.
 *
 * @author andreahuang
 */
public class GUIController {

    private static final Logger logger = Logger.getLogger(GUIController.class.getName());
    private static final int ACTIONUNIT_POSITION = 0;

    private View view;
    private Map<PlayerColor, PlayerBoardController> playerBoardControllerMap;
    private List<PlayerBoardController> playerBoardControllerList;
    private MapController mapController;
    private Map<String, List<String>> intermediateInput = new HashMap<>();
    private String message;

    private GridPane progressBar;

    @FXML
    private VBox leftVBox;
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
    private FlowPane actionUnitPane;
    @FXML
    private AnchorPane normalActionTile;
    @FXML
    private AnchorPane frenzyActionTile;
    @FXML
    private Button ms;
    @FXML
    private Button mmgAdr;
    @FXML
    private Button pass;
    @FXML
    private Button closeButton;
    @FXML
    private Label currentPlayer;

    /**
     * The main game board initializer which is called when the GameBoard.fxml file is loaded.
     *
     */
    @FXML
    private void initialize() {
        playerBoardControllerMap = new HashMap<>();
        playerBoardControllerList = new ArrayList<>();
    }

    /**
     * The setView method is called by the main application to set itself and give access to the model for linking.
     *
     * @param view is the view that interacts with the server.
     *
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * The init method initializes all the graphic parameters of the game using support methods.
     *
     */
    public void init() {
        setInfoPaneStyle();
        initMap();
        initPlayerBoards();
        initMyCards();
    }

    /**
     * The setInfoPaneStyle method sets the style of info panes.
     *
     */
    public void setInfoPaneStyle() {
        infoText.setWrapText(true);
        infoText.setTextAlignment(TextAlignment.CENTER);
        infoPane.getStyleClass().add("info-pane");
    }

    public void setInfoText(String info) {
        infoText.setText(info);
    }

    /**
     * The initMap method the map by loading the fxml containing the decorated map.
     *
     */
    public void initMap() {

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
        } catch (IOException e) {
            logger.warning("Error loading map.");
        }

    }

    /**
     * The showMap method calls methods provided by the mapController to render the kill shot track, the players, the
     * ammo crates, and the weapon crates.
     *
     */
    public void showMap() {
        mapController.showKillShotTrack();
        mapController.showPlayers();
        mapController.showAmmoGrid();
        mapController.showWeaponCrates();
    }

    /**
     * The initPlayerBoards method initializes the players' boards by loading them from the fxml file.
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
                    // gets the anchorPane containing the image view
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

    /**
     * The showPlayerBoards method shows the player board on the board. It is called on every update to refresh the
     * interface.
     */
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
     * The initMyCards method initializes the player's power up and weapon cards to respond to mouse click events.
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
     * The showMyCards method shows the client player's weapon and power up cards. It is called on every update to refresh the
     * interface.
     *
     */
    public void showMyCards() {
        CharacterState myCharacterState =  view.getModel().getGame().getPlayerList().stream()
                .filter(p -> p.getColor() == view.getPlayerColor())
                .collect(Collectors.toList()).get(0).getCharacterState();
        List<Weapon> myWeaponsModel = myCharacterState.getWeaponBag();
        List<PowerUp> myPowerUpsModel = myCharacterState.getPowerUpBag();

        myWeapons.setDisable(true);
        myWeapons.getChildren().forEach(node -> node.setVisible(false));
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
        myPowerUps.getChildren().forEach(node -> node.setVisible(false));
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
     * The showScore method shows a player's score.
     *
     * @param player can be either the client or an opponent.
     * @param score is the label containing the player's score.
     */
    public void showScore(Player player, Label score) {
        score.setText(player.getCharacterState().getScore().toString() + " pts");
    }

    /**
     * The showName method shows the player's name.
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
     * The showAmmo method shows the player's ammo.
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
     * The showOpponentUnloadedWeapons shows the opponents' unloaded weapons.
     *
     * @param player is an opponent player.
     * @param i the i-th player in the right half of the window.
     */
    public void showOpponentUnloadedWeapons(Player player, int i) {
        GridPane unloadedWeapons = (GridPane)((AnchorPane)rightVBox.getChildren().get(i)).getChildren().get(0);
        List<Weapon> weaponBag = player.getCharacterState().getWeaponBag();
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
     * The showActionButtons show the buttons to the action tile according to player's color and
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
     * The showRanking method shows the player's ranking with their scores. It only allows exit from the game.
     *
     */
    public void showRanking() {
        Alert scoreWindow = new Alert(Alert.AlertType.INFORMATION);
        scoreWindow.initStyle(StageStyle.UTILITY);

        List<Player> ranking = view.getModel().getGame().getRanking();
        GridPane rankingGrid = new GridPane();
        rankingGrid.getChildren().removeAll(rankingGrid.getChildren());
        ranking.stream()
                .forEach(p -> {
                    Label name = new Label(p.getUserData().getNickname());
                    Label score = new Label(p.getCharacterState().getScore().toString());
                    Util.setLabelColor(name, p.getColor());
                    Util.setLabelColor(score, p.getColor());
                    rankingGrid.add(name, 0, ranking.indexOf(p));
                    rankingGrid.add(score, 1, ranking.indexOf(p));
                });
        BackgroundFill background_fill = new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(background_fill);
        rankingGrid.setBackground(background);

        scoreWindow.setTitle("Ranking");
        scoreWindow.setHeaderText("This is the ranking");
        scoreWindow.setGraphic(rankingGrid);
        scoreWindow.initOwner(((GUIView)view).getPrimaryStage());
        scoreWindow.initModality(Modality.WINDOW_MODAL);
        Optional<ButtonType> result = scoreWindow.showAndWait();
        if(!result.isPresent()) {
            // alert is exited, no button has been pressed.
        } else if(result.get() == ButtonType.OK) {
            //okay button is pressed
            System.exit(0);
        }

    }

    /**
     * The showCurrentPlayer method shows the current player's name.
     *
     */
    public void showCurrentPlayer() {
        Player player = view.getModel().getGame().getCurrentPlayer();
        currentPlayer.setText(player.getUserData().getNickname());
        Util.setLabelColor(currentPlayer, player.getColor());
    }


    /**
     * The handleClose method handle the close event when the close button is clicked.
     *
     */
    @FXML
    public void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        System.exit(0);
    }


    /**
     * The handleConfirm method handles the mouse click event on the confirm button, it prepares the interface to receive
     * the next input. Finally, it sends the input and refreshes the interface calling the cancel button.
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
     * The handleCancel method handles the mouse click event on the cancel button, it refreshes the interface to a
     * state prior to the input selection.
     *
     */
    @FXML
    public void handleCancel() {
        System.out.println(">>> Input new action:");
        handleReset();
        view.getInputRequested().clear();
        intermediateInput.clear();
        Logger.getGlobal().info("Cancelling, show message: "+message);
        view.showMessage(message);
    }

    /**
     * The handleReset method handles the refreshing of the interface.
     *
     */
    public void handleReset() {
        progressBar.getChildren().stream()
                .map(n -> (Circle) n)
                .forEach(c -> {
                    c.setFill(Paint.valueOf("white"));
                    c.setVisible(false);
                });
        infoText.setText("Select an action");
        cancelButton.setDisable(true);
        confirmButton.setDisable(true);
        actionUnitPane.setVisible(false);

        showMyCards();

        actionButtons.setDisable(true);
        mapController.resetGrids();
        resetAmmoStyle();
    }

    /**
     * The storeMessage method is used store the message received from the server. It is necessary to implement the
     * action cancelling functionality.
     *
     * @param message is the message received from the server
     */
    public void storeMessage(String message) {
        this.message = message;
    }

    /**
     * Getter for  the list of the player boards' controller.
     *
     * @return list of player boards' controller
     */
    public List<PlayerBoardController> getPlayerBoardControllerList() {
        return playerBoardControllerList;
    }

    /**
     * Getter for the player board corresponding to the player's color.
     *
     * @param playerColor the player's color associated to the board
     * @return the player board with the correct player color
     */
    public PlayerBoardController getPlayerBoardController(PlayerColor playerColor) {
        Optional<PlayerBoardController> optional = getPlayerBoardControllerList().stream()
                .filter(pc -> pc.getPlayerColor() == playerColor)
                .findFirst();

        return optional.orElse(null);
    }

    /**
     * The getIntermediate input is the getter for a cache for the player input. It is forwarded to the view only once
     * the whole action has been performed.
     *
     * @return the map built as input to send to the server
     */
    public Map<String, List<String>> getIntermediateInput() {
        return intermediateInput;
    }

    /**
     * The setCardSelectionBehavior method gives a basic behaviour to an image view, and then adds additional behaviour
     * only when necessary.
     *
     * @param iv is the image view on which to set a behaviour.
     * @param myCards is the container of the image view, and is disabled when the image view is clicked.
     * @param action is the string action that is used as key for input building
     * @param additionalBehaviour is a Runnable used to add some additional behaviour to the mouse click event
     */
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

    /**
     * The addInput method receives a key string and an id string to build the input.
     *
     * @param key is the string used as key for input building.
     * @param id is the string used as value for input building.
     */
    public void addInput(String key, String id) {
        intermediateInput.putIfAbsent(key, new ArrayList<>());
        if (!intermediateInput.get(key).contains(id)) {
            intermediateInput.get(key).add(id);
        }
        System.out.println("Added: " + key + " " + id);
    }

    /**
     * The handleM method asks a tile input from the player when the 'm' is clicked.
     *
     */
    @FXML
    public void handleM() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        view.getInputRequested().add(this::getTile);
        view.askInput();
    }

    /**
     * The handleMG method asks a tile input and a card input from the player when the 'ms' button is clicked.
     *
     */
    @FXML
    public void handleMG() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        view.getInputRequested().add(this::getTile);
        view.getInputRequested().add(this::getCard);
        view.askInput();
    }

    /**
     * The handleMG method asks a tile input and a card input from the player when the 's' button is clicked.
     *
     */
    @FXML
    public void handleS() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        getIntermediateInput().clear();
        view.getInputRequested().add(this::getShootWeapon);
        view.askInput();
    }

    /**
     * The handleR method asks a reload input from the player when the 'r' button is clicked.
     *
     */
    @FXML
    public void handleR() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        getIntermediateInput().clear();
        view.getInputRequested().add(this::getReload);
        view.askInput();
    }

    /**
     * The handleMRS method asks a tile input, a relaod input, and a shoot weapon input when the 'mrs' button
     * is clicked.
     *
     */
    @FXML
    public void handleMRS() {
        handleCancel();
        actionButtons.setDisable(true);
        view.getInputRequested().add(this::getTile);
        view.getInputRequested().add(this::getReload);
        view.getInputRequested().add(this::getShootWeapon);
        view.askInput();
    }

    /**
     * The handleMS method asks a tile input and a shoot weapon input when the 'ms' button is clicked.
     */
    @FXML
    public void handleMS() {
        ((GUIView)view).getGuiController().handleCancel();
        actionButtons.setDisable(true);
        view.getInputRequested().add(this::getTile);
        view.getInputRequested().add(this::getShootWeapon);
        view.askInput();
    }

    /**
     * The handlePass method directly sends a NOP(no operation) to the server to skip an action.
     *
     */
    @FXML
    public void handlePass() {

        message = "";
        addKeyOrderAction(NOP);
        intermediateInput.put(NOP, new ArrayList<>());
        intermediateInput.keySet().stream()
                .forEach(k -> view.getPlayerInput().put(k, intermediateInput.get(k)));
        view.sendInput();
        intermediateInput.clear();
        pass.setDisable(true);

        handleCancel();
    }

    /**
     * The addKeyOrderAction method adds a 'keyOrder' key to the intemediateInput map if not present. Otherwise, if the
     * the key string is not contained in the value list of the 'keyOrder' key, it adds it to the list.
     *
     * @param key
     */
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

    /**
     * The getTile method is used to ask a tile input for a move action to the player.
     *
     */
    public void getTile(){

        addKeyOrderAction(MOVE);

        mapController.getTileGrid().toFront();
        mapController.getTileGrid().setDisable(false);
        mapController.getTileGrid().setVisible(true);

        infoText.setText("Select one tile ");
        cancelButton.setDisable(false);

        setUpProgressBar(1);
    }

    /**
     * The getShootTile method is used to ask a tile for a weapon shoot or powerUp action to the player.
     *
     * @param playerAction is either a shoot or a powerUp action.
     * @param amount is the amount of tile asked from by the action.
     */
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
        confirmButton.setDisable(true);
        cancelButton.setDisable(false);

        setUpProgressBar(amount);
    }

    /**
     * The getTarget method is used to ask a player target for a weapon shoot or powerUp action to the player.
     *
     * @param playerAction is either a shoot or a powerUp action.
     * @param amount is the amount of tile asked from by the action.
     */
    public void getTarget(String playerAction, int amount) {

        mapController.getPlayerGrid().toFront();
        mapController.getPlayerGrid().setDisable(false);
        mapController.getPlayerGrid().setVisible(true);

        infoText.setText("Select " + amount + " players");
        confirmButton.setDisable(true);
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

    /**
     * The getCard method is used to ask a card input for a grab action to the player.
     *
     */
    public void getCard() {

        addKeyOrderAction(GRAB);

        infoText.setText("Select one card to grab ");
        cancelButton.setDisable(false);
        showGrabbableCards();
    }

    /**
     * The getPowerUpForRespawn method is used to ask a power up for a respawn action to the player.
     *
     */
    public void getPowerUpForRespawn() {

        cancelButton.setDisable(true);
        confirmButton.setDisable(true);

        myPowerUps.setDisable(false);
        myPowerUps.getStyleClass().add(Constants.SELECTION_NODE);
        myPowerUps.getChildren().stream().forEach(node -> node.getStyleClass().add(Constants.CSS_HOVERING));
        showMyPowerups(RESPAWN);

        setUpProgressBar(1);
    }

    /**
     * The getReload method is used to ask a weapon card input for a reload action to the player.
     *
     */
    public void getReload() {

        infoText.setText("Select one or more weapons to reload");
        cancelButton.setDisable(false);
        confirmButton.setDisable(false);

        myWeapons.setDisable(false);
        myWeapons.getStyleClass().add(Constants.SELECTION_NODE);
        myWeapons.getChildren().stream().forEach(node -> node.getStyleClass().add(Constants.CSS_HOVERING));

        showMyUnloadedWeapons();

    }

    /**
     * The getShootWeapon method is used to ask a weapon card input for a shoot weapon action to the player.
     */
    public void getShootWeapon() {

        addKeyOrderAction(SHOOT_WEAPON);

        infoText.setText("Select one weapon to start shooting");
        cancelButton.setDisable(false);

        myWeapons.setDisable(false);
        myWeapons.getStyleClass().add(Constants.SELECTION_NODE);
        myWeapons.getChildren().stream().forEach(node -> node.getStyleClass().add(Constants.CSS_HOVERING));

        showMyLoadedWeapons();

        setUpProgressBar(1);
    }

    /**
     * The getActionUnit method is used to ask an action unit input for a shoot action to the player.
     * It can ask either tiles or targets, even both, to activate the action unit of the weapon.
     *
     */
    public void getActionUnit() {

        cancelButton.setDisable(false);

        Weapon weapon = view.getModel().getGame().getCurrentWeapon();
        if (weapon != null) {
            actionUnitPane.setVisible(true);
            IntStream.range(0, weapon.getActionUnitList().size() + weapon.getOptionalEffectList().size())
                    .forEach(i -> {
                        Button b = (Button) actionUnitPane.getChildren().get(i);

                        b.setVisible(true);
                        if (i < weapon.getActionUnitList().size()) {
                            b.setText(weapon.getActionUnitList().get(i).getName());

                            setActionUnitButton(b, weapon.getActionUnitList(), i, weapon.getId());

                        } else {
                            b.setText(weapon.getOptionalEffectList().get(i - weapon.getActionUnitList().size()).getName());

                            setActionUnitButton(b, weapon.getOptionalEffectList(), i-weapon.getActionUnitList().size(), weapon.getId());
                        }
                    });
        }
    }

    /**
     * The setActionUnitButton method is used to give behaviour to an action button based on the list of action units
     * of a weapon.
     *
     * @param b is the button on which to set the behaviour.
     * @param actionUnitList is the list of action units that asks tile or player target inputs.
     * @param i is index of the action in the action unit list.
     * @param weaponID is the id of the weapon that is performing the action unit.
     */
    public void setActionUnitButton(Button b, List<ActionUnit> actionUnitList, int i, String weaponID) {
        b.setOnAction(event -> {
            // adds the action unit in the input list
            addInput(SHOOT, weaponID);
            ((GUIView)view).getGuiController().getIntermediateInput().get(Constants.SHOOT).add(b.getText());
            addKeyOrderAction(SHOOT);
            if (actionUnitList.get(i).getNumPlayerTargets() > 0) {
                view.getInputRequested().add(() -> getTarget(SHOOT, actionUnitList.get(i).getNumPlayerTargets()));
            }
            if (actionUnitList.get(i).getNumTileTargets() > 0) {
                view.getInputRequested().add(() -> getShootTile(SHOOT, actionUnitList.get(i).getNumTileTargets()));
            }
            view.askInput();
        });
    }

    /**
     * The handlePlayerSelected method is used to add a player target input to a player action.
     *
     * @param playerActon is the playerAction for which to add the input.
     * @param color is the input to add for the player action.
     */
    public void handlePlayerSelected(String playerActon, String color) {
        String id = view.getModel().getGame().getPlayerList().stream()
                .filter(p -> Paint.valueOf(color).equals(Paint.valueOf(p.getColor().getColor())))
                .collect(Collectors.toList()).get(0).getId();
        addInput(playerActon, id);
    }

    /**
     * The setUpProgressBar method sets up the number of circles indicating the max number of selections allowed.
     *
     * @param numOfTargets is maximum number of selections.
     */
    public void setUpProgressBar(int numOfTargets) {

        IntStream.range(0, numOfTargets)
                .forEach(i -> progressBar.getChildren().get(i).setVisible(true));
    }

    /**
     * The showGrabbableCards method shows the objects that are grabbable from the player's position in the map.
     * The grabbable cards can be either ammo crate or weapon crates. In case the player already has three cards
     * in his or her hand.
     *
     */
    public void showGrabbableCards() {
        Tile t;
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

                String roomColor = t.getRoomColor().getColor();
                Logger.getGlobal().info("spawn tile " + roomColor);
                Optional<GridPane> optGrid = mapController.getWeaponCrateList().stream()
                        .filter(wc -> wc.getId().split("Weapons")[0].equalsIgnoreCase(roomColor))
                        .findFirst();
                if (optGrid.isPresent()){
                    GridPane weaponCrate = optGrid.get();
                    Logger.getGlobal().info("Found weapon crate: " + weaponCrate.getId());
                    weaponCrate.setDisable(false);
                    weaponCrate.getStyleClass().add(SELECTION_NODE);
                    if(view.getModel().getGame().getCurrentPlayer().getCharacterState().getWeaponBag().size() == 3) {
                        setUpProgressBar(2);
                    } else {
                        setUpProgressBar(1);
                    }
                    weaponCrate.getChildren().forEach(node -> node.getStyleClass().add(CSS_HOVERING));
                    weaponCrate.getChildren().forEach(node -> {
                        node.setDisable(false);
                        setCardSelectionBehavior((ImageView) node, weaponCrate, GRAB, () -> {
                            //additional behaviour
                            if (view.getModel().getGame().getCurrentPlayer().getCharacterState().getWeaponBag().size() == 3) {
                                infoText.setText("Select a weapon to discard");
                                weaponCrate.setDisable(true);
                                myWeapons.getStyleClass().add(Constants.SELECTION_NODE);
                                myWeapons.getChildren().forEach(weapon -> {
                                    weapon.setDisable(false);
                                    weapon.setOpacity(1.0);
                                    weapon.getStyleClass().add(CSS_HOVERING);
                                });
                                myWeapons.setDisable(false);
                                Logger.getGlobal().info("Give behavior to weapons in hand ... ");
                                myWeapons.getChildren().forEach(weapon -> setCardSelectionBehavior((ImageView) weapon, myWeapons, GRAB, () -> {}));
                            }
                        });
                    });
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
                hBox.setDisable(false);
                node.setDisable(false);
                iv.setDisable(false);

                node.getStyleClass().add(Constants.SELECTION_NODE);
                node.getStyleClass().add(CSS_HOVERING);
                setCardSelectionBehavior(iv, node, Constants.GRAB, () -> {});

                setUpProgressBar(1);
            }
        } catch (TileNotFoundException e) {
            logger.warning(e.toString());
        }
    }

    /**
     * The showMyUnloadedWeapons shows the player's unloaded weapons for selection, and hides the loaded ones.
     *
     */
    public void showMyUnloadedWeapons() {
        CharacterState myCharacterState =  view.getModel().getGame().getPlayerList().stream()
                .filter(p -> p.getColor() == view.getPlayerColor())
                .collect(Collectors.toList()).get(0).getCharacterState();
        List<Weapon> myWeaponsModel = myCharacterState.getWeaponBag();

        if (myWeaponsModel.stream().noneMatch(weapon -> !weapon.isLoaded())) {
            cancelButton.setDisable(true);
            pass.setDisable(true);
            Logger.getGlobal().info("No weapons to reload");
            handlePass();

        } else {
            IntStream.range(0, myWeaponsModel.size())
                    .forEach(i -> {
                        ImageView iv = null;
                        iv = (ImageView) myWeapons.getChildren().get(i);
                        if (!myWeaponsModel.get(i).isLoaded()) {
                            iv.setOpacity(1.0);
                            iv.setDisable(false);
                            iv.setVisible(true);
                        } else {
                            iv.setVisible(false);
                        }

                        setCardSelectionBehavior(iv, myWeapons, Constants.RELOAD, () -> {
                        });
                    });
            setUpProgressBar((int)myWeaponsModel.stream().filter(weapon -> !weapon.isLoaded()).count());
        }
    }

    /**
     * The showMyLoadedWeapons method shows the player's loaded weapons for selection, and hides the unloaded ones.
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

                    setCardSelectionBehavior(iv, myWeapons, SHOOT_WEAPON, () -> {});
                });
    }

    /**
     * The showMyPowerups method shows all the player's powerUps for selection.
     *
     */
    public void showMyPowerups(String playerAction) {
        CharacterState myCharacterState =  view.getModel().getGame().getPlayerByColor(view.getPlayerColor()).getCharacterState();
        List<PowerUp> myPowerUpsModel = myCharacterState.getPowerUpBag();

        cancelButton.setDisable(false);
        myPowerUps.getChildren().forEach(node -> node.setVisible(false));
        IntStream.range(0, myPowerUpsModel.size())
                .forEach(i -> {
                    ImageView iv = (ImageView) myPowerUps.getChildren().get(i);
                    iv.setVisible(true);
                    iv.setOpacity(1.0);
                    iv.setDisable(false);

                    setCardSelectionBehavior(iv, myPowerUps, playerAction, () -> {});
                });
    }

    /**
     * The showPowerUps method shows only the requested powerUps for selection. It shows the requested powerUps and
     * hides the others ones.
     *
     * @param powerUpList is the list of powerUps that are asked for input.
     */
    public void showPowerUps(List<String> powerUpList) {
        CharacterState myCharacterState =  view.getModel().getGame().getPlayerByColor(view.getPlayerColor()).getCharacterState();
        List<PowerUp> myPowerUpsModel = myCharacterState.getPowerUpBag();

        cancelButton.setDisable(false);
        myPowerUps.setDisable(false);
        if (!myPowerUpsModel.isEmpty()) {
            myPowerUps.getStyleClass().add(Constants.SELECTION_NODE);
        }
        myPowerUps.getChildren().forEach(node -> {
            node.setDisable(true);
            node.setOpacity(CLICKED_OPACITY);
        });
        myPowerUps.getChildren().stream().map(n -> (ImageView) n)
                .filter(iv -> powerUpList.contains(((NamedImage)iv.getImage()).getName().split("_")[1]))
                .forEach(iv -> {
                    iv.setOpacity(1.0);
                    iv.setDisable(false);
                    iv.getStyleClass().add(CSS_HOVERING);
                    setCardSelectionBehavior(iv, myPowerUps, POWERUP, () -> {
                        PowerUp powerUp = myPowerUpsModel.stream().filter(pU -> pU.getName().equals(((NamedImage)iv.getImage()).getName())).findFirst().orElse(null);
                        if (powerUp != null) {
                            iv.setVisible(true);
                            if (powerUp.getActionUnitList().get(ACTIONUNIT_POSITION).getNumPlayerTargets() > 0) {
                                view.getInputRequested().add(() -> getTarget(POWERUP, powerUp.getActionUnitList().get(ACTIONUNIT_POSITION).getNumPlayerTargets()));
                            }
                            if (powerUp.getActionUnitList().get(ACTIONUNIT_POSITION).getNumTileTargets() > 0) {
                                view.getInputRequested().add(() -> getShootTile(POWERUP, powerUp.getActionUnitList().get(ACTIONUNIT_POSITION).getNumTileTargets()));
                            }
                            if (powerUp.getId().split("_")[1].equals(TARGETING_SCOPE)) {
                                view.getInputRequested().add(this::getAmmo);
                            }
                        }
                        view.askInput();
                    });
                });
    }

    /**
     * The getAmmo method shows the player's ammo for selection. It is needed when a targeting scope powerUp is
     * used.
     *
     */
    public void getAmmo() {
        infoText.setText("Select 1 ammo");
        myAmmo.getStyleClass().add(SELECTION_NODE);
        myAmmo.getChildren().forEach(node -> {
            node.setDisable(false);
            node.getStyleClass().add(CSS_HOVERING);
        });
        myAmmo.getChildren().get(0).setOnMouseClicked(event ->  {
            myAmmo.getChildren().get(0).setDisable(true);
            myAmmo.getChildren().get(0).setOpacity(Constants.CLICKED_OPACITY);

            Util.ifFirstSelection(confirmButton, progressBar);
            Util.updateCircle(progressBar);

            if (Util.isLastSelection(progressBar)) {
                myAmmo.setDisable(true);
            }
            addInput(POWERUP, "BLUE");
        });
        myAmmo.getChildren().get(1).setOnMouseClicked(event ->  {
            myAmmo.getChildren().get(1).setDisable(true);
            myAmmo.getChildren().get(1).setOpacity(Constants.CLICKED_OPACITY);

            Util.ifFirstSelection(confirmButton, progressBar);
            Util.updateCircle(progressBar);

            if (Util.isLastSelection(progressBar)) {
                myAmmo.setDisable(true);
            }
            addInput(POWERUP, "RED");
        });
        myAmmo.getChildren().get(2).setOnMouseClicked(event ->  {
            myAmmo.getChildren().get(2).setDisable(true);
            myAmmo.getChildren().get(2).setOpacity(Constants.CLICKED_OPACITY);

            Util.ifFirstSelection(confirmButton, progressBar);
            Util.updateCircle(progressBar);

            if (Util.isLastSelection(progressBar)) {
                myAmmo.setDisable(true);
            }
            addInput(POWERUP, "YELLOW");
        });

        setUpProgressBar(1);
    }

    /**
     * The resetAmmoStyle method resets the style of the player's ammo.
     *
     */
    public void resetAmmoStyle() {
        myAmmo.setDisable(false);
        if (!myAmmo.getStyleClass().isEmpty()) {
            myAmmo.getStyleClass().remove(0);
        }
        myAmmo.getChildren().forEach(node -> {
            node.setOpacity(1.0);
            node.setDisable(false);
            if (!node.getStyleClass().isEmpty()) {
                node.getStyleClass().remove(0);
            }
        });
    }


    /**
     * The showPass method enables the pass button selection to skip an action.
     *
     */
    public void showPass() {
        pass.setDisable(false);
    }
}
