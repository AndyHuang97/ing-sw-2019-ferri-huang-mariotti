package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class GameBoardController {

    private static final Logger logger = Logger.getLogger(GameBoardController.class.getName());

    private MainApp mainApp;
    private List<PlayerBoardController> pbControllerList;
    private MapController mapController;

    @FXML
    private VBox leftVbox;
    @FXML
    private AnchorPane map;
    @FXML
    private AnchorPane playerBoard;
    @FXML
    private VBox opponents;
    @FXML
    private Label infoText;
    @FXML
    private GridPane progressBar;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private GridPane myWeapons;
    @FXML
    private GridPane myPowerups;

    /**
     * The main game board initializer.
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
     * Initializes all parameters
     */
    public void init(PlayerColor playerColor) {
        initMap();
        initPlayerBoards(playerColor);
        initMyCards();
    }

    /**
     * Initializes the map.
     */
    public void initMap() {

        try {
            FXMLLoader mloader = new FXMLLoader();
            mloader.setLocation(getClass().getResource("/fxml/Map.fxml"));
            AnchorPane decoratedMap = (AnchorPane) mloader.load();
            mapController = mloader.getController();
            mapController.setMainApp(mainApp);

            // removes the old anchor and adds the new one
            leftVbox.getChildren().remove(0);
            leftVbox.getChildren().add(0, decoratedMap);
            mapController.handleMapLoading();
        } catch (IOException e) {
            logger.warning("Error loading map.");
        }
    }

    /**
     * Initializes the players' boards.
     */
    public void initPlayerBoards(PlayerColor playerColor) {

        AnchorPane targetPane;
        int i = 0;
        for (PlayerColor pc : PlayerColor.values()) {
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

                if (pc != playerColor) {
                    // gets the anchorPane containing the imageview
                    AnchorPane box = (AnchorPane) opponents.getChildren().get(i);
                    targetPane = (AnchorPane) box.getChildren().get(1);

                    i++;
                }else {
                    targetPane = playerBoard;
                    playerController.addActionTileButtons(playerColor, Constants.NORMAL.split("_")[1]);
                }
                // removes the static image view and loads the decorated one
                targetPane.getChildren().remove(0);
                targetPane.getChildren().add(decoratedPane);
                playerController.initPlayerBoard(pc);
            }
            catch (IOException e) {
                logger.warning("Error loading player boards");
            }
        }
    }

    public void initMyCards() {
        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();
        GridPane progressBar = (GridPane) (root.getCenter()).lookup("#progressBar");
        Arrays.asList(myPowerups,myWeapons).stream()
                .forEach(myCards -> {
                    myCards.setDisable(true);
                    myCards.getChildren().stream()
                            .forEach(w -> {
                                //w.setVisible(false);
                                w.setOnMouseClicked(event -> {
                                    myWeapons.setDisable(true);
                                    w.setOpacity(0.6);

                                    Util.isFirstSelection(root, progressBar);
                                    Util.updateCircle(progressBar);

                                    mainApp.handleCardSelection();
                                });
                            });
                });
    }

    @FXML
    public void handleConfirm() {
        mainApp.sendInput();
        handleCancel();
    }

    @FXML
    public void handleCancel() {
        progressBar.getChildren().stream()
                .map(n -> (Circle) n)
                .forEach(c -> {
                    c.setFill(Paint.valueOf("white"));
                    c.setVisible(false);
                });
        infoText.setText("Select an action("+mainApp.getActionNumber()+")");
        cancelButton.setDisable(true);
        confirmButton.setDisable(true);

        Arrays.asList(myPowerups,myWeapons).stream()
                .forEach(myCards -> {
                    if (!myCards.getStyleClass().isEmpty()) {
                        myCards.getStyleClass().remove(0);
                    }
                    myCards.getChildren().stream()
                            .forEach(w ->
                                    w.setOpacity(1.0)
                            );
                });

        enableActionButtons();
        mapController.disableGrids();
    }

    public void enableActionButtons() {
        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();
        // if it's not necessary to control specific buttons, it is wiser to get their container
        root.getCenter().lookup("#mmm").setDisable(false);
        root.getCenter().lookup("#mg").setDisable(false);
        root.getCenter().lookup("#s").setDisable(false);
        root.getCenter().lookup("#r").setDisable(false);
    }

    /**
     * Getter for the player's board.
     * @return player board the player.
     */
    public AnchorPane getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Setter for the player's board.
     * @param playerBoard is the new player's board.
     */
    public void setPlayerBoard(AnchorPane playerBoard) {
        this.playerBoard = playerBoard;
    }

    /**
     * Getter for the map of the game.
     * @return the map of the game.
     */
    public AnchorPane getMap() {
        return map;
    }

    /**
     * Setter for the map of the game.
     * @param map is the new map of the game.
     */
    public void setMap(AnchorPane map) {
        this.map = map;
    }

    /**
     * Getter for the left Vbox of the game board.
     * @return left Vbox of the game board.
     */
    public VBox getLeftVbox() {
        return leftVbox;
    }

    /**
     * Gets the list of the player boards' controller.
     * @return list of player boards' controller
     */
    public List<PlayerBoardController> getPbControllerList() {
        return pbControllerList;
    }

    /**
     * Gets the player board corresponding to the player's color
     * @param playerColor the player's color associated to the board
     * @return the player board with the correct player color
     */
    public PlayerBoardController getPlayerBoardController(PlayerColor playerColor) {
        Optional<PlayerBoardController> optional = getPbControllerList().stream()
                .filter(pc -> pc.getPlayerColor() == playerColor)
                .findFirst();

        return optional.orElse(null);
    }
}
