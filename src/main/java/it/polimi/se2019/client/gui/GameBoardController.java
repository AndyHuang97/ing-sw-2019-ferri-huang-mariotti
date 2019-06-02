package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.NamedImage;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class GameBoardController {

    private static final Logger logger = Logger.getLogger(GameBoardController.class.getName());

    private MainApp mainApp;
    private List<PlayerBoardController> pbControllerList;
    private MapController mapController;
    private Map<String, List<String>> intermediateInput = new HashMap<>();

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

    /**
     * The main game board initializer which is called when the GameBoard.fxml file is loaded.
     */
    @FXML
    private void initialize() {
        pbControllerList = new ArrayList<>();
    }

    /**
     *  Is called by the main application to set itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Initializes all parameters.
     *
     */
    public void init(Player player) {
        setInfoPaneStyle();
        initMap();
        initPlayerBoards(player);
        initMyCards();

        showMyCards();
        //showRanking();
    }

    /**
     * Initializes the map by loading the fxml containing the decorated map.
     *
     */
    public void initMap() {

        try {
            FXMLLoader mloader = new FXMLLoader();
            mloader.setLocation(getClass().getResource("/fxml/Map.fxml"));
            AnchorPane decoratedMap = (AnchorPane) mloader.load();
            mapController = mloader.getController();
            mapController.setMainApp(mainApp);

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
     * Initializes the players' boards with all their info.
     *
     * @param player is the client.
     */
    public void initPlayerBoards(Player player) {
        Font.loadFont(PlayerBoardController.class.getResource("/css/sofachromerg.ttf").toExternalForm(),10);

        AnchorPane playerBoardPane;
        int i = 0;
        for (PlayerColor pc : mainApp.getGame().getActiveColors()) {
            try {
                FXMLLoader playerLoader = new FXMLLoader();
                playerLoader.setLocation(getClass().getResource("/fxml/PlayerBoard.fxml"));
                // gets the decorated pane
                AnchorPane decoratedPane = playerLoader.load();

                PlayerBoardController playerController = playerLoader.getController();
                pbControllerList.add(playerController);
                playerController.setMainApp(mainApp);
                playerController.setPlayerColor(pc);
                playerController.initMarkerPane(pc);

                if (pc != player.getColor()) {
                    // gets the anchorPane containing the imageview
                    AnchorPane box = (AnchorPane) rightVBox.getChildren().get(i);
                    box.setVisible(true);
                    playerBoardPane = (AnchorPane) box.getChildren().get(1);

                    HBox scoreBox = (HBox)((AnchorPane)rightVBox.getChildren().get(i)).getChildren().get(2);
                    HBox nameBox = (HBox)((AnchorPane)rightVBox.getChildren().get(i)).getChildren().get(3);
                    GridPane ammoPane = (GridPane)((AnchorPane)rightVBox.getChildren().get(i)).getChildren().get(4);

                    showName(mainApp.getGame().getPlayerByColor(pc), (Label) nameBox.getChildren().get(0));
                    showScore(mainApp.getGame().getPlayerByColor(pc),(Label) scoreBox.getChildren().get(0));
                    showAmmo(mainApp.getGame().getPlayerByColor(pc), ammoPane);
                    showOpponentUnloadedWeapons(mainApp.getGame().getPlayerByColor(pc), i);
                    i++;
                }else {
                    playerBoardPane = playerBoard;
                    showActionButtons(player);

                    showName(player, myName);
                    showAmmo(player, myAmmo);
                    showScore(player, myScore);
                }

                // removes the static image view and loads the decorated one
                playerBoardPane.getChildren().remove(0);
                playerBoardPane.getChildren().add(decoratedPane);


                playerController.showPlayerBoard(mainApp.getGame().getPlayerByColor(pc));
                playerController.showDamageBar(mainApp.getGame().getPlayerByColor(pc));
                playerController.showMarkerBar(mainApp.getGame().getPlayerByColor(pc));
                playerController.showSkullBar(mainApp.getGame().getPlayerByColor(pc));
                playerController.showActionTile(mainApp.getGame().getPlayerByColor(pc));


            }
            catch (IOException e) {
                logger.warning(e.toString());
            }
        }
    }

    /**
     * Initialize the client's cards to respond to mouse click events.
     *
     */
    public void initMyCards() {
        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();

        Arrays.asList(myPowerUps,myWeapons).stream()
                .forEach(myCards -> {
                    myCards.setDisable(true);
                    myCards.getChildren().stream()
                            .map(n -> (ImageView) n)
                            .forEach(c -> {
                                c.setVisible(false);
                                c.setOnMouseClicked(event -> {
                                    c.setOpacity(0.6);

                                    Util.ifFirstSelection(root, progressBar);
                                    Util.updateCircle(progressBar);

                                    if(Util.isLastSelection(progressBar)) {
                                        myCards.setDisable(true);
                                    }
                                    NamedImage image = (NamedImage) c.getImage();

                                    addInput(Constants.CARD, image.getName());
                                });
                            });
                });
    }

    /**
     * Sets the style of info panes.
     *
     */
    public void setInfoPaneStyle() {
        infoPane.getStyleClass().add("info-pane");
        //rankingGrid.getStyleClass().add("info-pane");
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
        List<Weapon> weaponBag =player.getCharacterState().getWeapoonBag();
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
    public void showActionButtons(Player player) {
        try {
            AnchorPane buttonedPane = null;
            FXMLLoader loader = new FXMLLoader();
            String gameMode = mainApp.getGame().isFrenzy() ? Constants.FRENZY : Constants.NORMAL;

            loader.setLocation(getClass().getResource(Constants.ACTION_BUTTONS + gameMode.split("_")[1] + ".fxml"));
            buttonedPane = loader.load();

            ActionTileController atController = loader.getController();
            atController.setMainApp(mainApp);
            atController.init();

            actionButtons.getChildren().add(buttonedPane);
            ((GridPane) buttonedPane.getChildren().get(0)).getChildren().stream()
                    .map(n -> (Button) n)
                    .forEach(b -> b.setMaxSize(31.0, 1.0));

            // shows adrenaline buttons
            if (Util.getCorrectPlayerBoardMode(player).equalsIgnoreCase(Constants.NORMAL)) {
                Button mmg = new Button("");
                Button ms = new Button("");

                mmg.setOpacity(0.45);
                mmg.setLayoutX(98);
                mmg.setLayoutY(20);
                mmg.setPrefSize(30.0, 16.0);

                ms.setOpacity(0.45);
                ms.setLayoutX(178);
                ms.setLayoutY(20);
                ms.setPrefSize(30.0, 16.0);

                mmg.setOnAction(event -> {
                    mainApp.getInputRequested().add(() -> System.out.println(mmg));
                    mainApp.getInput();

                });

                ms.setOnAction(event -> {
                    mainApp.getInputRequested().add(() -> System.out.println(ms));
                    mainApp.getInput();

                });

                actionButtons.getChildren().add(mmg);
                actionButtons.getChildren().add(ms);
            }

        }catch (IOException e) {
            logger.warning(e.toString());
        }
    }

    /**
     * Plainly shows the client player's weapon and powerup cards.
     *
     */
    public void showMyCards() {
        CharacterState myCharacterState =  mainApp.getGame().getPlayerList().stream()
                .filter(p -> p.getColor() == mainApp.getPlayerColor())
                .collect(Collectors.toList()).get(0).getCharacterState();
        List<Weapon> myWeaponsModel = myCharacterState.getWeapoonBag();
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
                        iv.setOpacity(0.6);
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
     * Show the player's raking with their scores.
     *
     */
    public void showRanking() {
        List<Player> ranking = mainApp.getGame().getRanking();
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
                .forEach(k -> mainApp.getPlayerInput().put(k, intermediateInput.get(k)));
        if (mainApp.getInputRequested().isEmpty())
        {
            mainApp.sendInput();
            handleCancel();
        }
        else {
            handleReset();
            mainApp.getInput();

        }
    }

    /**
     * Handles the cancel button, it refreshes the interface to a state prior to the input
     * selection.
     *
     */
    @FXML
    public void handleCancel() {
        handleReset();
        mainApp.getInputRequested().clear();
        intermediateInput.clear();
    }

    public void handleReset() {
        progressBar.getChildren().stream()
                .map(n -> (Circle) n)
                .forEach(c -> {
                    c.setFill(Paint.valueOf("white"));
                    c.setVisible(false);
                });
        infoText.setText("Select an action("+mainApp.getActionNumber()+")");
        cancelButton.setDisable(true);
        confirmButton.setDisable(true);

        showMyCards();

        enableActionButtons();
        mapController.resetGrids();
    }

    /**
     * Enables the selection of the action buttons.
     *
     */
    public void enableActionButtons() {
        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();
        // if it's not necessary to control specific buttons, it is wiser to get their container
        if (mainApp.getGame().isFrenzy()) {
            root.getCenter().lookup("#mrs").setDisable(false);
            root.getCenter().lookup("#mmmm").setDisable(false);
            root.getCenter().lookup("#mmg").setDisable(false);
            root.getCenter().lookup("#mmrs").setDisable(false);
            root.getCenter().lookup("#mmmg").setDisable(false);
        }
        else {
            root.getCenter().lookup("#mmm").setDisable(false);
            root.getCenter().lookup("#mg").setDisable(false);
            root.getCenter().lookup("#s").setDisable(false);
            root.getCenter().lookup("#r").setDisable(false);
        }
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

    public void addInput(String key, String id) {
        intermediateInput.putIfAbsent(key, new ArrayList<>());
        intermediateInput.get(key).add(id);
        System.out.println("Added: " + key + " " + id);
    }
}
